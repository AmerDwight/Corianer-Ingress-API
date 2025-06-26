package tw.amer.cia.core.service.host.signOff;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.SignOffStandardSetting;
import tw.amer.cia.core.component.functional.signOff.SignOffProcessComponent;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.database.dao.*;
import tw.amer.cia.core.model.pojo.service.host.web.signOff.SignatureReviewer;
import tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority.*;
import tw.amer.cia.core.model.pojo.service.host.web.signOff.marker.ApplyRoleAuthorityResponseContext;
import tw.amer.cia.core.service.core.ValidateService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Slf4j
@HostService
public class SignOffForRoleAuthorityService {
    @Autowired
    ValidateService validateService;

    @Autowired
    ApiDpyEntityRepo apiDpyEntityRepo;

    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;

    @Autowired
    SystemSignOffConfigEntityRepo systemSignOffConfigEntityRepo;

    @Autowired
    FabSignOffConfigEntityRepo fabSignOffConfigEntityRepo;

    @Autowired
    RoleAuthApplyEntityRepo roleAuthApplyEntityRepo;

    @Autowired
    RoleAuthApplyDetailEntityRepo roleAuthApplyDetailEntityRepo;

    @Autowired
    SignOffProcessComponent signOffProcessComponent;

    private static final String CIA_CHARGER_MAP_HEADER = "ciaChargerId";
    private static final String CIA_SUPER_CHARGER_MAP_HEADER = "ciaSuperChargerId";

    public List<ApplyRoleAuthorityCreateFormResultDto> applyRoleAuthority(ApplyRoleAuthorityInDto onApplyData) throws DataSourceAccessException {
        // verify
        if (verifyApplyRoleAuthority(onApplyData)) {
            // 拆單
            List<ApplyRoleAuthorityOnSendSignOffDto> onSendSignOffListBySystem = prepareApplyRoleAuthorityContextListBySystemId(onApplyData);

            // 正式送出簽核 與 紀錄
            List<ApplyRoleAuthorityCreateFormResultDto> createSignOffFormResultList = sendingSignOffForApplyingRoleAuthority(onSendSignOffListBySystem);

            if (CollectionUtils.isNotEmpty(createSignOffFormResultList)) {
                return createSignOffFormResultList;
            }

        }
        throw DataSourceAccessException.createExceptionForHttp(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorConstantLib.UNKNOWN_EXCEPTION_WEB_ERROR.getCompleteMessage()
        );
    }

    private boolean verifyApplyRoleAuthority(@NotNull ApplyRoleAuthorityInDto onApplyData) throws DataSourceAccessException {
        if (CollectionUtils.isNotEmpty(onApplyData.getOnApplyItemList())) {
            List<ApplyRoleAuthorityApiDeployDataForDto> onVerifyList =
                    // 單純資料轉換
                    onApplyData.getOnApplyItemList().stream().map(
                            ApiDataBySystemAndFabDto -> ApplyRoleAuthorityApiDeployDataForDto.builder()
                                    .apiId(ApiDataBySystemAndFabDto.getApiId())
                                    .fabId(ApiDataBySystemAndFabDto.getFabId())
                                    .build()).collect(Collectors.toList());

            // 1. Verify API 與對應的申請廠區 是否有部署資料
            boolean verifyApiDeploy = verifyApplyRoleAuthorityOnApiDpy(onVerifyList);

            // 2. Verify Role 是否真的沒有這些權限? 真的沒有才會進行申請
            boolean verifyRoleAuthority = verifyApplyRoleAuthorityOnIncapableAuthority(onApplyData.getRoleId(), onVerifyList);

            return verifyApiDeploy && verifyRoleAuthority;
        }
        return false;
    }

    private boolean verifyApplyRoleAuthorityOnApiDpy(@NotEmpty List<ApplyRoleAuthorityApiDeployDataForDto> onVerifyList) throws DataSourceAccessException {
        List<ApplyRoleAuthorityApiDeployDataForDto> completeApiDpyList = apiDpyEntityRepo.searchApiAndDeployForSignatureByApiIdList(
                onVerifyList.stream().map(ApplyRoleAuthorityApiDeployDataForDto::getApiId).collect(Collectors.toSet()));
        if (CollectionUtils.isNotEmpty(completeApiDpyList)) {
            Set<ApplyRoleAuthorityApiDeployDataForDto> completeDataSet = new HashSet<>(completeApiDpyList);
            // 檢查
            for (ApplyRoleAuthorityApiDeployDataForDto onCheckItem : onVerifyList) {
                if (!completeDataSet.contains(onCheckItem)) {
                    throw DataSourceAccessException.createExceptionForHttp(
                            HttpStatus.BAD_REQUEST,
                            ErrorConstantLib.WEB_SIGN_OFF_APPLY_ROLE_AUTHORITY_APPLY_ITEM_NOT_EXISTS.getCompleteMessage()
                    );
                }
            }
            return true; // 檢查完成
        }
        return false;
    }

