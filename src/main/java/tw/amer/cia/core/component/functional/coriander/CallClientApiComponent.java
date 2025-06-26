package tw.amer.cia.core.component.functional.coriander;


import org.springframework.lang.Nullable;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.database.SystemEntity;
import tw.amer.cia.core.model.database.compositeId.RoleAuthoroityEntityId;
import tw.amer.cia.core.model.pojo.component.property.NodeStatusDTO;
import tw.amer.cia.core.model.pojo.service.common.api.CreateOrUpdateApiEndpointDto;
import tw.amer.cia.core.model.pojo.service.common.gateway.ExternalGatewayInfoDto;

import java.util.List;
import java.util.Map;

public interface CallClientApiComponent
{
    // SystemEntity 類別
    void createOrUpdateSystem(String fabId, SystemEntity dtoToClient) throws CiaProcessorException;

    void createOrUpdateSystemDeployment(String fabId, SystemDpyEntity dtoToClient) throws CiaProcessorException;

    void deleteSystemDeployment(String fabId, SystemDpyEntity dtoToClient) throws CiaProcessorException;

    void updateSystemToDeployedClients(List<String> fabList, SystemEntity dtoToClient) throws CiaProcessorException;


    // Api 類別
    void createOrUpdateApi(String fabId, ApiEntity dtoToClient) throws CiaProcessorException;

    void createOrUpdateApiEndpoint(String fabId, CreateOrUpdateApiEndpointDto dtoToClient) throws CiaProcessorException;

    void createOrUpdateApiDeployment(String fabId, ApiDpyEntity dtoToClient) throws CiaProcessorException;

    void deleteApiDeployment(String fabId, ApiDpyEntity dtoToClient) throws CiaProcessorException;

    void updateApiToDeployedClients(List<String> fabList, ApiEntity dtoToClient) throws CiaProcessorException;

    void updateApiEndpointToDeployedClients(List<String> fabList, CreateOrUpdateApiEndpointDto dtoToClient) throws CiaProcessorException;


    // Apikey
    void createOrUpdateApikeyFromHost(String fabId, GwApikeyEntity dtoToClient) throws CiaProcessorException;
    void createOrUpdateApikeyFromHostExclude(String fabId, String excludeIdentifier, GwApikeyEntity dtoToClient) throws CiaProcessorException;
    void manageApikeyPermissionFromHostBatchThroughFabId(String fabId, @Nullable List<GwApikeyPermissionEntity> revokePermissionList, @Nullable List<GwApikeyPermissionEntity> grantPermissionList) throws CiaProcessorException;
    void manageApikeyPermissionFromHostBatchThroughFabListExclude(String fabId, String excludeIdentifier, @Nullable List<GwApikeyPermissionEntity> revokePermissionList, @Nullable List<GwApikeyPermissionEntity> grantPermissionList) throws CiaProcessorException;


    // Role 類別
    void createOrUpdateRole(String fabId, RoleEntity dtoToClient) throws CiaProcessorException;

    void createOrUpdateRoleAuthority(String fabId, List<RoleAuthorityEntity> dtoToClient) throws CiaProcessorException;

    void deleteRoleAuthority(String fabId, List<RoleAuthorityEntity> dtoToClient) throws CiaProcessorException;

    void updateRoleToDeployedClients(List<String> fabList, RoleEntity dtoToClient) throws CiaProcessorException;

    void deleteRoleAuthorityToDeployedClients(List<String> fabList, Map<String, List<RoleAuthoroityEntityId>> dtoToClientHm) throws CiaProcessorException;

    void createOrUpdateRoleDeviceFromHost(String fabId, RoleDeviceEntity newDevice) throws CiaProcessorException;

    void deleteRoleDevice(String originalFabId, String deviceId)throws CiaProcessorException;

    // Broadcast 類別
    // 全播型服務，把資訊推播給所有Client
    Map<String, Boolean> tryDeleteApiBroadcast(String apiId) throws CiaProcessorException;
    Map<String, List<NodeStatusDTO>> tryCheckAllClientAliveBroadcast() throws CiaProcessorException;
    Map<String, List<ExternalGatewayInfoDto>> tryLoadAllClientGatewayInfoBroadcast() throws CiaProcessorException;
    // Gateway Plugin 類別
    Map<String, Boolean> createOrUpdateGwPluginBroadcast(GwPluginEntity onMountPlugin) throws CiaProcessorException;

    Map<String, Boolean> createOrUpdateApiPluginBroadcast(ApiGwPluginDpyEntity onMountPlugin) throws CiaProcessorException;

    Map<String, Boolean> undeployGwPluginBroadcast(String gwPluginId) throws CiaProcessorException;

    void tryUpdateApikeyActiveStatusBroadcastNoReply(String apikeyId) throws CiaProcessorException;

    void undeployApiPlugin(String fabId, String apiId, String gwPluginId) throws CiaProcessorException;


    void tryUpdateExtEntityBroadcastNoReply(ExternalSystemConfigEntity entity);

    void tryDeleteExtEntityBroadcastNoReply(String entityId);
}