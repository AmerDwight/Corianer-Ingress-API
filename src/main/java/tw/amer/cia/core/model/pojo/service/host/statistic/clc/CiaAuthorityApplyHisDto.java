package tw.amer.cia.core.model.pojo.service.host.statistic.clc;

import tw.amer.cia.core.model.database.RoleAuthApplyHistoryEntity;
import tw.amer.cia.core.model.database.RoleAuthApplyDetailHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CiaAuthorityApplyHisDto {
    private String applyFormId;
    private String roleId;
    private Instant createTime;
    private String apiId;
    private String fabId;

    public static List<CiaAuthorityApplyHisDto> createByHisData(RoleAuthApplyHistoryEntity mainDto, Collection<RoleAuthApplyDetailHistoryEntity> detailDtoData) {
        if (mainDto == null || StringUtils.isEmpty(mainDto.getApplyFormId()) || CollectionUtils.isEmpty(detailDtoData)) {
            return new ArrayList<>();
        }
        return detailDtoData.stream().map(
                detailDto -> CiaAuthorityApplyHisDto.builder()
                        .applyFormId(mainDto.getApplyFormId())
                        .roleId(mainDto.getRoleId())
                        .createTime(mainDto.getCreateTime())
                        .apiId(detailDto.getApiId())
                        .fabId(detailDto.getFabId())
                        .build()
        ).collect(Collectors.toList());
    }
}
