package tw.amer.cia.core.common.utility;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PageableUtils {

    /**
     * 將List轉換為分頁對象
     * @param list 原始列表
     * @param pageable 分頁參數
     * @param <T> 泛型類型
     * @return 分頁後的Page對象
     */
    public static <T> Page<T> convertListToPage(List<T> list, Pageable pageable) {
        if (list == null || list.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<T> sortedList = list.stream()
                .sorted((item1, item2) -> compareObjects(item1, item2, pageable.getSort()))
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        return new PageImpl<>(sortedList, pageable, list.size());
    }

    /**
     * 比較兩個對象的指定屬性
     * @param item1 第一個對象
     * @param item2 第二個對象
     * @param sort 排序參數
     * @param <T> 泛型類型
     * @return 比較結果
     */
    @SuppressWarnings("unchecked")
    private static <T> int compareObjects(T item1, T item2, Sort sort) {
        for (Sort.Order order : sort) {
            try {
                // 獲取屬性字段
                Field field = getField(item1.getClass(), order.getProperty());
                if (field == null) continue;

                field.setAccessible(true);
                Comparable<Object> value1 = (Comparable<Object>) field.get(item1);
                Comparable<Object> value2 = (Comparable<Object>) field.get(item2);

                // 處理null值
                if (value1 == null && value2 == null) continue;
                if (value1 == null) return order.isAscending() ? -1 : 1;
                if (value2 == null) return order.isAscending() ? 1 : -1;

                // 比較值
                int comparison = value1.compareTo(value2);
                if (comparison != 0) {
                    return order.isAscending() ? comparison : -comparison;
                }
            } catch (Exception e) {
                // 如果出現異常，繼續下一個屬性的比較
                continue;
            }
        }
        return 0;
    }

    /**
     * 遞歸查找類中的字段，包括父類
     * @param type 類型
     * @param fieldName 字段名
     * @return 字段對象
     */
    private static Field getField(Class<?> type, String fieldName) {
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superclass = type.getSuperclass();
            if (superclass != null) {
                return getField(superclass, fieldName);
            }
        }
        return null;
    }
}
