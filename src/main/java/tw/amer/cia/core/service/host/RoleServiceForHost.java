package tw.amer.cia.core.service.host;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.functional.coriander.CallClientApiComponent;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.database.compositeId.RoleAuthoroityEntityId;
import tw.amer.cia.core.model.database.dao.*;
import tw.amer.cia.core.model.pojo.service.common.role.CompleteRoleDto;
import tw.amer.cia.core.model.pojo.service.common.role.CreateRoleHostDto;
import tw.amer.cia.core.model.pojo.service.common.role.DeleteRoleHostDto;
import tw.amer.cia.core.model.pojo.service.common.role.UpdateRoleHostDto;
import tw.amer.cia.core.service.core.ValidateService;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@HostService
@Slf4j
public class RoleServiceForHost {
    @Autowired
    ValidateService validateService;
    @Autowired
    ApikeyServiceForHost apikeyServiceForHost;
    @Autowired
    RoleDeviceServiceForHost roleDeviceServiceForHost;
    @Autowired
    CallClientApiComponent callClientApiComponent;
    @Autowired
    RoleEntityRepo roleEntityRepo;
    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;
    @Autowired
    RoleDeviceEntityRepo roleDeviceEntityRepo;
    @Autowired
    RoleUserEntityRepo roleUserEntityRepo;
    @Autowired
    GwApikeyEntityRepo gwApikeyEntityRepo;
    @Autowired
    SystemEntityRepo systemEntityRepo;
    @Autowired
    ApiEntityRepo apiEntityRepo;

