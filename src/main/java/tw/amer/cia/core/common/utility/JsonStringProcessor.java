package tw.amer.cia.core.common.utility;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class JsonStringProcessor {
    private static ObjectMapper mapper = new ObjectMapper();

    public static String jsonObjectParser(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "ERROR WHILE PARSING JSON.";
        }
    }

    public static String removeTagsIncludEmptyParentFromJsonString(String jsonString, Set<String> tagsToRemove) throws DataSourceAccessException {
        try {
            Map<String, Object> dataMap = mapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });
            removeTags(dataMap, tagsToRemove);
            return mapper.writeValueAsString(dataMap);
        } catch (IOException e) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorConstantLib.JSON_STRING_PROCESSOR_ERROR_ON_REMOVE_TAGS.getCompleteMessage()
            );
        }
    }

    public static String removeTagsFromJsonString(String jsonString, Set<String> tagsToRemove) throws DataSourceAccessException {
        try {
            Map<String, Object> dataMap = mapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
            removeTagsAndSetParentNullIfEmpty(dataMap, tagsToRemove);
            return mapper.writeValueAsString(dataMap);
        } catch (IOException e) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorConstantLib.JSON_STRING_PROCESSOR_ERROR_ON_REMOVE_TAGS.getCompleteMessage()
            );
        }
    }

    private static boolean removeTags(Map<String, Object> map, Set<String> tagsToRemove) {
        boolean isChanged = false;
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if (tagsToRemove.contains(entry.getKey())) {
                it.remove();
                isChanged = true;
            } else if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                boolean childChanged = removeTags((Map<String, Object>) entry.getValue(), tagsToRemove);
                if (childChanged && ((Map<?, ?>) entry.getValue()).isEmpty()) {
                    it.remove();
                    isChanged = true;
                } else if (childChanged) {
                    isChanged = true;
                }
            }
        }

        return isChanged;
    }

    private static boolean removeTagsAndSetParentNullIfEmpty(Map<String, Object> map, Set<String> tagsToRemove) {
        boolean isChanged = false;
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if (tagsToRemove.contains(entry.getKey())) {
                it.remove();
                isChanged = true;
            } else if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                boolean childChanged = removeTagsAndSetParentNullIfEmpty((Map<String, Object>) entry.getValue(), tagsToRemove);
                if (childChanged) {
                    isChanged = true;
                    if (((Map<?, ?>) entry.getValue()).isEmpty()) {
                        map.put(entry.getKey(), null); // Set parent map value to null if child map is empty
                    }
                }
            }
        }
        // Check if the map itself has become empty and return true if it has changed
        if (map.isEmpty()) {
            return true;
        }
        return isChanged;
    }

    // 僅提取指定tag的內容
    public static String extractTagOnly(String json, String tagToExtract) {
        try {
            Map<String, Object> dataMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
            Map<String, Object> result = new LinkedHashMap<>();

            findAndExtractTagOnly(dataMap, tagToExtract, result);

            return result.isEmpty() ? "{}" : mapper.writeValueAsString(result);
        } catch (IOException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }

    // 提取包含父節點和兄弟節點的內容
    public static String extractTagWithParent(String json, String tagToExtract) {
        return extractTagWithParent(json, tagToExtract, 1);
    }

    public static String extractTagWithParent(String json, String tagToExtract, int parentLevels) {
        try {
            Map<String, Object> dataMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
            ResultContainer resultContainer = new ResultContainer();

            findAndExtractTagWithParent(dataMap, tagToExtract, parentLevels, 0, resultContainer);

            return resultContainer.result.isEmpty() ? "{}" : mapper.writeValueAsString(resultContainer.result);
        } catch (IOException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }

    private static boolean findAndExtractTagOnly(Map<String, Object> map, String tagToFind, Map<String, Object> result) {
        // 直接檢查當前層級是否包含目標tag
        if (map.containsKey(tagToFind)) {
            result.put(tagToFind, map.get(tagToFind));
            return true;
        }

        // 遍歷所有值，檢查是否有嵌套的Map
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();
                if (findAndExtractTagOnly(innerMap, tagToFind, result)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static class ResultContainer {
        Map<String, Object> result = new LinkedHashMap<>();
        int foundAtLevel = -1;
    }

    private static boolean findAndExtractTagWithParent(Map<String, Object> map,
                                                       String tagToFind,
                                                       int parentLevels,
                                                       int currentLevel,
                                                       ResultContainer resultContainer) {
        if (map.containsKey(tagToFind)) {
            resultContainer.foundAtLevel = currentLevel;
            resultContainer.result = new LinkedHashMap<>(map);
            return true;
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();
                if (findAndExtractTagWithParent(innerMap, tagToFind, parentLevels, currentLevel + 1, resultContainer)) {
                    // 如果找到目標，且當前層級在指定的父層數範圍內
                    if (resultContainer.foundAtLevel - currentLevel <= parentLevels) {
                        Map<String, Object> newResult = new LinkedHashMap<>();
                        newResult.put(entry.getKey(), resultContainer.result);
                        resultContainer.result = newResult;
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
