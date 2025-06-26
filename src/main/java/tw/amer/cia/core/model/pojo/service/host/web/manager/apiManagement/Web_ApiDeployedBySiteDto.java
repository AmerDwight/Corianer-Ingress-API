package tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement;

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
public class Web_ApiDeployedBySiteDto implements Serializable {
    @JsonProperty("SITE_NAME")
    String siteName;

    @JsonProperty("FAB_LIST_AND_DEPLOY_FLAG")
    Map<String, Boolean> fabMap;

    public static List<Web_ApiDeployedBySiteDto> importFromAllAvailableFabAndDeployedFab(Map<String, List<String>> fabIdListBySite, Collection<String> deployedFabList) {
        List<Web_ApiDeployedBySiteDto> newList = new ArrayList<>();
        for (String site : fabIdListBySite.keySet()) {
            Map<String, Boolean> deployedFabMap = new HashMap<>();
            for (String fabId : fabIdListBySite.get(site)) {
                if (CollectionUtils.isNotEmpty(deployedFabList)) {
                    deployedFabMap.put(fabId, deployedFabList.contains(fabId));
                } else {
                    deployedFabMap.put(fabId, Boolean.FALSE);
                }
            }
            newList.add(Web_ApiDeployedBySiteDto.builder()
                    .siteName(site)
                    .fabMap(deployedFabMap)
                    .build());
        }
        return newList;
    }
}
