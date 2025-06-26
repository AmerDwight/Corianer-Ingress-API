package tw.amer.cia.core.service.host.api;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import tw.amer.cia.core.common.DataModifyAction;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.SuccessConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.ApiEntity;
import tw.amer.cia.core.model.database.RoleEntity;
import tw.amer.cia.core.model.database.SystemEntity;
import tw.amer.cia.core.model.database.dao.ApiEntityRepo;
import tw.amer.cia.core.model.database.dao.RoleAuthorityEntityRepo;
import tw.amer.cia.core.model.pojo.service.common.api.*;
import tw.amer.cia.core.model.pojo.service.common.apikey.CreateApikeyHostDto;
import tw.amer.cia.core.model.pojo.service.common.apikey.DeleteApikeyHostDto;
import tw.amer.cia.core.model.pojo.service.common.apikey.UpdateApikeyHostDto;
import tw.amer.cia.core.model.pojo.service.common.role.*;
import tw.amer.cia.core.model.pojo.service.common.system.CreateSystemHostDto;
import tw.amer.cia.core.model.pojo.service.common.system.DeleteSystemHostDto;
import tw.amer.cia.core.model.pojo.service.common.system.SystemDeploymentDto;
import tw.amer.cia.core.model.pojo.service.common.system.UpdateSystemHostDto;
import tw.amer.cia.core.model.pojo.service.host.control.*;
import tw.amer.cia.core.service.core.ValidateService;
import tw.amer.cia.core.service.host.ApiServiceForHost;
import tw.amer.cia.core.service.host.ApikeyServiceForHost;
import tw.amer.cia.core.service.host.RoleServiceForHost;
import tw.amer.cia.core.service.host.SystemServiceForHost;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CREATE、UPDATE、DELETE 類API，處理邏輯放在對應 Service
 */
@HostService
public class HostAdminApiService {
    @Autowired
    ApikeyServiceForHost apikeyServiceForHost;

    @Autowired
    SystemServiceForHost systemServiceForHost;

    @Autowired
    ApiServiceForHost apiServiceForHost;

    @Autowired
    ValidateService validateService;

    @Autowired
    RoleServiceForHost roleServiceForHost;

    @Autowired
    ApiEntityRepo apiEntityRepo;

    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;

