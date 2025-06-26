package tw.amer.cia.core.service.host.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.common.RoleSetting;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.common.utility.PageableUtils;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.database.compositeId.RoleUserEntityId;
import tw.amer.cia.core.model.database.dao.*;
import tw.amer.cia.core.model.pojo.service.common.role.CreateRoleHostDto;
import tw.amer.cia.core.model.pojo.service.common.role.UpdateRoleHostDto;
import tw.amer.cia.core.model.pojo.service.host.web.item.Web_BasicSiteItemAuthedDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement.*;
import tw.amer.cia.core.service.core.ValidateService;
import tw.amer.cia.core.service.host.RoleServiceForHost;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@HostService
public class Web_ManagerRoleManageService {

    @Autowired
    ValidateService validateService;

    @Autowired
    UserEntityRepo userEntityRepo;

    @Autowired
    RoleUserEntityRepo roleUserEntityRepo;

    @Autowired
    RoleEntityRepo roleEntityRepo;

    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;

    @Autowired
    SystemEntityRepo systemEntityRepo;

    @Autowired
    ApiEntityRepo apiEntityRepo;

    @Autowired
    ApiDpyEntityRepo apiDpyEntityRepo;

    @Autowired
    RoleServiceForHost roleServiceForHost;


    public Page<Web_RoleManagementPanelDataDto> findRoleManagePanelOrderByRoleIdDesc(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(RoleEntity.Fields.roleId).descending());
        Page<Web_RoleManagementPanelDataDto> sourcePage = roleEntityRepo.findRoleManagePanelDataOrderByRoleIdDesc(pageable);
        if (CollectionUtils.isNotEmpty(sourcePage.getContent())) {
            return sourcePage;
        }
        return new PageImpl<>(Collections.emptyList());
    }

    public Page<Web_RoleManagementModifyAuthSystemDto> findRoleManagementModifyAuthSystemOrderBySystemName(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(SystemEntity.Fields.systemName).descending());
        Page<Web_RoleManagementModifyAuthSystemDto> sourcePage = systemEntityRepo.findRoleManagementModifyAuthSystemOrderBySystemName(pageable);
        if (CollectionUtils.isNotEmpty(sourcePage.getContent())) {
            return sourcePage;
        }
        return new PageImpl<>(Collections.emptyList());

    }

    public Page<Web_RoleManagementModifyAuthApiDto> modifyAuthFindApiWithAuthorityOrderByApiName(String roleId, String systemId, int pageNumber, int pageSize) {
        List<ApiEntity> totalApiList = apiEntityRepo.findBySystemId(systemId);
        List<String> totalApiIdList = totalApiList.stream().map(ApiEntity::getApiId).collect(Collectors.toList());

        // 微服務部署資訊取得
        List<ApiDpyEntity> totalApiDpyList = apiDpyEntityRepo.findByFabIdIn(totalApiIdList);
        Map<String, List<String>> apiDpyByApiIdMap = totalApiDpyList.stream()
                .filter(apiDpyEntity -> !StringUtils.equalsIgnoreCase(apiDpyEntity.getFabId(), GeneralSetting.SANDBOX_FAB.getFabId()))
                .collect(Collectors.groupingBy(
                        ApiDpyEntity::getApiId,
                        Collectors.mapping(ApiDpyEntity::getFabId, Collectors.toList())
                ));

        // ROLE 針對 微服務的權限 資料取得
        List<RoleAuthorityEntity> roleAuthorityList = roleAuthorityEntityRepo.findByRoleIdAndApiIdIn(roleId, totalApiIdList);
        Map<String, List<String>> roleAuthorityByApiIdMap = roleAuthorityList.stream()
                .collect(Collectors.groupingBy(
                        RoleAuthorityEntity::getApiId,
                        Collectors.mapping(RoleAuthorityEntity::getFabId, Collectors.toList())
                ));

        // 處理資料
        if (CollectionUtils.isNotEmpty(totalApiList)) {
            List<Web_RoleManagementModifyAuthApiDto> resultList =
                    totalApiList.stream().map(apiEntity -> {
                        Web_RoleManagementModifyAuthApiDto outDto = new Web_RoleManagementModifyAuthApiDto();
                        // 複製 Api主表資訊
                        BeanUtils.copyNonNullProperties(apiEntity, outDto);

                        // 設置其他資訊
                        outDto.setSystemId(systemId);
                        outDto.setSiteAuthedList(
                                Web_BasicSiteItemAuthedDto.importFromDeployedFabAndAuthorizedFab(
                                        fabIdListTransferTositeFabIdMap(apiDpyByApiIdMap.get(apiEntity.getApiId())),
                                        roleAuthorityByApiIdMap.get(apiEntity.getApiId())
                                )
                        );
                        return outDto;
                    }).collect(Collectors.toList());
            return PageableUtils.convertListToPage(
                    resultList,
                    PageRequest.of(
                            pageNumber,
                            pageSize,
                            Sort.by(Web_RoleManagementModifyAuthApiDto.Fields.apiName).ascending()));
        }
        return new PageImpl<>(Collections.emptyList());
    }

    public Map<String, List<String>> fabIdListTransferTositeFabIdMap(Collection<String> fabIdList) {
        Map<String, List<String>> resultMap = new HashMap<>();
        try {
            if (CollectionUtils.isNotEmpty(fabIdList)) {
                for (String fabId : fabIdList) {
                    String site = validateService.validateFabIdExistsReturnSite(fabId);
                    if (resultMap.containsKey(site)) {
                        resultMap.get(site).add(fabId);
                    } else {
                        resultMap.put(site, new ArrayList<String>() {{
                            add(fabId);
                        }});
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return resultMap;
    }

    public void modifyRoleAuth(Web_RoleManagementModifyAuthInDto inDto) throws DataSourceAccessException, CiaProcessorException {

        // 微服務部署資訊取得
        List<ApiDpyEntity> targetApiDpyList = apiDpyEntityRepo.findByApiIdWithoutVirtualSite(inDto.getApiId());
        List<RoleAuthorityEntity> totalRoleAuthorities = roleAuthorityEntityRepo.findByRoleId(inDto.getRoleId());

        // 按照原本的角色權限生成相應的資料結構，以便進行判斷
        List<Web_BasicSiteItemAuthedDto> originalAuthedBySiteDtoList =
                Web_BasicSiteItemAuthedDto.importFromDeployedFabAndAuthorizedFab(
                        fabIdListTransferTositeFabIdMap(targetApiDpyList.stream().map(ApiDpyEntity::getFabId).collect(Collectors.toList())),
                        totalRoleAuthorities.stream()
                                .filter(cRoleAuthority -> StringUtils.equals(cRoleAuthority.getApiId(),inDto.getApiId()))
                                .map(RoleAuthorityEntity::getFabId)
                                .collect(Collectors.toList()));

        try {
            // 先進行判斷，確認請求資訊與部署廠區資訊結構一致
            if (CollectionUtils.isNotEmpty(originalAuthedBySiteDtoList) &&
                    CollectionUtils.isNotEmpty(inDto.getSiteAuthedList()) &&
                    Web_BasicSiteItemAuthedDto.checkStructureEquals(originalAuthedBySiteDtoList, inDto.getSiteAuthedList())) {

                // 轉換成BySiteMap形式比對
                Map<String, Web_BasicSiteItemAuthedDto> originalDataBySiteNameMap = new HashMap<>();
                Map<String, Web_BasicSiteItemAuthedDto> onUpdateDataBySiteNameMap = new HashMap<>();
                // 先按照Site 建構 Map
                originalAuthedBySiteDtoList.forEach(authedDto -> originalDataBySiteNameMap.put(authedDto.getSiteName(), authedDto));
                inDto.getSiteAuthedList().forEach(authedDto -> onUpdateDataBySiteNameMap.put(authedDto.getSiteName(), authedDto));

                // 將現有角色權限按照FAB進行分類，以便配合 RoleEntityService 介面
                Map<String, List<String>> authorizedApiIdByFabIdMap =
                        totalRoleAuthorities.parallelStream()
                                .collect(Collectors.groupingBy(
                                        RoleAuthorityEntity::getFabId,
                                        Collectors.mapping(
                                                RoleAuthorityEntity::getApiId,
                                                Collectors.toList()
                                        ))
                                );

                for (String siteName : onUpdateDataBySiteNameMap.keySet()) {
                    Map<String, Boolean> changedStatusByFabIdInSiteMap = Web_BasicSiteItemAuthedDto.
                            getChangedByFabIdMap(originalDataBySiteNameMap.get(siteName),
                                    onUpdateDataBySiteNameMap.get(siteName));
                    if (MapUtils.isNotEmpty(changedStatusByFabIdInSiteMap)) {
                        // 針對有進行 權限異動 的 FAB 進行操作
                        for (String fabId : changedStatusByFabIdInSiteMap.keySet()) {
                            {
                                boolean isGrant = BooleanUtils.isTrue(changedStatusByFabIdInSiteMap.get(fabId));

                                // 更新微服務權限清單
                                List<String> authedApiIdList =
                                        // 當 該角色 在 該廠區沒有任何微服務權限，List 可能為空
                                        Optional.ofNullable(authorizedApiIdByFabIdMap.get(fabId))
                                                .orElse(new ArrayList<>());
                                if (isGrant) {
                                    authedApiIdList.add(inDto.getApiId());
                                } else {
                                    authedApiIdList.remove(inDto.getApiId());
                                }

                                // 進行更新
                                UpdateRoleHostDto onUpdateDto = UpdateRoleHostDto.builder()
                                        .roleId(inDto.getRoleId())
                                        .fabId(fabId)
                                        .apiIdList(authedApiIdList)
                                        .build();
                                roleServiceForHost.updateRole(onUpdateDto);
                            }
                        }
                    }
                }
            } else {
                log.error("Critical Error, got a serious issue while modifyRoleAuth processing.");
                log.debug("Critical parameters: originalAuthedBySiteDtoList is empty or null? {} ", originalAuthedBySiteDtoList);
                log.debug("Critical parameters: inDto.getSiteAuthedList() is empty or null? {} ", inDto.getSiteAuthedList());
                log.debug("Check structure compliance: {} ", Web_BasicSiteItemAuthedDto.checkStructureEquals(originalAuthedBySiteDtoList, inDto.getSiteAuthedList()));
                throw new IllegalArgumentException("Structure mismatched, please contact owner for process logics.");
            }
        } catch (IllegalArgumentException e) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.GENERAL_API_MISSING_CRITICAL_DATA_INPUT.getCompleteMessage() + " " +
                            e.getMessage()
            );
        } catch (DataSourceAccessException | CiaProcessorException e) {
            throw e;
        } catch (Exception e) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorConstantLib.UNKNOWN_EXCEPTION_API_ERROR.getCompleteMessage()
            );
        }
    }

    public Web_RoleManagementForMemberPanelDto findRoleMemberPanel(String onSearchRoleId) throws DataSourceAccessException {
        RoleEntity role = validateService.validateRoleId(onSearchRoleId);
        return Web_RoleManagementForMemberPanelDto.builder()
                .roleId(role.getRoleId())
                .roleType(role.getRoleType())
                .roleName(role.getRoleName())
                .roleDesc(role.getRoleDesc())
                .isMemberModifiable(
                        RoleSetting.ROLE_TYPE
                                .fromString(role.getRoleType())
                                .isMemberModifiable()
                )
                .memberList(
                        userEntityRepo.findRoleManagementMemberByRoleId(role.getRoleId())
                ).build();
    }

    public Web_RoleManagementUserRoleListDto findRoleListByUserId(String onSearchUserId) throws DataSourceAccessException {
        List<RoleUserEntity> roleUsrList = roleUserEntityRepo.findByUserId(onSearchUserId);
        if (CollectionUtils.isNotEmpty(roleUsrList)) {
            return Web_RoleManagementUserRoleListDto.builder()
                    .userId(onSearchUserId)
                    .roles(
                            roleUsrList
                                    .stream()
                                    .map(
                                            cRoleUsr -> Web_RoleManagementUserRoleDto.builder()
                                                    .roleId(cRoleUsr.getRoleId())
                                                    .joinTime(cRoleUsr.getLmTime())
                                                    .build()
                                    ).collect(Collectors.toSet())
                    )
                    .build();
        } else {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.NO_CONTENT,
                    " No Role Data For User: " + onSearchUserId
            );
        }
    }

    public void changeUiVisibilityOfMember(String onUpdateRoleId, String onUpdateUserId) throws DataSourceAccessException {
        RoleEntity role = validateService.validateRoleId(onUpdateRoleId);
        Optional<RoleUserEntity> onSearchRelation = roleUserEntityRepo.findByUserIdAndRoleId(onUpdateUserId, onUpdateRoleId);
        if (onSearchRelation.isPresent()) {
            RoleUserEntity relation = onSearchRelation.get();
            if (StringUtils.equalsIgnoreCase(relation.getIsUiVisible(), GeneralSetting.GENERAL_POSITIVE_STRING)) {
                relation.setIsUiVisible(GeneralSetting.GENERAL_NEGATIVE_STRING);
            } else if (StringUtils.equalsIgnoreCase(relation.getIsUiVisible(), GeneralSetting.GENERAL_NEGATIVE_STRING)) {
                relation.setIsUiVisible(GeneralSetting.GENERAL_POSITIVE_STRING);
            }
            roleUserEntityRepo.save(relation);
        } else {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_USER_USER_ID_OR_ROLE_ID_NOT_MATCH.getCompleteMessage()
            );
        }
    }

    public void deleteRoleMember(String onUpdateRoleId, String onDeleteUserId) throws DataSourceAccessException {
        RoleEntity role = validateService.validateRoleId(onUpdateRoleId);

        if (StringUtils.equalsIgnoreCase(role.getRoleType(), RoleSetting.ROLE_TYPE.DEPT.name())) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.GENERAL_API_ILLEGAL_LOGIC_OPERATION.getCompleteMessage() + " " +
                            " DEPT role member cannot be modified."
            );
        } else {
            roleUserEntityRepo.deleteById(
                    RoleUserEntityId.builder()
                            .userId(onDeleteUserId)
                            .roleId(onUpdateRoleId).build()
            );
        }
    }

    // CIA 4.3.0
    public List<Web_RoleManagementDeptCodeDto> getAllDeptCode() {
        return null;
    }

    // CIA 4.3.0
    public void createRole(Web_RoleManagementCreateRoleInDto inDto) throws DataSourceAccessException, CiaProcessorException {
        Optional<RoleEntity> onSearchRole = roleEntityRepo.findByRoleName(inDto.getRoleName());
        if (onSearchRole.isPresent()) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_ROLE_DUPLICATE_ROLE_ID_OR_NAME.getCompleteMessage()
            );
        }
        RoleSetting.ROLE_TYPE roleType = RoleSetting.ROLE_TYPE.fromString(inDto.getRoleType());
        if (roleType == null) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getCompleteMessage() + "ROLE_TYPE"
            );
        }

        // Create Role
        String newRoleId = inDto.getRoleName().trim();
        roleServiceForHost.createRole(
                CreateRoleHostDto.builder()
                        .roleId(newRoleId)
                        .roleName(inDto.getRoleName())
                        .roleType(roleType.name())
                        .roleDesc(inDto.getRoleDesc())
                        .build()
        );

        Optional<RoleEntity> onCreatedRole = roleEntityRepo.findByRoleId(newRoleId);
        if (onCreatedRole.isPresent()) {
            // Grant initial Role Authority
            Map<String, List<String>> apiListByFabIdMap = inDto.getApiListByFabIdMap();
            if (MapUtils.isNotEmpty(apiListByFabIdMap)) {
                for (String fabId : apiListByFabIdMap.keySet()) {
                    if (CollectionUtils.isNotEmpty(apiListByFabIdMap.get(fabId))) {
                        roleServiceForHost.manageClientRoleAuthorityByFab(
                                onCreatedRole.get(),
                                "CIA MANAGER",
                                fabId,
                                apiListByFabIdMap.get(fabId));
                    }
                }
            }
        }

    }

    // CIA 4.3.0
    public Page<Web_RoleManagementCreateRoleAuthApiDto> findCreateRoleNoAuthApiListBySystemIdOrderByApiName(String onSearchSystemId, int pageNumber, int pageSize) {
        List<ApiEntity> totalApiList = apiEntityRepo.findBySystemId(onSearchSystemId);
        List<String> totalApiIdList = totalApiList.stream().map(ApiEntity::getApiId).collect(Collectors.toList());

        // 微服務部署資訊取得
        List<ApiDpyEntity> totalApiDpyList = apiDpyEntityRepo.findByApiIdIn(totalApiIdList);
        Map<String, List<String>> apiDpyByApiIdMap = totalApiDpyList.stream()
                .filter(apiDpyEntity -> !StringUtils.equalsIgnoreCase(apiDpyEntity.getFabId(), GeneralSetting.SANDBOX_FAB.getFabId()))
                .collect(Collectors.groupingBy(
                        ApiDpyEntity::getApiId,
                        Collectors.mapping(ApiDpyEntity::getFabId, Collectors.toList())
                ));

        // 處理資料
        if (CollectionUtils.isNotEmpty(totalApiList)) {
            List<Web_RoleManagementCreateRoleAuthApiDto> resultList =
                    totalApiList.stream().map(apiEntity -> {
                        Web_RoleManagementCreateRoleAuthApiDto outDto = new Web_RoleManagementCreateRoleAuthApiDto();
                        // 複製 Api主表資訊
                        BeanUtils.copyNonNullProperties(apiEntity, outDto);

                        // 設置其他資訊
                        outDto.setSystemId(onSearchSystemId);
                        outDto.setSiteAuthedList(
                                Web_BasicSiteItemAuthedDto.importFromDeployedFabAndAuthorizedFab(
                                        fabIdListTransferTositeFabIdMap(apiDpyByApiIdMap.get(apiEntity.getApiId())),
                                        new ArrayList<>()
                                )
                        );
                        return outDto;
                    }).collect(Collectors.toList());
            return PageableUtils.convertListToPage(
                    resultList,
                    PageRequest.of(
                            pageNumber,
                            pageSize,
                            Sort.by(Web_RoleManagementCreateRoleAuthApiDto.Fields.apiName).ascending()));
        }
        return new PageImpl<>(Collections.emptyList());
    }

    // CIA 4.3.0
    public Page<Web_RoleManagementPanelDataDto> findRoleNameLikeOrderByRoleName(int pageNumber, int pageSize, String roleName) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(RoleEntity.Fields.roleId).descending());
        Page<Web_RoleManagementPanelDataDto> sourcePage = roleEntityRepo.findByRoleNameLike(pageable, "%" + roleName + "%");
        if (CollectionUtils.isNotEmpty(sourcePage.getContent())) {
            return sourcePage;
        }
        return new PageImpl<>(Collections.emptyList());
    }

    // CIA 4.3.0
    public Page<String> findDeptCodeLike(int pageNumber, int pageSize, String deptCode) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(UserEntity.Fields.deptCode).ascending());
        Page<String> sourcePage = userEntityRepo.findDeptCodeByDeptCodeLike(pageable, "%" + deptCode + "%");
        if (CollectionUtils.isNotEmpty(sourcePage.getContent())) {
            return sourcePage;
        }
        return new PageImpl<>(Collections.emptyList());
    }

    // CIA 4.3.0
    public Page<Web_RoleManagementMemberSimpleDto> findMembersByDeptCode(int pageNumber, int pageSize, String deptCode) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(UserEntity.Fields.userId).ascending());
        Page<Web_RoleManagementMemberSimpleDto> sourcePage = userEntityRepo.findMembersByDeptCode(pageable, deptCode);
        if (CollectionUtils.isNotEmpty(sourcePage.getContent())) {
            return sourcePage;
        }
        return new PageImpl<>(Collections.emptyList());
    }

    // CIA 4.3.0
    @Transactional(rollbackFor = Exception.class)
    public void modifyRoleMember(Web_RoleManagementModifyRoleMemberInDto inDto) throws DataSourceAccessException {
        validateService.validateRoleId(inDto.getRoleId());
        Set<String> totalUserIds = new HashSet<>(CollectionUtils.union(inDto.getAddUserIdList(), inDto.getRemoveUserIdList()));
        Set<String> notExistsUserIds = new HashSet<>(CollectionUtils.subtract(totalUserIds, userEntityRepo.findUserIdsByUserIdIn(totalUserIds)));

        if (CollectionUtils.isNotEmpty(notExistsUserIds)) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_USER_INVALID_USER_ID.getCompleteMessage() + ": " + notExistsUserIds
            );
        }

        if (CollectionUtils.isNotEmpty(inDto.getRemoveUserIdList())) {
            try {
                inDto.getRemoveUserIdList().forEach(
                        onRemoveUserId -> {
                            roleUserEntityRepo.deleteById(new RoleUserEntityId(onRemoveUserId, inDto.getRoleId()));
                        }
                );
            } catch (EmptyResultDataAccessException e) {
                throw DataSourceAccessException.createExceptionForHttp(
                        HttpStatus.NOT_FOUND,
                        ErrorConstantLib.VALIDATE_USER_USER_ID_OR_ROLE_ID_NOT_MATCH.getMessage()
                );
            }
        }
        if (CollectionUtils.isNotEmpty(inDto.getAddUserIdList())) {
            inDto.getAddUserIdList().forEach(
                    onAddUserId -> {
                        roleUserEntityRepo.save(
                                RoleUserEntity.builder()
                                        .roleId(inDto.getRoleId())
                                        .userId(onAddUserId)
                                        .build()
                        );
                    }
            );
        }
    }
}
