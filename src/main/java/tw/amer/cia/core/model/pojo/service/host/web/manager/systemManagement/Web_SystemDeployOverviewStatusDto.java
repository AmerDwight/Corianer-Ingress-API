package tw.amer.cia.core.model.pojo.service.host.web.manager.systemManagement;


import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.model.database.SystemDpyEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_SystemDeployOverviewStatusDto implements Serializable {
    @JsonProperty("TOTAL")
    @Builder.Default
    private Integer total = 0;

    @JsonProperty("ACTIVE")
    @Builder.Default
    private Integer active = 0;

    @JsonProperty("NON_ACTIVE")
    @Builder.Default
    private Integer nonActive = 0;

    public static Web_SystemDeployOverviewStatusDto importFromTableData(final Collection<SystemDpyEntity> deployments) {
        Web_SystemDeployOverviewStatusDto result = new Web_SystemDeployOverviewStatusDto();
        result.setTotal(deployments.size());

        Map<String, Long> statusCount = deployments.stream()
                .collect(Collectors.groupingBy(
                        SystemDpyEntity::getActiveStatus,
                        Collectors.counting()
                ));

        result.setActive(statusCount.getOrDefault(
                GeneralSetting.GENERAL_ACTIVE_STATUS_ACTIVE, 0L).intValue());
        result.setNonActive(statusCount.getOrDefault(
                GeneralSetting.GENERAL_ACTIVE_STATUS_NON_ACTIVE, 0L).intValue());
        return result;
    }
}