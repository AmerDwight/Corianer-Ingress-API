package tw.amer.cia.core.controller.host.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.component.structural.annotation.RequireUserVerifyApi;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ApiEntity;
import tw.amer.cia.core.model.database.SystemEntity;
import tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority.*;
import tw.amer.cia.core.service.core.ValidateService;
import tw.amer.cia.core.service.host.signOff.SignOffForRoleAuthorityService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@HostRestController
@RequireUserVerifyApi
@RequestMapping("/web/signOff")
public class SignOffController {

    @Autowired
    ValidateService validateService;

    @Autowired
    SignOffForRoleAuthorityService signOffForRoleAuthorityService;

    @PostMapping("/apply/role/authority")
    public Object webSignOffCreateApplyRoleAuthority(@RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                     @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId,
                                                     @Valid @RequestBody Web_SignOffApplyRoleAuthorityInDto inDto) throws DataSourceAccessException {
        // Prepare Data and Names
        List<ApplyRoleAuthorityApplyItemDto> onApplyItemList = new ArrayList<>();
        Map<String, List<ApplyRoleAuthorityApplyItemDto>> applyItemBySystemIdMap = new HashMap<>();
        Map<String, List<String>> fabIdListByApiIdMap = new HashMap<>();

        List<SystemEntity> onApplySystemList = validateService.validateSystemSystemIdList(
                inDto.getApplyList().stream().map(Web_SignOffSystemCategoryForApplyRoleAuthorityDto::getSystemId).collect(Collectors.toList()));

        // 驗整 系統 並準備資料
        Map<String, SystemEntity> systemBySystemId = new HashMap<>();
        onApplySystemList.forEach(
                system -> {
                    systemBySystemId.put(system.getSystemId(), system);
                }
        );
        // 驗整 API 並準備資料
        List<ApiEntity> onApplyApiList = validateService.validateApiByApiIdCollection(
                inDto.getApplyList().stream()
                        .flatMap(applyDataBySystem -> applyDataBySystem.getOnApplyApiLIst().stream())
                        .map(applyApiDataInSystem -> applyApiDataInSystem.getApiId())                           // 從 applyApiDataInSystem 獲取 ApiId
                        .collect(Collectors.toList())
        );
        Map<String, ApiEntity> apiByApiId = new HashMap<>();
        onApplyApiList.forEach(
                apiEntity -> {
                    apiByApiId.put(apiEntity.getApiId(), apiEntity);
                }
        );

        inDto.getApplyList().forEach(
                systemCategoryDto -> {
                    systemCategoryDto.getOnApplyApiLIst().forEach(
                            apiItemDto -> {
                                apiItemDto.getScopeIdList().forEach(
                                        scopeId -> {
                                            ApplyRoleAuthorityApplyItemDto applyItem = ApplyRoleAuthorityApplyItemDto.builder()
                                                    .systemId(systemCategoryDto.getSystemId())
                                                    .systemName(systemBySystemId.get(systemCategoryDto.getSystemId()).getSystemName())
                                                    .apiId(apiItemDto.getApiId())
                                                    .apiName(apiByApiId.get(apiItemDto.getApiId()).getApiName())
                                                    .fabId(scopeId).build();
                                            onApplyItemList.add(applyItem);
                                            applyItemBySystemIdMap.computeIfAbsent(
                                                    systemCategoryDto.getSystemId(), k -> new ArrayList<>()).add(applyItem);
                                            fabIdListByApiIdMap.computeIfAbsent(
                                                    apiItemDto.getApiId(), k -> new ArrayList<>()).add(scopeId);
                                        }
                                );
                            }
                    );
                }
        );

        ApplyRoleAuthorityInDto applyDto = ApplyRoleAuthorityInDto.builder()
                .roleId(inDto.getRoleId())
                .applicantId(inDto.getApplicantWorkId())
                .applicantAccount(inDto.getApplicantAccountName())
                .applyReason(inDto.getApplyReason())
                .applyApiByApiIdMap(apiByApiId)
                .applySystemBySystemIdMap(systemBySystemId)
                .onApplyItemList(onApplyItemList)
                .applyItemBySystemIdMap(applyItemBySystemIdMap)
                .fabIdListByApiIdMap(fabIdListByApiIdMap)
                .build();
        List<ApplyRoleAuthorityCreateFormResultDto> createFormResultList = signOffForRoleAuthorityService.applyRoleAuthority(applyDto);

        return createFormResultList.stream()
                .map(applyRoleAuthorityCreateFormResultDto -> {
                    Web_SignOffApplyRoleAuthorityOutDto dto = new Web_SignOffApplyRoleAuthorityOutDto();
                    BeanUtils.copyNonNullProperties(applyRoleAuthorityCreateFormResultDto, dto);
                    return dto;
                }).collect(Collectors.toList());
    }

}
