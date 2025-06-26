package tw.amer.cia.core.model.pojo.service.host.web.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_BasicSiteItemAuthedDto implements Serializable {

    @JsonProperty("SITE_NAME")
    String siteName;

    @JsonProperty("FAB_LIST_AND_AUTHED_STATUS")
    Map<String, Boolean> fabMap;


    public static List<Web_BasicSiteItemAuthedDto> importFromDeployedFabAndAuthorizedFab(Map<String, List<String>> fabIdListBySite, Collection<String> authedFabList) {
        List<Web_BasicSiteItemAuthedDto> newList = new ArrayList<>();
        for (String site : fabIdListBySite.keySet()) {
            Map<String, Boolean> fabAuthedMap = new HashMap<>();
            for (String fabId : fabIdListBySite.get(site)) {
                if(CollectionUtils.isNotEmpty(authedFabList)){
                    fabAuthedMap.put(fabId, authedFabList.contains(fabId));
                }else{
                    fabAuthedMap.put(fabId, Boolean.FALSE);
                }
            }

            newList.add(Web_BasicSiteItemAuthedDto.builder()
                    .siteName(site)
                    .fabMap(fabAuthedMap)
                    .build());
        }
        return newList;
    }

    public static Map<String, Boolean> getChangedByFabIdMap(Web_BasicSiteItemAuthedDto source, Web_BasicSiteItemAuthedDto onCompare) {
        // 處理 null 情況
        if (source == null || onCompare == null) {
            return new HashMap<>();
        }

        // 檢查 siteName 是否一致
        if (!Objects.equals(source.getSiteName(), onCompare.getSiteName())) {
            return new HashMap<>();
        }

        // 取得兩個 fabMap，如果任一為 null，返回空 Map
        Map<String, Boolean> sourceFabMap = source.getFabMap();
        Map<String, Boolean> compareFabMap = onCompare.getFabMap();
        if (sourceFabMap == null || compareFabMap == null) {
            return new HashMap<>();
        }

        // 檢查 keySet 是否一致
        if (!sourceFabMap.keySet().equals(compareFabMap.keySet())) {
            return new HashMap<>();
        }

        // 比對差異並存入結果
        Map<String, Boolean> result = new HashMap<>();
        for (String key : sourceFabMap.keySet()) {
            Boolean sourceValue = sourceFabMap.get(key);
            Boolean compareValue = compareFabMap.get(key);

            if (!Objects.equals(sourceValue, compareValue)) {
                result.put(key, compareValue);
            }
        }

        return result;
    }


    public static boolean checkStructureEquals(Collection<Web_BasicSiteItemAuthedDto> a, Collection<Web_BasicSiteItemAuthedDto> b) {
        // 如果其中一個是 null，只有兩個都是 null 時才相等
        if (a == null || b == null) {
            return a == b;
        }

        // 如果大小不同，直接返回 false
        if (a.size() != b.size()) {
            return false;
        }

        // 檢查是否有重複的 siteName
        Set<String> siteNamesA = new HashSet<>();
        Set<String> siteNamesB = new HashSet<>();

        for (Web_BasicSiteItemAuthedDto itemA : a) {
            if (!siteNamesA.add(itemA.getSiteName())) {
                throw new IllegalArgumentException("Site name repeated! ");
            }
        }

        for (Web_BasicSiteItemAuthedDto itemB : b) {
            if (!siteNamesB.add(itemB.getSiteName())) {
                throw new IllegalArgumentException("Site name repeated! ");
            }
        }

        // 將 Collection 轉換為 Map 以便比較
        Map<String, Set<String>> structureMapA = new HashMap<>();
        Map<String, Set<String>> structureMapB = new HashMap<>();

        // 建立 Collection a 的結構 map
        for (Web_BasicSiteItemAuthedDto itemA : a) {
            structureMapA.put(itemA.getSiteName(),
                    itemA.getFabMap() != null ? itemA.getFabMap().keySet() : new HashSet<>());
        }

        // 建立 Collection b 的結構 map
        for (Web_BasicSiteItemAuthedDto itemB : b) {
            structureMapB.put(itemB.getSiteName(),
                    itemB.getFabMap() != null ? itemB.getFabMap().keySet() : new HashSet<>());
        }

        // 比較每個 siteName 對應的 fabMap 結構
        for (String siteName : structureMapA.keySet()) {
            // 檢查 b 是否包含相同的 siteName
            if (!structureMapB.containsKey(siteName)) {
                return false;
            }

            // 比較 fabMap 的 keySet
            Set<String> keysA = structureMapA.get(siteName);
            Set<String> keysB = structureMapB.get(siteName);

            if (!Objects.equals(keysA, keysB)) {
                return false;
            }
        }
        return true;
    }
}