    @Transactional(rollbackFor = {Exception.class})
    public void createRole(CreateRoleHostDto dto) throws DataSourceAccessException, CiaProcessorException {
        // Data Check
        boolean illegalInput = StringUtils.isEmpty(dto.getRoleId()) &&
                StringUtils.isEmpty(dto.getRoleName()) &&
                StringUtils.isEmpty(dto.getRoleType());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_ROLE_CREATE_ROLE_INCORRECT_INPUT.getCompleteMessage());
        }
        validateService.validateRoleIdOrNameDuplicate(dto.getRoleId(), dto.getRoleName());
        if (StringUtils.isNotEmpty(dto.getFabId())) {
            validateService.validateFabIdExists(dto.getFabId());
        }

        // Action
        RoleEntity newRole = RoleEntity.builder()
                .roleId(dto.getRoleId())
                .roleName(dto.getRoleName())
                .roleType(dto.getRoleType())
                .build();
        BeanUtils.copyNonNullProperties(dto, newRole);

        // Data
        roleEntityRepo.save(newRole);

        // Generate Authority
        if (StringUtils.isNotEmpty(dto.getFabId()) &&
                CollectionUtils.isNotEmpty(dto.getApiIdList())) {
            // Client Role 主表更新
            callClientApiComponent.createOrUpdateRole(dto.getFabId(), newRole);
            this.manageClientRoleAuthorityByFab(newRole, dto.getUpdateRef(), dto.getFabId(), dto.getApiIdList());
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void manageClientRoleAuthorityByFab(RoleEntity role, String updateApplyFormNo, String fabId, List<String> latestApiIdList) throws CiaProcessorException {
        // Data Preparation
        List<String> existsApiIdList = roleAuthorityEntityRepo.findApiIdByRoleIdAndFabId(role.getRoleId(), fabId);
        // 捕捉差集
        // 1.  愈刪除權限之權限清單；即原本有，更新後將消失的部分。
        List<String> waitRevokeApiIdList = (List<String>) CollectionUtils.subtract(existsApiIdList, latestApiIdList);
        // 2.  愈新建權限之權限清單；即原本沒有，更新後將加入的部分。
        List<String> waitGrantApiIdList = (List<String>) CollectionUtils.subtract(latestApiIdList, existsApiIdList);

        // Action
        // updateRoleInfo
        // revoke
        // grant

        // Role Authority 更新
        if (CollectionUtils.isNotEmpty(waitGrantApiIdList)) {
            this.grantAuthoritiesByFab(role, updateApplyFormNo, fabId, waitGrantApiIdList);
        }
        if (CollectionUtils.isNotEmpty(waitRevokeApiIdList)) {
            this.revokeAuthoritiesAndApikeyPermissionsByFab(role.getRoleId(), fabId, waitRevokeApiIdList);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    private void revokeAuthoritiesAndApikeyPermissionsByFab(String roleId, String fabId, List<String> waitRevokeApiIdList) throws CiaProcessorException {
        // 1. purge apikey permission
        // 2. purge Authority

        // 1. purge apikey permission
        List<GwApikeyEntity> onUpdateApikeyList = gwApikeyEntityRepo.findByRoleIdAndKeyPermissions(roleId, fabId, waitRevokeApiIdList);
        if (CollectionUtils.isNotEmpty(onUpdateApikeyList)) {
            for (GwApikeyEntity onUpdateKey : onUpdateApikeyList) {
                apikeyServiceForHost.revokeApikeyPermissionByFabAndApiIds
                        (fabId, onUpdateKey.getApikeyId(), waitRevokeApiIdList);
            }
        }

        // 2. purge Authority
        List<RoleAuthorityEntity> onRevokeAuthorityList = roleAuthorityEntityRepo.findByRoleIdAndFabIdAndApiIdIn(roleId, fabId, waitRevokeApiIdList);
        if (CollectionUtils.isNotEmpty(onRevokeAuthorityList)) {
            callClientApiComponent.deleteRoleAuthority(fabId, onRevokeAuthorityList);
            roleAuthorityEntityRepo.deleteAll(onRevokeAuthorityList);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    private void grantAuthoritiesByFab(RoleEntity role, @Nullable String updateFormNumber, String fabId, List<String> waitGrantApiIdList) throws CiaProcessorException {
        // 1. grant role authorities
        List<RoleAuthorityEntity> onGrantAuthorityList = new ArrayList<>();
        for (String apiId : waitGrantApiIdList) {
            RoleAuthorityEntity newAuthority = RoleAuthorityEntity.builder()
                    .fabId(fabId)
                    .roleId(role.getRoleId())
                    .apiId(apiId)
                    .applyFormNumber(StringUtils.defaultString(updateFormNumber, this.getClass().getSimpleName()))
                    .lmTime(Instant.now())
                    .lmUser(this.getClass().getSimpleName())
                    .build();
            onGrantAuthorityList.add(newAuthority);
        }
        roleAuthorityEntityRepo.saveAll(onGrantAuthorityList);
        // 新增權限採用Restful資料創建方法
        callClientApiComponent.createOrUpdateRole(fabId, role);
        callClientApiComponent.createOrUpdateRoleAuthority(fabId, onGrantAuthorityList);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void updateRole(UpdateRoleHostDto dto) throws DataSourceAccessException, CiaProcessorException {
        // Data Check
        boolean illegalInput = StringUtils.isEmpty(dto.getRoleId());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_ROLE_INCORRECT_INPUT_EMPTY_ROLE_ID.getCompleteMessage());
        }
        if (StringUtils.isNotEmpty(dto.getFabId())) {
            validateService.validateFabIdExists(dto.getFabId());
        }
        RoleEntity onUpdateRole = validateService.validateRoleId(dto.getRoleId());
        if (StringUtils.isNotEmpty(dto.getRoleName()) &&
                !StringUtils.equalsIgnoreCase(onUpdateRole.getRoleName(), dto.getRoleName())) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_ROLE_ROLE_NAME_CAN_NOT_CHANGE.getCompleteMessage());
        }
        BeanUtils.copyNonNullProperties(dto, onUpdateRole);

        // Action
        // updateRoleInfo
        // updateRoleAuthorities

        // Update Role Info
        roleEntityRepo.save(onUpdateRole);
        this.updateRoleToDeployedClients(onUpdateRole);

        // update Role Authorities
        if (StringUtils.isNotEmpty(dto.getFabId())) {
            this.manageClientRoleAuthorityByFab(onUpdateRole, dto.getUpdateRef(), dto.getFabId(), dto.getApiIdList());
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    private void updateRoleToDeployedClients(RoleEntity onUpdateRole) throws CiaProcessorException {
        List<String> deployFabList = roleAuthorityEntityRepo.findFabIdListByRoleId(onUpdateRole.getRoleId());
        if (CollectionUtils.isNotEmpty(deployFabList)) {
            callClientApiComponent.updateRoleToDeployedClients(deployFabList, onUpdateRole);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteRole(DeleteRoleHostDto dto) throws DataSourceAccessException, CiaProcessorException {
        // Data Check
        boolean illegalInput = StringUtils.isEmpty(dto.getRoleId());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_ROLE_INCORRECT_INPUT_EMPTY_ROLE_ID.getCompleteMessage());
        }
        RoleEntity onDeleteRole = validateService.validateRoleId(dto.getRoleId());

        // Data Preparation
        List<String> authorizedFabList = roleAuthorityEntityRepo.findFabIdListByRoleId(dto.getRoleId());
        List<RoleAuthorityEntity> roleAuthorityList = roleAuthorityEntityRepo.findByRoleId(dto.getRoleId());

        // Action
        // deleteApikey
        // eraseRoleAuthorities
        // eraserRoleUsrList
        // deleteRole

        // Delete Apikey including Permissions
        apikeyServiceForHost.deleteApikeyByRole(dto.getRoleId());

        // Delete Role Device
        roleDeviceServiceForHost.deleteAllRoleDeviceFromHost(dto.getRoleId());

        // Eraser Role Authorities
        this.deleteRoleAuthorityToDeployedClients(authorizedFabList, roleAuthorityList);
        roleAuthorityEntityRepo.deleteAll(roleAuthorityList);

        // Eraser Role Users, clients 不會有 role user 的資料
        roleUserEntityRepo.deleteByRoleId(dto.getRoleId());

        // Clean DB
        roleEntityRepo.delete(onDeleteRole);
    }

    @Transactional(rollbackFor = {Exception.class})
    private void deleteRoleAuthorityToDeployedClients(List<String> authorizedFabList, List<RoleAuthorityEntity> roleAuthorityList) throws CiaProcessorException {
        if (CollectionUtils.isNotEmpty(authorizedFabList) &&
                CollectionUtils.isNotEmpty(roleAuthorityList)) {
            Map<String, List<RoleAuthoroityEntityId>> roleAuthorityHm = new HashMap<>();
            for (String fabId : authorizedFabList) {
                List<RoleAuthorityEntity> specificAuthorityForSite =
                        roleAuthorityList.stream()
                                .filter(dto -> authorizedFabList.contains(dto.getFabId()))
                                .collect(Collectors.toList());
                List<RoleAuthoroityEntityId> sentInIdList =
                        specificAuthorityForSite.stream()
                                .map(obj -> new RoleAuthoroityEntityId(obj.getRoleId(), obj.getApiId(), obj.getFabId()))
                                .collect(Collectors.toList());
                roleAuthorityHm.put(fabId, sentInIdList);
            }

            callClientApiComponent.deleteRoleAuthorityToDeployedClients
                    (authorizedFabList, roleAuthorityHm);
        }
    }

    public List<CompleteRoleDto> retrieveCompleteRoleDataByFabId(Collection<String> fabIdSet) {
        // Build Result
        List<CompleteRoleDto> result = new ArrayList<>();

        // 查找資料
        List<RoleAuthorityEntity> authorityList = roleAuthorityEntityRepo.findByFabIdIn(fabIdSet);
        List<RoleDeviceEntity> deviceList = roleDeviceEntityRepo.findDistinctByFabIdIn(fabIdSet);

        if (CollectionUtils.isNotEmpty(authorityList)) {
            // 處理 Role Device By Role
            Map<String, List<RoleDeviceEntity>> roleDeviceListByRoleIdMap = new HashMap<>();
            deviceList.forEach(
                    roleDevice -> {
                        roleDeviceListByRoleIdMap.computeIfAbsent(
                                roleDevice.getRoleId(), k -> new ArrayList<>()
                        ).add(roleDevice);
                    }
            );
            Set<String> roleIdSet = authorityList.stream().map(RoleAuthorityEntity::getRoleId).collect(Collectors.toSet());
            roleIdSet.forEach(roleId ->
            {
                Optional<RoleEntity> onSearchRole = roleEntityRepo.findByRoleId(roleId);
                if (onSearchRole.isPresent()) {
                    RoleEntity targetRole = onSearchRole.get();
                    List<RoleAuthorityEntity> targetAuthorityList = authorityList.stream()
                            .filter(obj -> StringUtils.isNotEmpty(obj.getRoleId()) &&
                                    StringUtils.equals(obj.getRoleId(), roleId))
                            .collect(Collectors.toList());
                    result.add(CompleteRoleDto.builder()
                            .role(targetRole)
                            .authorityList(targetAuthorityList)
                            .deviceList(roleDeviceListByRoleIdMap.get(targetRole.getRoleId()))
                            .build());
                }
            });
        }
        return result;
    }

    public Set<String> findRoleAuthorityScopeByRoleId(String roleId) {
        return new HashSet<>(roleAuthorityEntityRepo.findFabIdListByRoleId(roleId));
    }

    public List<SystemEntity> findRoleAuthoritySystemByRoleIdAndFabIdList(String roleId, List<String> scopeList) {
        return systemEntityRepo.findRoleAuthoritySystemByRoleIdAndFabList(roleId, scopeList);
    }

    public List<ApiEntity> findRoleAuthorityApiByRoleIdAndSystemIdAndFabIdList(String roleId, String systemId, List<String> fabIdList) {
        return apiEntityRepo.findRoleAuthorityApiByRoleIdAndSystemIdAndFabIdList(roleId, systemId, fabIdList);
    }
}
