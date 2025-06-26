package tw.amer.cia.core.component.structural.aspect;

import tw.amer.cia.core.common.GeneralSetting;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Aspect
@Slf4j
@Component
public class OptimisticLockingAspect {

    @Value("${coriander-ingress-api.setting.deploy-type}")
    private String deploymentMode;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.save*(..)) || " +
            "execution(* org.springframework.data.jpa.repository.JpaRepository+.update*(..)) || " +
            "execution(* org.springframework.data.repository.CrudRepository+.save*(..))")
    public Object handleOptimisticLocking(ProceedingJoinPoint pjp) throws Throwable {
        // 僅在CLIENT模式下進行處理
        if (StringUtils.equalsIgnoreCase(deploymentMode, GeneralSetting.CiaDeployType.HOST.getDisplayName())) {
            return pjp.proceed();
        }

        try {
            return pjp.proceed();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("遇到樂觀鎖衝突，嘗試強制更新: {}", e.getMessage());

            // 使用事務模板確保事務完整性
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            return template.execute(status -> {
                try {
                    return forceUpdate(pjp);
                } catch (Throwable ex) {
                    log.error("強制更新失敗", ex);
                    status.setRollbackOnly();
                    throw new RuntimeException("Force update failed", ex);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private Object forceUpdate(ProceedingJoinPoint pjp) throws Throwable {
        Object entity = pjp.getArgs()[0];

        // 處理集合類型的參數
        if (entity instanceof Iterable) {
            List<Object> updatedEntities = new ArrayList<>();
            for (Object item : (Iterable<Object>) entity) {
                updatedEntities.add(forceUpdateEntity(item));
            }
            return updatedEntities;
        } else {
            // 處理單個實體
            return forceUpdateEntity(entity);
        }
    }

    private Object forceUpdateEntity(Object entity) {
        try {
            // 1. 獲取實體的類
            Class<?> entityClass = entity.getClass();

            // 2. 檢查是否使用複合主鍵
            boolean hasIdClass = entityClass.isAnnotationPresent(IdClass.class);
            boolean hasEmbeddedId = hasEmbeddedIdField(entityClass);

            // 3. 根據主鍵類型獲取ID對象
            Object entityId;
            if (hasIdClass) {
                entityId = getIdClassInstance(entity);
            } else if (hasEmbeddedId) {
                entityId = getEmbeddedIdValue(entity);
            } else {
                entityId = getSingleIdValue(entity);
            }

            if (entityId == null) {
                log.info("無法獲取實體ID或是新實體，直接保存: {}", entityClass.getSimpleName());
                return entityManager.merge(entity);
            }

            // 4. 從數據庫獲取最新實體
            entityManager.clear(); // 清除一級緩存
            Object databaseEntity = entityManager.find(entityClass, entityId);

            if (databaseEntity == null) {
                log.info("資料庫中找不到ID為{}的實體{}，直接保存", entityId, entityClass.getSimpleName());
                return entityManager.merge(entity);
            }

            // 5. 獲取需要排除的版本字段名
            Set<String> versionFieldNames = getVersionFieldNames(entityClass);

            log.debug("實體 {} 的版本字段為: {}", entityClass.getSimpleName(), versionFieldNames);

            // 6. 複製除版本字段以外的所有非空字段
            copyNonNullPropertiesExcludeFields(entity, databaseEntity, versionFieldNames);

            // 7. 保存並返回更新後的實體
            Object savedEntity = entityManager.merge(databaseEntity);
            entityManager.flush();

            log.info("成功強制更新實體: {}，ID: {}", entityClass.getSimpleName(), entityId);
            return savedEntity;

        } catch (Exception ex) {
            log.error("強制更新實體時發生錯誤: {}", ex.getMessage(), ex);
            throw new RuntimeException("強制更新實體失敗", ex);
        }
    }

    /**
     * 檢查類是否使用了@EmbeddedId
     */
    private boolean hasEmbeddedIdField(Class<?> entityClass) {
        for (Field field : getAllFields(entityClass)) {
            if (field.isAnnotationPresent(EmbeddedId.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 獲取使用@IdClass註解的複合主鍵實例
     */
    private Object getIdClassInstance(Object entity) throws Exception {
        Class<?> entityClass = entity.getClass();
        IdClass idClassAnnotation = entityClass.getAnnotation(IdClass.class);

        if (idClassAnnotation == null) {
            return null;
        }

        Class<?> idClass = idClassAnnotation.value();
        Constructor<?> constructor = idClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object idInstance = constructor.newInstance();

        // 查找所有帶有@Id註解的字段，並從實體中複製值到ID類實例中
        for (Field field : getAllFields(entityClass)) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = field.get(entity);

                try {
                    Field idField = idClass.getDeclaredField(fieldName);
                    idField.setAccessible(true);
                    idField.set(idInstance, fieldValue);
                } catch (NoSuchFieldException e) {
                    log.warn("ID類 {} 中沒有找到字段 {}", idClass.getName(), fieldName);
                }
            }
        }

        return idInstance;
    }

    /**
     * 獲取使用@EmbeddedId註解的嵌入式ID值
     */
    private Object getEmbeddedIdValue(Object entity) throws Exception {
        for (Field field : getAllFields(entity.getClass())) {
            if (field.isAnnotationPresent(EmbeddedId.class)) {
                field.setAccessible(true);
                return field.get(entity);
            }
        }
        return null;
    }

    /**
     * 獲取單一ID字段的值
     */
    private Object getSingleIdValue(Object entity) throws Exception {
        for (Field field : getAllFields(entity.getClass())) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return field.get(entity);
            }
        }

        // 嘗試通過getId方法獲取
        try {
            Method getIdMethod = entity.getClass().getMethod("getId");
            return getIdMethod.invoke(entity);
        } catch (NoSuchMethodException e) {
            log.debug("實體 {} 沒有getId方法", entity.getClass().getSimpleName());
        }

        return null;
    }

    /**
     * 獲取實體所有的版本字段名稱
     */
    private Set<String> getVersionFieldNames(Class<?> entityClass) {
        Set<String> versionFieldNames = new HashSet<>();

        // 查找所有標記了@Version註解的字段
        for (Field field : getAllFields(entityClass)) {
            if (field.isAnnotationPresent(Version.class)) {
                versionFieldNames.add(field.getName());
            }
        }

        // 如果沒有找到@Version字段，可以添加一些常見的版本字段名稱
        if (versionFieldNames.isEmpty()) {
            versionFieldNames.add("version");
            versionFieldNames.add("versionNumber");
            versionFieldNames.add("optLockVersion");
            versionFieldNames.add("lmTime");
        }

        return versionFieldNames;
    }

    /**
     * 複製非空屬性，排除指定的字段
     */
    private void copyNonNullPropertiesExcludeFields(Object source, Object target, Set<String> excludeFields) {
        final BeanWrapper srcWrapper = new BeanWrapperImpl(source);
        final BeanWrapper trgWrapper = new BeanWrapperImpl(target);

        for (PropertyDescriptor propertyDescriptor : srcWrapper.getPropertyDescriptors()) {
            String propertyName = propertyDescriptor.getName();

            // 跳過需要排除的字段
            if (excludeFields.contains(propertyName)) {
                log.debug("跳過版本字段: {}", propertyName);
                continue;
            }

            Object providedValue = srcWrapper.getPropertyValue(propertyName);

            // 檢查是否可寫入（是否存在setter方法）及值是否為非空
            if (trgWrapper.isWritableProperty(propertyName) && providedValue != null) {
                try {
                    PropertyDescriptor trgDescriptor = trgWrapper.getPropertyDescriptor(propertyName);
                    // 判斷來源和目標屬性的數據類型是否相同
                    if (propertyDescriptor.getPropertyType().equals(trgDescriptor.getPropertyType())) {
                        trgWrapper.setPropertyValue(propertyName, providedValue);
                        log.debug("複製字段: {} = {}", propertyName, providedValue);
                    } else {
                        log.debug("字段類型不匹配，跳過: {} ({} -> {})",
                                propertyName,
                                propertyDescriptor.getPropertyType().getSimpleName(),
                                trgDescriptor.getPropertyType().getSimpleName());
                    }
                } catch (Exception e) {
                    log.warn("複製字段 {} 時出錯: {}", propertyName, e.getMessage());
                }
            }
        }
    }

    /**
     * 獲取類及其所有父類的所有字段
     */
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;

        // 遍歷類的層次結構，獲取所有字段
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                fields.add(field);
            }
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }
}
