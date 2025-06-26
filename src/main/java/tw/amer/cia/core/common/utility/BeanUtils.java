package tw.amer.cia.core.common.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;

@Slf4j
public class BeanUtils extends org.springframework.beans.BeanUtils {
    public static void copyNonNullProperties(Object src, Object target) {
        final BeanWrapper srcWrapper = new BeanWrapperImpl(src);
        final BeanWrapper trgWrapper = new BeanWrapperImpl(target);

        for (PropertyDescriptor propertyDescriptor : srcWrapper.getPropertyDescriptors()) {
            String propertyName = propertyDescriptor.getName();
            Object providedObject = srcWrapper.getPropertyValue(propertyName);

            // 檢查是否可寫入（是否存在setter方法）
            if (trgWrapper.isWritableProperty(propertyName) && providedObject != null) {
                PropertyDescriptor trgDescriptor = trgWrapper.getPropertyDescriptor(propertyName);
                // 判斷來源和目標屬性的資料型別是否相同
                if (providedObject != null && propertyDescriptor.getPropertyType().equals(trgDescriptor.getPropertyType())) {
                    trgWrapper.setPropertyValue(propertyName, providedObject);
                } else {
                    log.debug("On Coping {} to {}, Found unmatchable type: {} to {}. Ignore Property: {}",
                            src.getClass().getSimpleName(),
                            target.getClass().getSimpleName(),
                            propertyDescriptor.getPropertyType().getSimpleName(),
                            trgDescriptor.getPropertyType().getSimpleName(),
                            propertyName
                    );
                }
            }
        }
    }
}