    private boolean verifyApplyRoleAuthorityOnIncapableAuthority(@NotBlank String roleId,
                                                                 @NotEmpty List<ApplyRoleAuthorityApiDeployDataForDto> onVerifyList) throws DataSourceAccessException {
        List<String> apiIdList = onVerifyList.stream().map(ApplyRoleAuthorityApiDeployDataForDto::getApiId).collect(Collectors.toList());
        List<RoleAuthorityEntity> roleAuthedDataList = roleAuthorityEntityRepo.findByRoleIdAndApiIdIn(roleId, apiIdList);
        if (CollectionUtils.isNotEmpty(roleAuthedDataList)) {
            // 已有權限處理
            Map<String, List<String>> roleAuthedApiFabIdListByApiIdMap = new HashMap<>();
            roleAuthedDataList.forEach(
                    cRoleAuthority -> {
                        roleAuthedApiFabIdListByApiIdMap.computeIfAbsent(
                                cRoleAuthority.getApiId(), k -> new ArrayList<>()
                        ).add(cRoleAuthority.getFabId());
                    }
            );
            // 申請權限處理
            Map<String, List<String>> roleApplyApiFabIdListByApiIdMap = new HashMap<>();
            onVerifyList.forEach(
                    signOffDto -> {
                        roleApplyApiFabIdListByApiIdMap.computeIfAbsent(
                                signOffDto.getApiId(), k -> new ArrayList<>()
                        ).add(signOffDto.getFabId());
                    }
            );
            // 比較
            for (String apiId : roleApplyApiFabIdListByApiIdMap.keySet()) {
                if (roleAuthedApiFabIdListByApiIdMap.containsKey(apiId)) {
                    HashSet<String> fabIdOfRoleAuthedApiSet = new HashSet<>(roleAuthedApiFabIdListByApiIdMap.get(apiId));
                    for (String applyFabIdOfApi : roleApplyApiFabIdListByApiIdMap.get(apiId)) {
                        if (fabIdOfRoleAuthedApiSet.contains(applyFabIdOfApi)) {
                            throw DataSourceAccessException.createExceptionForHttp(
                                    HttpStatus.BAD_REQUEST,
                                    ErrorConstantLib.WEB_SIGN_OFF_APPLY_ROLE_AUTHORITY_EXISTS_ROLE_AUTH.getCompleteMessage()
                            );
                        }

                    }
                }
            }

        }
        return true;
    }

