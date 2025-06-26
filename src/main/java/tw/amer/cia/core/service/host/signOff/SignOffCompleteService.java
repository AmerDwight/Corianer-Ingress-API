package tw.amer.cia.core.service.host.signOff;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.component.functional.statistic.clc.ClcMessageSender;
import tw.amer.cia.core.component.functional.statistic.clc.processor.ClcAuthorityApplyLogMessageProcessor;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.database.dao.*;
import tw.amer.cia.core.model.pojo.service.common.role.UpdateRoleHostDto;
import tw.amer.cia.core.model.pojo.service.host.statistic.clc.CiaAuthorityApplyHisDto;
import tw.amer.cia.core.model.pojo.service.host.statistic.clc.ClcLogMessage;
import tw.amer.cia.core.service.core.ValidateService;
import tw.amer.cia.core.service.host.RoleServiceForHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@HostService
public class SignOffCompleteService {

    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;

    @Autowired
    RoleAuthApplyEntityRepo roleAuthApplyEntityRepo;

    @Autowired
    RoleAuthApplyDetailEntityRepo roleAuthApplyDetailEntityRepo;

    @Autowired
    RoleAuthApplyHistoryEntityRepo roleAuthApplyHistoryEntityRepo;

    @Autowired
    RoleAuthApplyDetailHistoryEntityRepo roleAuthApplyDetailHistoryEntityRepo;

    @Autowired
    RoleServiceForHost roleServiceForHost;

    @Autowired
    ClcMessageSender clcMessageSender;

    @Autowired
    ClcAuthorityApplyLogMessageProcessor logProcessor;

    @Autowired
    ValidateService validateService;

    @Transactional(rollbackFor = {Exception.class})
    public void autoCompleteRoleAuthorityGrantingByApplyFormId(String applyFormId) throws DataSourceAccessException, CiaProcessorException {
        List<RoleAuthApplyEntity> applyFormList = roleAuthApplyEntityRepo.findByApplyFormId(applyFormId);
        List<RoleAuthApplyDetailEntity> applyFormDetailList = roleAuthApplyDetailEntityRepo.findByApplyFormId(applyFormId);
        if (CollectionUtils.isNotEmpty(applyFormList) && CollectionUtils.isNotEmpty(applyFormDetailList)) {
            // Re-organize on grant api by fab
            Map<String, List<String>> apiIdListByFabIdMap = new HashMap<>();
            applyFormDetailList.forEach(
                    formDetail -> {
                        apiIdListByFabIdMap.computeIfAbsent(
                                formDetail.getFabId(), k -> new ArrayList<>()
                        ).add(formDetail.getApiId());
                    }
            );
            for (RoleAuthApplyEntity singleForm : applyFormList) {
                // Prepare Role Data
                RoleEntity onUpdateRole = validateService.validateRoleId(singleForm.getRoleId());

                // Update By Fab
                for (String fabId : apiIdListByFabIdMap.keySet()) {
                    // Obtain the original authed ApiIdList
                    List<String> originalRoleAuthApiIdList = roleAuthorityEntityRepo.findApiIdByRoleIdAndFabId(onUpdateRole.getRoleId(), fabId);
                    List<String> onGrantApiIdList = apiIdListByFabIdMap.get(fabId);
                    List<String> unionApiIdListWithoutDuplicated = Stream.concat(
                            originalRoleAuthApiIdList.stream(),
                            onGrantApiIdList.stream()
                    ).distinct().collect(Collectors.toList());
                    log.debug("On Grant Api For Role: {}, Api List: {}", onUpdateRole.getRoleId(), onGrantApiIdList);

                    roleServiceForHost.updateRole(UpdateRoleHostDto.builder()
                            .roleId(onUpdateRole.getRoleId())
                            .roleName(onUpdateRole.getRoleName())
                            .roleType(onUpdateRole.getRoleType())
                            .roleDesc(onUpdateRole.getRoleDesc())
                            .fabId(fabId)
                            .updateRef(applyFormId)
                            .apiIdList(unionApiIdListWithoutDuplicated)
                            .build()
                    );
                }
            }
            // Finally Move To History
            List<RoleAuthApplyHistoryEntity> hisMainData = applyFormList.stream().map(RoleAuthApplyHistoryEntity::createFromRealData).collect(Collectors.toList());
            List<RoleAuthApplyDetailHistoryEntity> hisDetailData = applyFormDetailList.stream().map(RoleAuthApplyDetailHistoryEntity::createFromRealData).collect(Collectors.toList());
            roleAuthApplyHistoryEntityRepo.saveAll(hisMainData);
            roleAuthApplyDetailHistoryEntityRepo.saveAll(hisDetailData);
            roleAuthApplyDetailEntityRepo.deleteByApplyFormId(applyFormId);
            roleAuthApplyEntityRepo.deleteByApplyFormId(applyFormId);

            // Send Records to CLC
            clcRecordApply(hisMainData, hisDetailData);
        }else{
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.NOT_FOUND,
                    ErrorConstantLib.GENERAL_API_MISSING_CRITICAL_DATA_INPUT.getCompleteMessage() + " "
                            + "Apply Form Number."
            );
        }
    }

    public void clcRecordApply(List<RoleAuthApplyHistoryEntity> mainApplies, List<RoleAuthApplyDetailHistoryEntity> detailList) throws DataSourceAccessException {
        List<ClcLogMessage> clcLogMessages = new ArrayList<>();
        for (RoleAuthApplyHistoryEntity mainApply : mainApplies) {
            CiaAuthorityApplyHisDto.createByHisData
                            (mainApply, detailList.stream()
                                    .filter(detail -> StringUtils.equalsIgnoreCase(detail.getApplyFormId(), mainApply.getApplyFormId()))
                                    .collect(Collectors.toList()))
                    .forEach(
                            onTransferDto -> clcLogMessages.add(logProcessor.processLog(onTransferDto))
                    );
        }
        for (ClcLogMessage clcLogMessage : clcLogMessages) {
            clcMessageSender.addMessage(clcLogMessage);
        }

    }
}
