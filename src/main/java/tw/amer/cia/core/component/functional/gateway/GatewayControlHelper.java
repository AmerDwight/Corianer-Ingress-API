package tw.amer.cia.core.component.functional.gateway;

import tw.amer.cia.core.exception.gateway.ApisixProcessorException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.ApiEndpointEntity;
import tw.amer.cia.core.model.database.ApiGwPluginDpyEntity;
import tw.amer.cia.core.model.database.GwPluginEntity;
import tw.amer.cia.core.model.database.SystemDpyEntity;
import tw.amer.cia.core.model.pojo.component.gateway.GwApikeyNameDto;
import tw.amer.cia.core.model.pojo.service.common.api.ApiNameDto;
import tw.amer.cia.core.model.pojo.service.common.gateway.ExternalGatewayInfoListDto;

import java.util.List;

public interface GatewayControlHelper {
    boolean checkAllGatewayAlive();

    boolean cleanUpGatewayInfo() throws ApisixProcessorException;

    boolean setupAllGatewayGlobalRules() throws ApisixProcessorException;

    boolean createGwApikeyWithFab(String targetFabId, String roleId, String apikeyId, String keyName) throws GatewayControllerException;

    boolean createGwApikeyBySite(String roleId, String apikeyId, String keyName) throws GatewayControllerException;

    boolean deleteGwApikey(String roleId, String apikeyName) throws GatewayControllerException;

    boolean revokeGwApikeyPermission(String targetFabId, String gwRouteId, String roleId, String apikeyName) throws GatewayControllerException;

    boolean grantGwApikeyPermission(String targetFabId, String gwRouteId, String roleId, String apikeyName) throws GatewayControllerException;

    boolean patchGwRouteDeviceIpListBatch(String fabId, List<String> gwRouteIdList, List<String> onGrantDeviceIpList, List<String> onRevokeDeviceIpList) throws GatewayControllerException;

    boolean createOrUpdateGwUpstream(String gwUpstreamId, String systemName, SystemDpyEntity sysDeploy) throws GatewayControllerException;

    boolean deleteGwUpstream(String undeployFabId, String gwUpstreamId) throws GatewayControllerException;

    boolean createGwRoute(String deployFabId, String gwUpstreamId, String gwRouteId, ApiNameDto deployApiName, ApiEndpointEntity endpoint) throws GatewayControllerException;

    boolean deleteGwRoute(String undeployFabId, String gwRouteId) throws GatewayControllerException;

    boolean updateGwRoute(String fabId, String gwRouteId, String systemName, ApiEndpointEntity endpoint) throws GatewayControllerException;

    boolean updateGwRoutePermissionBatch(String fabId, String gwRouteId, List<GwApikeyNameDto> onInitialNameList) throws GatewayControllerException;

    String getGwSimpleRoutePath(String fabId, String systemName, String endpoint);

    String getExternalGwCompleteRoutePath(String fabId, String systemName, String endpointUri) throws GatewayControllerException;

    boolean deployOrUpdateGwPartialPlugin(String fabId, String gwRouteId, GwPluginEntity onMountPlugin, ApiGwPluginDpyEntity pluginDpyData) throws GatewayControllerException;

    boolean undeployGwPlugin(String fabId, String gwRouteId, GwPluginEntity undeployPlugin, ApiGwPluginDpyEntity pluginDpyData) throws GatewayControllerException;

    void changeApikeyStatus(String fabId, String roleId, String apikeyId, String apikeyName, boolean isActive) throws ApisixProcessorException;

    ExternalGatewayInfoListDto provideExternalGatewayInfoList();

}
