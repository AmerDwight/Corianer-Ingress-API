package tw.amer.cia.core.component.functional.statistic.clc.supplier;

import tw.amer.cia.core.component.functional.statistic.clc.processor.ClcAuthorityApplyLogMessageProcessor;
import tw.amer.cia.core.component.structural.annotation.HostComponent;
import tw.amer.cia.core.model.database.RoleAuthApplyDetailHistoryEntity;
import tw.amer.cia.core.model.database.dao.RoleAuthApplyDetailHistoryEntityRepo;
import tw.amer.cia.core.model.database.dao.RoleAuthApplyHistoryEntityRepo;
import tw.amer.cia.core.model.database.RoleAuthApplyHistoryEntity;
import tw.amer.cia.core.model.pojo.service.host.statistic.clc.CiaAuthorityApplyHisDto;
import tw.amer.cia.core.model.pojo.service.host.statistic.clc.ClcLogMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Slf4j
@HostComponent
@ConditionalOnProperty(name = "coriander-ingress-api.host.clc.enable-clc", havingValue = "true")
public class ClcAuthorityApplyHisLogSupplier implements ClcHistoryLogSupplier {

    @Autowired
    RoleAuthApplyHistoryEntityRepo hApyRoleAuthRepo;

    @Autowired
    RoleAuthApplyDetailHistoryEntityRepo hApyRoleAuthDetailRepo;

    @Autowired
    ClcAuthorityApplyLogMessageProcessor logProcessor;

    @Override
    public Collection<ClcLogMessage> supply() {
        List<RoleAuthApplyHistoryEntity> totalApplyList = hApyRoleAuthRepo.findAll();
        if (CollectionUtils.isNotEmpty(totalApplyList)) {
            List<ClcLogMessage> result = new ArrayList<>();
            for (RoleAuthApplyHistoryEntity mainApply : totalApplyList) {
                List<RoleAuthApplyDetailHistoryEntity> detailList = hApyRoleAuthDetailRepo.findByApplyFormId(mainApply.getApplyFormId());
                if (CollectionUtils.isNotEmpty(detailList)) {
                    CiaAuthorityApplyHisDto.createByHisData(mainApply, detailList)
                            .forEach(
                                    onTransferDto -> result.add(logProcessor.processLog(onTransferDto))
                            );
                }
            }
            return result;
        }
        return Collections.emptyList();
    }
}