    public Object maintainApikey(MaintainApikeyInHostDto inDto) throws DataSourceAccessException, CiaProcessorException, GatewayControllerException {
        if (StringUtils.isEmpty(inDto.getMaintainAction())) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_KEY_EMPTY_MAINTAIN_KEY_ACTION.getCompleteMessage());
        }
        switch (StringUtils.defaultString(inDto.getMaintainAction()).toUpperCase()) {
            case DataModifyAction.MAINTAIN_ACTION_CREATE:
                return this.maintainNewApikey(inDto);

            case DataModifyAction.MAINTAIN_ACTION_RETRIEVE:
                return this.maintainRetrieveApikey(inDto);

            case DataModifyAction.MAINTAIN_ACTION_UPDATE:
                return this.maintainUpdateApikey(inDto);

            case DataModifyAction.MAINTAIN_ACTION_DELETE:
                return this.maintainDeleteApikey(inDto);

            default:
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.API_KEY_INVALID_MAINTAIN_KEY_ACTION.getCompleteMessage());
        }
    }

    private Object maintainNewApikey(MaintainApikeyInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        CreateApikeyHostDto createApikeyHostDto = new CreateApikeyHostDto();
        BeanUtils.copyProperties(inDto, createApikeyHostDto);

        // Transfer { apiName, systemName } to { apiId }
        List<String> permissionApiIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(inDto.getPermissions())) {
            for (SimpleAuthority permissionObj : inDto.getPermissions()) {
                Optional<String> apiId = apiEntityRepo.findApiIdByName(permissionObj.getSystemName(), permissionObj.getApiName());
                if (apiId.isPresent()) {
                    permissionApiIdList.add(apiId.get());
                }
            }
        }
        createApikeyHostDto.setApiIdList(permissionApiIdList);
        if (CollectionUtils.isNotEmpty(permissionApiIdList)) {
            validateService.validateRoleAuthorities(inDto.getRoleId(), inDto.getFabId(), permissionApiIdList);
        }

        // Calling Action
        String createdKeyId = apikeyServiceForHost.createApikeyFromHost(createApikeyHostDto);

        if (StringUtils.isNotEmpty(createdKeyId)) {
            // Assemble message
            return MaintainNewApikeyOutHostDto.builder()
                    .apikey(createdKeyId)
                    .fabId(inDto.getFabId()).build();
        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.SERVICE_UNAVAILABLE,
                    ErrorConstantLib.UNKNOWN_EXCEPTION_API_ERROR.getCompleteMessage());
        }
    }

    private Object maintainRetrieveApikey(MaintainApikeyInHostDto inDto) throws DataSourceAccessException, GatewayControllerException {
        boolean illegalInput = StringUtils.isEmpty(inDto.getFabId()) || StringUtils.isEmpty(inDto.getApikeyId());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_KEY_INCORRECT_MAINTAIN_KEY_RETRIEVE_INPUT.getCompleteMessage());
        }
        validateService.validateApikeyByApikeyId(inDto.getApikeyId());

        // Calling Action
        List<ListApikeyPermissionDto> keyPermissionList = apikeyServiceForHost.retrieveApikeyInfoByFabAndApikeyId(inDto.getFabId(), inDto.getApikeyId());

        // Assemble message
        return MaintainRetrieveApikeyOutHostDto.builder()
                .apikey(inDto.getApikeyId())
                .fabId(inDto.getFabId())
                .permissions(keyPermissionList).build();
    }

    private Object maintainUpdateApikey(MaintainApikeyInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        UpdateApikeyHostDto updateApikeyHostDto = new UpdateApikeyHostDto();
        BeanUtils.copyProperties(inDto, updateApikeyHostDto);

        // Transfer { apiName, systemName } to { apiId }
        List<String> permissionApiIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(inDto.getPermissions())) {
            for (SimpleAuthority permissionObj : inDto.getPermissions()) {
                Optional<String> apiId = apiEntityRepo.findApiIdByName(permissionObj.getSystemName(), permissionObj.getApiName());
                if (apiId.isPresent()) {
                    permissionApiIdList.add(apiId.get());
                } else {
                    throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                            ErrorConstantLib.API_DOES_NOT_EXISTS.getCompleteMessage());
                }
            }
        }
        updateApikeyHostDto.setApiIdList(permissionApiIdList);
        if (CollectionUtils.isNotEmpty(permissionApiIdList)) {
            validateService.validateRoleAuthorities(inDto.getRoleId(), inDto.getFabId(), permissionApiIdList);
        }
        // Calling Action
        apikeyServiceForHost.updateApikeyFromHost(updateApikeyHostDto);

        return MaintainUpdateApikeyOutHostDto.builder()
                .apikey(updateApikeyHostDto.getApikeyId())
                .fabId(updateApikeyHostDto.getFabId()).build();
    }

    private Object maintainDeleteApikey(MaintainApikeyInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
//        deleteApikey
        DeleteApikeyHostDto deleteApikeyDto = new DeleteApikeyHostDto();
        BeanUtils.copyProperties(inDto, deleteApikeyDto);

        // Calling Action
        apikeyServiceForHost.deleteApikeyFromHost(deleteApikeyDto);

        MaintainDeleteApikeyOutHostDto returnMessage = MaintainDeleteApikeyOutHostDto.builder()
                .deletedApikey(deleteApikeyDto.getApikeyId())
                .build();
        returnMessage.setRtnMessage(SuccessConstantLib.HOST_PURGE_KEY_ALL_FAB.getMessage());

        return returnMessage;
    }

    public Object maintainSystem(MaintainSystemInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        if (StringUtils.isEmpty(inDto.getMaintainAction())) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_KEY_EMPTY_MAINTAIN_KEY_ACTION.getCompleteMessage());
        }
        switch (StringUtils.defaultString(inDto.getMaintainAction()).toUpperCase()) {
            case DataModifyAction.MAINTAIN_ACTION_CREATE:
                return this.maintainNewSystem(inDto);

            case DataModifyAction.MAINTAIN_ACTION_RETRIEVE:
                return this.maintainRetrieveSystem(inDto);

            case DataModifyAction.MAINTAIN_ACTION_UPDATE:
                return this.maintainUpdateSystem(inDto);

            case DataModifyAction.MAINTAIN_ACTION_DELETE:
                return this.maintainDeleteSystem(inDto);

            default:
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.API_KEY_INVALID_MAINTAIN_KEY_ACTION.getCompleteMessage());
        }
    }

    private MaintainNewSystemOutHostDto maintainNewSystem(MaintainSystemInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        CreateSystemHostDto createSystemHostDto = new CreateSystemHostDto();
        BeanUtils.copyProperties(inDto, createSystemHostDto);

        // Calling Action
        systemServiceForHost.createSystem(createSystemHostDto);

        // Assemble message
        return MaintainNewSystemOutHostDto.builder().build();
    }

    private Object maintainRetrieveSystem(MaintainSystemInHostDto inDto) throws DataSourceAccessException {
        boolean illegalInput = StringUtils.isEmpty(inDto.getSystemName());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_SYSTEM_SYSTEM_NAME_INCORRECT_INPUT.getCompleteMessage());
        }

        // Calling Action
        SystemEntity inSearchSystem = validateService.validateSystemName(inDto.getSystemName());
        List<SystemDeploymentDto> deployList = systemServiceForHost.retrieveSystemDeploymentList(inSearchSystem.getSystemId());

        return MaintainRetrieveSystemOutHostDto.builder()
                .systemName(inSearchSystem.getSystemName())
                .serviceLevel(inSearchSystem.getServiceLevel())
                .owner(inSearchSystem.getOwner())
                .activeStatus(inSearchSystem.getActiveStatus())
                .deployment(deployList).build();
    }

    private Object maintainUpdateSystem(MaintainSystemInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        UpdateSystemHostDto updateSystemHostDto = new UpdateSystemHostDto();
        BeanUtils.copyNonNullProperties(inDto, updateSystemHostDto);

        // Calling Action
        systemServiceForHost.updateSystem(updateSystemHostDto);

        // Assemble message
        return MaintainUpdateSystemOutHostDto.builder().build();

    }

    private Object maintainDeleteSystem(MaintainSystemInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        // deleteSystem
        DeleteSystemHostDto deleteSystemHostDto = new DeleteSystemHostDto();
        BeanUtils.copyNonNullProperties(inDto, deleteSystemHostDto);

        // Calling Action
        systemServiceForHost.deleteSystem(deleteSystemHostDto);

        // Assemble message
        MaintainDeleteSystemOutHostDto returnMessage = MaintainDeleteSystemOutHostDto.builder()
                .deletedSystem(inDto.getSystemName())
                .build();
        return returnMessage;
    }

    public Object maintainApi(MaintainApiInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        if (StringUtils.isEmpty(inDto.getMaintainAction())) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_KEY_EMPTY_MAINTAIN_KEY_ACTION.getCompleteMessage());
        }
        switch (StringUtils.defaultString(inDto.getMaintainAction()).toUpperCase()) {
            case DataModifyAction.MAINTAIN_ACTION_CREATE:
                return this.maintainNewApi(inDto);

            case DataModifyAction.MAINTAIN_ACTION_RETRIEVE:
                return this.maintainRetrieveApi(inDto);

            case DataModifyAction.MAINTAIN_ACTION_UPDATE:
                return this.maintainUpdateApi(inDto);

            case DataModifyAction.MAINTAIN_ACTION_DELETE:
                return this.maintainDeleteApi(inDto);

            default:
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.API_KEY_INVALID_MAINTAIN_KEY_ACTION.getCompleteMessage());
        }
    }

    private Object maintainNewApi(MaintainApiInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        CreateApiHostDto createApiHostDto = new CreateApiHostDto();
        BeanUtils.copyProperties(inDto, createApiHostDto);

        // Calling Action
        boolean createSuccess = apiServiceForHost.createApi(createApiHostDto);

        if (createSuccess) {
            // Assemble message
//            return MaintainNewSystemOutDto.builder().build();
            return MaintainUpdateApiOutHostDto.builder().build();
        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.SERVICE_UNAVAILABLE,
                    ErrorConstantLib.UNKNOWN_EXCEPTION_API_ERROR.getCompleteMessage());
        }
    }

    private Object maintainRetrieveApi(MaintainApiInHostDto inDto) throws DataSourceAccessException {
        boolean illegalInput = StringUtils.isEmpty(inDto.getSystemName()) || StringUtils.isEmpty(inDto.getApiName());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_API_NAME_INCORRECT_INPUT.getCompleteMessage());
        }

        // Calling Action
        ApiEntity inSearchApi = validateService.validateApiByName(inDto.getSystemName(), inDto.getApiName());
        List<ApiEndpointDto> endpointList = apiServiceForHost.retrieveApiEndpointInfoClientApi(inSearchApi.getApiId());
        List<ApiDeployedFabDto> deploymentList = apiServiceForHost.retrieveApiDeploymentInfoClientApi(inSearchApi.getApiId());

        MaintainRetrieveApiOutHostDto outDto = MaintainRetrieveApiOutHostDto.builder()
                .endpoint(endpointList)
                .deployment(deploymentList)
                .build();
        BeanUtils.copyNonNullProperties(inSearchApi, outDto);
        return outDto;
    }

    private Object maintainUpdateApi(MaintainApiInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        UpdateApiHostDto updateupdateApiHostDto = new UpdateApiHostDto();
        BeanUtils.copyNonNullProperties(inDto, updateupdateApiHostDto);

        // Calling Action
        apiServiceForHost.updateApi(updateupdateApiHostDto);

        return MaintainUpdateApiOutHostDto.builder().build();
    }

    private Object maintainDeleteApi(MaintainApiInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        // delete Api
        DeleteApiHostDto deleteApiHostDto = new DeleteApiHostDto();
        BeanUtils.copyNonNullProperties(inDto, deleteApiHostDto);

        // Calling Action
        boolean complete = apiServiceForHost.deleteApi(deleteApiHostDto);

        if (complete) {
            // Assemble message
            MaintainDeleteApiOutHostDto returnMessage = MaintainDeleteApiOutHostDto.builder()
                    .systemName(deleteApiHostDto.getSystemName())
                    .deletedApi(deleteApiHostDto.getApiName())
                    .build();
            return returnMessage;
        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.SERVICE_UNAVAILABLE,
                    ErrorConstantLib.UNKNOWN_EXCEPTION_API_ERROR.getCompleteMessage());
        }
    }

    public Object maintainRole(MaintainRoleInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        if (StringUtils.isEmpty(inDto.getMaintainAction())) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_KEY_EMPTY_MAINTAIN_KEY_ACTION.getCompleteMessage());
        }
        switch (StringUtils.defaultString(inDto.getMaintainAction()).toUpperCase()) {
            case DataModifyAction.MAINTAIN_ACTION_CREATE:
                return this.maintainNewRole(inDto);

            case DataModifyAction.MAINTAIN_ACTION_RETRIEVE:
                return this.maintainRetrieveRole(inDto);

            case DataModifyAction.MAINTAIN_ACTION_UPDATE:
                return this.maintainUpdateRole(inDto);

            case DataModifyAction.MAINTAIN_ACTION_DELETE:
                return this.maintainDeleteRole(inDto);

            default:
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.API_KEY_INVALID_MAINTAIN_KEY_ACTION.getCompleteMessage());
        }
    }

    private Object maintainRetrieveRole(MaintainRoleInHostDto inDto) throws DataSourceAccessException {
        boolean illegalInput = StringUtils.isEmpty(inDto.getRoleId());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_ROLE_INCORRECT_INPUT_EMPTY_ROLE_ID.getCompleteMessage());
        }
        RoleEntity retrieveRole = validateService.validateRoleId(inDto.getRoleId());
        // Data Preparation
        List<String> authorizedFabList = roleAuthorityEntityRepo.findFabIdListByRoleId(inDto.getRoleId());
        List<RoleFabScopeDto> fabScopeList = new ArrayList<>();

        for (String fabId : authorizedFabList) {
            RoleFabScopeDto fabScope = RoleFabScopeDto.builder()
                    .fabId(fabId)
                    .authorities(roleAuthorityEntityRepo.findAuthorizedSimpleAuthorityDtoByRoleIdAndFabId(
                            inDto.getRoleId(), fabId
                    )).build();
            fabScopeList.add(fabScope);
        }
        MaintainRetrieveRoleOutHostDto outDto = MaintainRetrieveRoleOutHostDto.builder()
                .roleId(inDto.getRoleId())
                .fabScope(fabScopeList).build();
        BeanUtils.copyNonNullProperties(retrieveRole, outDto);
        return outDto;
    }

    private Object maintainDeleteRole(MaintainRoleInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        // delete Role
        DeleteRoleHostDto deleteRoleHostDto = new DeleteRoleHostDto();
        BeanUtils.copyNonNullProperties(inDto, deleteRoleHostDto);

        try {
            // Calling Action
            roleServiceForHost.deleteRole(deleteRoleHostDto);

            MaintainDeleteRoleOutHostDto returnMessage = MaintainDeleteRoleOutHostDto.builder()
                    .deletedRole(deleteRoleHostDto.getRoleId())
                    .build();
            return returnMessage;
        } catch (RuntimeException e) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.SERVICE_UNAVAILABLE,
                    ErrorConstantLib.UNKNOWN_EXCEPTION_API_ERROR.getCompleteMessage());
        }
    }

    private Object maintainUpdateRole(MaintainRoleInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        UpdateRoleHostDto updateRoleHostDto = new UpdateRoleHostDto();
        BeanUtils.copyProperties(inDto, updateRoleHostDto);

        // Transfer { apiName, systemName } to { apiId }
        List<String> authoritiesApiIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(inDto.getAuthorities())) {
            for (SimpleAuthority authorityObj : inDto.getAuthorities()) {
                ApiEntity onGrantMs = validateService.validateApiByName(authorityObj.getSystemName(), authorityObj.getApiName());
                authoritiesApiIdList.add(onGrantMs.getApiId());
            }
        }
        updateRoleHostDto.setApiIdList(authoritiesApiIdList);

        // Calling Action
        roleServiceForHost.updateRole(updateRoleHostDto);

        return MaintainNewRoleOutHostDto.builder().build();
    }

    private Object maintainNewRole(MaintainRoleInHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        CreateRoleHostDto createRoleHostDto = new CreateRoleHostDto();
        BeanUtils.copyProperties(inDto, createRoleHostDto);

        // Transfer { apiName, systemName } to { apiId }
        List<String> authoritiesApiIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(inDto.getAuthorities())) {
            for (SimpleAuthority authorityObj : inDto.getAuthorities()) {
                ApiEntity onGrantMs = validateService.validateApiByName(authorityObj.getSystemName(), authorityObj.getApiName());
                authoritiesApiIdList.add(onGrantMs.getApiId());
            }
        }
        createRoleHostDto.setApiIdList(authoritiesApiIdList);

        // Calling Action
        roleServiceForHost.createRole(createRoleHostDto);

        // Assemble message
        return MaintainNewRoleOutHostDto.builder().build();
    }
}