    private List<ApplyRoleAuthorityOnSendSignOffDto> prepareApplyRoleAuthorityContextListBySystemId(@NotNull ApplyRoleAuthorityInDto onApplyData) throws DataSourceAccessException {
        // 由於實現邏輯必須與外部系統進行綁定，故採用組合模式進行設計
        // 使用 SignOffProcessComponent 進行資料生成
        if (MapUtils.isNotEmpty(onApplyData.getApplyItemBySystemIdMap())) {
            List<ApplyRoleAuthorityOnSendSignOffDto> preparedApplyRoleAuthorityContextListBySystem = new ArrayList<>();
            for (String systemId : onApplyData.getApplyItemBySystemIdMap().keySet()) {
                List<ApplyRoleAuthorityApplyItemDto> bySystemApplyList = onApplyData.getApplyItemBySystemIdMap().get(systemId);

                // prepare applyItem
                List<ApplyRoleAuthorityApiApplyDataWithNameDto> applyIteapi = new ArrayList<>();
                bySystemApplyList.forEach(
                        applyItem -> {
                            applyIteapi.add(ApplyRoleAuthorityApiApplyDataWithNameDto.builder()
                                    .apiId(applyItem.getApiId())
                                    .apiName(onApplyData.getApplyApiByApiIdMap().get(applyItem.getApiId()).getApiName())
                                    .fabId(applyItem.getFabId())
                                    .build());
                        }
                );

                SystemEntity system = onApplyData.getApplySystemBySystemIdMap().get(systemId);

                // prepare reviewer list
                String systemOwner = system.getOwner();
                List<String> siteReviewerOnSignList = new ArrayList<>();
                List<String> additionalReviewerOnSignList = new ArrayList<>();

                // 準備 Site Owner 之簽核清單
                // 20240120改動，移調 Site 主管優先於系統Owner進行簽核審核
                List<FabSignOffConfigEntity> inSearchSiteReviewerList = fabSignOffConfigEntityRepo.findDistinctByFabIdIn(
                        applyIteapi.stream().map(ApplyRoleAuthorityApiApplyDataWithNameDto::getFabId).collect(Collectors.toSet()));
                if (CollectionUtils.isNotEmpty(inSearchSiteReviewerList)) {
                    inSearchSiteReviewerList.forEach(
                            cFabSignoffAdd -> {
                                if (StringUtils.isNotBlank(cFabSignoffAdd.getSiteManagerId())) {
                                    siteReviewerOnSignList.add(cFabSignoffAdd.getSiteManagerId());
                                }
                            }
                    );
                }

                // 準備 系統Owner指定之簽核清單
                List<SystemSignOffConfigEntity> inSearchAddReviewerList = systemSignOffConfigEntityRepo.findAllBySystemId(systemId);
                if (CollectionUtils.isNotEmpty(inSearchAddReviewerList)) {
                    inSearchAddReviewerList.sort(Comparator.comparing(SystemSignOffConfigEntity::getSignOffRank));
                    inSearchAddReviewerList.forEach(
                            cSysSignoffAdd -> {
                                if (StringUtils.isNotBlank(cSysSignoffAdd.getUserId())) {
                                    additionalReviewerOnSignList.add(cSysSignoffAdd.getUserId());
                                }
                            }
                    );
                }

                // 將所有追加簽核人員配置於SignOffMap
                Map<Integer, SignatureReviewer> reviewerBySeqMap = prepareSignOffSeqMap(
                        siteReviewerOnSignList, Arrays.asList(systemOwner), additionalReviewerOnSignList
                );


                preparedApplyRoleAuthorityContextListBySystem.add(
                        ApplyRoleAuthorityOnSendSignOffDto.builder()
                                .roleId(onApplyData.getRoleId())
                                .systemId(systemId)
                                .systemName(system.getSystemName())
                                .applicantId(onApplyData.getApplicantId())
                                .onApplyItemList(onApplyData.getApplyItemBySystemIdMap().get(systemId))
                                .onSendContext(
                                        signOffProcessComponent.createApplyRoleAuthorityContext(
                                                ApplyRoleAuthoritySingleSystemCompleteDataDto.builder()
                                                        .systemId(systemId)
                                                        .systemName(system.getSystemName())
                                                        .applicantWorkId(onApplyData.getApplicantId())
                                                        .applicantAccount(onApplyData.getApplicantAccount())
                                                        .applyReason(onApplyData.getApplyReason())
                                                        .applyScopes(bySystemApplyList.stream()
                                                                .map(ApplyRoleAuthorityApplyItemDto::getFabId)
                                                                .collect(Collectors.toSet()))
                                                        .applyItems(applyIteapi)
                                                        .reviewerBySeqMap(reviewerBySeqMap)
                                                        .build())
                                )
                                .build()
                );

            }
            return preparedApplyRoleAuthorityContextListBySystem;
        }
        throw DataSourceAccessException.createExceptionForHttp(
                HttpStatus.BAD_REQUEST,
                ErrorConstantLib.WEB_SIGN_OFF_APPLY_ROLE_AUTHORITY_APPLY_DATA_MISSING.getCompleteMessage()
        );
    }

    private Map<Integer, SignatureReviewer> prepareSignOffSeqMap(final List<String> siteReviewers, final List<String> sysOwnerReviewers, final List<String> additionalReviewer) {
        // Three Parts: Basic + Additional
        int signOffSeqCounter = 1;
        Map<Integer, SignatureReviewer> reviewerBySeqMap = new HashMap<>();
        AtomicInteger atomicSignOffSeqCounter = new AtomicInteger(signOffSeqCounter);

        // prepare site reviewer
        if (CollectionUtils.isNotEmpty(siteReviewers)) {
            siteReviewers.stream().forEach(
                    reviewerId -> {
                        reviewerBySeqMap.put(atomicSignOffSeqCounter.getAndIncrement(),
                                SignatureReviewer.builder()
                                        .reviewerWorkId(reviewerId)
                                        .build());
                    }
            );
        }


        // prepare system owner reviewer
        if (CollectionUtils.isNotEmpty(sysOwnerReviewers)) {
            sysOwnerReviewers.stream().forEach(
                    reviewerId -> {
                        reviewerBySeqMap.put(atomicSignOffSeqCounter.getAndIncrement(),
                                SignatureReviewer.builder()
                                        .reviewerWorkId(reviewerId)
                                        .build());
                    }
            );
        }

        // prepare additional  reviewer
        if (CollectionUtils.isNotEmpty(additionalReviewer)) {
            additionalReviewer.stream().forEach(
                    reviewerId -> {
                        reviewerBySeqMap.put(atomicSignOffSeqCounter.getAndIncrement(),
                                SignatureReviewer.builder()
                                        .reviewerWorkId(reviewerId)
                                        .build());
                    }
            );
        }
        return reviewerBySeqMap;
    }

    private List<ApplyRoleAuthorityCreateFormResultDto> sendingSignOffForApplyingRoleAuthority(List<ApplyRoleAuthorityOnSendSignOffDto> onSendListBySystemId) throws DataSourceAccessException {
        // 預設應該都要成功，所以遇到錯誤不進行中斷，而是跳過單筆
        // 故 sendingSignOffForApplyingRoleAuthorityBySystem 不任意拋出錯誤
        List<ApplyRoleAuthorityCreateFormResultDto> applyFormResultList = new ArrayList<>();
        List<String> failedCreatedFormSystemList = new ArrayList<>();

        for (ApplyRoleAuthorityOnSendSignOffDto onSendData : onSendListBySystemId) {
            ApplyRoleAuthorityCreateFormResultDto formResult = sendingSignOffForApplyingRoleAuthorityBySystem(onSendData);
            applyFormResultList.add(formResult);

            // 錯誤示警
            if (!StringUtils.equalsIgnoreCase(formResult.getApplyFormStatus(), SignOffStandardSetting.FormStatus.CREATED)) {
                log.error("Sending Apply Role Authority Fails on applicant: {}, SystemId = {}, Apply Iteapi: {}"
                        , onSendData.getApplicantId(), onSendData.getSystemId(), onSendData.getOnApplyItemList());
                failedCreatedFormSystemList.add(onSendData.getSystemId());
            }
        }
        return applyFormResultList;
    }

    private ApplyRoleAuthorityCreateFormResultDto sendingSignOffForApplyingRoleAuthorityBySystem(ApplyRoleAuthorityOnSendSignOffDto onSendData) {
        // 由於實現邏輯必須與外部系統進行綁定，故採用組合模式進行設計
        // 使用 SignOffProcessComponent 進行簽核單送出
        if (onSendData != null && onSendData.getOnSendContext() != null) {
            try {
                ApplyRoleAuthorityResponseContext result = signOffProcessComponent.sendApplyRoleAuthoritySignature(onSendData.getOnSendContext());
                boolean isFormSuccessfulCreate = StringUtils.equalsIgnoreCase(result.getApplyFormStatus(), SignOffStandardSetting.FormStatus.CREATED);
                if (isFormSuccessfulCreate) {
                    roleAuthApplyEntityRepo.saveAndFlush(
                            RoleAuthApplyEntity.builder()
                                    .applyFormId(result.getApplyFormNumber())
                                    .roleId(onSendData.getRoleId())
                                    .formStatus(result.getApplyFormStatus())
                                    .applicant(onSendData.getApplicantId())
                                    .build());
                    List<RoleAuthApplyDetailEntity> onSaveApplyItemList = new ArrayList<>();
                    for (ApplyRoleAuthorityApplyItemDto applyItem : onSendData.getOnApplyItemList()) {
                        onSaveApplyItemList.add(
                                RoleAuthApplyDetailEntity.builder()
                                        .applyFormId(result.getApplyFormNumber())
                                        .apiId(applyItem.getApiId())
                                        .fabId(applyItem.getFabId())
                                        .build()
                        );
                    }
                    if (CollectionUtils.isNotEmpty(onSaveApplyItemList)) {
                        roleAuthApplyDetailEntityRepo.saveAll(onSaveApplyItemList);
                    }
                } else {
                    log.error("Error Occurred while sending to sign off system." +
                                    " roleId = {} " +
                                    " systemId = {} " +
                                    " Error Message： {}", onSendData.getRoleId(), onSendData.getSystemId(),
                            result == null ? "Empty Log From SignOff SystemEntity." : result.getApplyFormMessage());
                }
                return ApplyRoleAuthorityCreateFormResultDto.builder()
                        .systemId(onSendData.getSystemId())
                        .systemName(onSendData.getSystemName())
                        .applyFormStatus(result.getApplyFormStatus())
                        .applyFormNumber(isFormSuccessfulCreate ? result.getApplyFormNumber() : "")
                        .applyFormMessage(isFormSuccessfulCreate ? "" : result.getApplyFormMessage())
                        .onApplyApiLIst(onSendData.getOnApplyItemList())
                        .build();

            } catch (Exception e) {
                log.error("Sending SignOff Procedure fails on applicant: {} systemId: {} ", onSendData.getApplicantId(), onSendData.getSystemId());
                return null;
            }
        } else {
            log.error("Error Occurred on sending sign off Apply: Data Miss: onSendData is null.");
            return null;
        }
    }
}
