package tw.amer.cia.core.component.functional.gateway.watcher;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.component.functional.gateway.GatewayCommandProxy;
import tw.amer.cia.core.component.structural.httpClient.HttpRequestSender;
import tw.amer.cia.core.component.structural.property.ApisixProperties;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.ApiEntity;
import tw.amer.cia.core.model.database.SystemDpyEntity;
import tw.amer.cia.core.model.database.dao.SystemDpyEntityRepo;
import tw.amer.cia.core.model.database.ApiEndpointEntity;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.ApisixPropertyFormat;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.GwRoutePropertyCommandDto;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin.ProxyRewritePluginDto;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin.RoutePluginsDto;
import tw.amer.cia.core.model.pojo.service.common.api.ApiNameDto;
import tw.amer.cia.core.service.core.ValidateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Aspect
@Slf4j
@Component
@ConditionalOnExpression("'${api-gateway.gateway-type}'=='apisix' && '${coriander-ingress-api.setting.deploy-type}'=='client'")
public class ApisixRouteControlWatcher extends HttpRequestSender implements GatewayRouteControlWatcher {

    @Autowired
    ValidateService validateService;

    @Autowired
    ApisixProperties configProperties;

    @Autowired
    GatewayCommandProxy gatewayCommandProxy;

    @Autowired
    SystemDpyEntityRepo cSysDpyRepo;

    @Override
    @Pointcut("execution(* tw.amer.cia.core.gateway.functional.component.GatewayControlHelper+.createGwRoute(..))")
    public void createGwRouteWatcher() {
    }


    @After("createGwRouteWatcher() && args(deployFabId, gwUpstreamId, gwRouteId, deployApiName, endpoint)")
    public void afterCreateGwRoute(String deployFabId, String gwUpstreamId, String gwRouteId,
                                   ApiNameDto deployApiName, ApiEndpointEntity endpoint) throws GatewayControllerException, DataSourceAccessException {
        log.info("Watcher: After createGwRoute execution");

        // 記錄所有輸入參數
        log.debug("Water Debug. Input parameters: deployFabId={}, gwUpstreamId={}, gwRouteId={}",
                deployFabId, gwUpstreamId, gwRouteId);
        log.debug("Water Debug. deployApiName={}", deployApiName);
        log.debug("Water Debug. endpoint={}", endpoint);

        // 檢查endpoint是否為null
        if (endpoint == null) {
            log.debug("Water Debug. Endpoint is null, cannot proceed");
            return;
        }

        // 檢查apiId是否為null
        if (endpoint.getApiId() == null) {
            log.error("Water Debug. Endpoint apiId is null, cannot proceed");
            return;
        }

        // 獲取並檢查relatedMs
        ApiEntity relatedApi = validateService.validateApiByApiId(endpoint.getApiId());
        if (relatedApi == null) {
            log.error("Water Debug. relatedApi is null after validation");
            return;
        }

        // 檢查systemId
        if (relatedApi.getSystemId() == null) {
            log.error("Water Debug. relatedApi.systemId is null");
            return;
        }

        // 查詢部署信息並檢查結果
        Optional<SystemDpyEntity> sysDpyOptional = cSysDpyRepo.findBySystemIdAndFabId(relatedApi.getSystemId(), deployFabId);
        if (!sysDpyOptional.isPresent()) {
            log.error("Water Debug. No SystemDpyEntity record found for systemId={}, fabId={}",
                    relatedApi.getSystemId(), deployFabId);
            return;
        }
        SystemDpyEntity relatedSysDpy = sysDpyOptional.get();

        // 檢查是否需要代理重定向
        boolean requireProxyRedirect = checkGwRouteRequireProxyRedirect(relatedSysDpy);
        log.info("Water: Proxy redirect required: {}", requireProxyRedirect);

        if (requireProxyRedirect) {
            log.debug("Water Debug. Processing gwRoute proxy redirect for gwRouteId={}", gwRouteId);
            processGwRouteRequireProxyRedirect(deployFabId, gwRouteId, relatedSysDpy);
            log.debug("Water Debug. Proxy redirect processing completed successfully");
        } else {
            log.debug("Water Debug. No proxy redirect required, process completed");
        }
    }


    @Override
    @Pointcut("execution(* tw.amer.cia.core.component.functional.gateway.GatewayControlHelper+.updateGwRoute(..))")
    public void updateGwRouteWatcher() {
    }

    @After("updateGwRouteWatcher() && args(deployFabId, gwRouteId, systemName, endpoint)")
    public void afterUpdateGwRoute(String deployFabId, String gwRouteId, String systemName, ApiEndpointEntity endpoint) throws GatewayControllerException, DataSourceAccessException {
        log.info("Watcher: After updateGwRoute execution");
        ApiEntity relatedMs = validateService.validateApiByApiId(endpoint.getApiId());
        SystemDpyEntity relatedSysDpy = cSysDpyRepo.findBySystemIdAndFabId(relatedMs.getSystemId(), deployFabId).get();

        if (checkGwRouteRequireProxyRedirect(relatedSysDpy)) {
            processGwRouteRequireProxyRedirect(deployFabId, gwRouteId, relatedSysDpy);
        }
    }


    private boolean checkGwRouteRequireProxyRedirect(SystemDpyEntity relatedSysDpy) {
        return StringUtils.isNotBlank(relatedSysDpy.getProxyRequired()) &&
                StringUtils.equalsIgnoreCase(relatedSysDpy.getProxyRequired(), GeneralSetting.GENERAL_POSITIVE_STRING);
    }

    private void processGwRouteRequireProxyRedirect(String deployFabId, String gwRouteId, SystemDpyEntity sysDpy) throws GatewayControllerException, DataSourceAccessException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(deployFabId);

        if (StringUtils.isNotBlank(connectionInfo.getGatewayProxyRedirectHeaderHost()) &&
                StringUtils.isNotBlank(connectionInfo.getGatewayProxyRedirectHeaderPort())) {
            Map<String, String> gprHeaders = new HashMap<>();
            gprHeaders.put(connectionInfo.getGatewayProxyRedirectHeaderHost(), sysDpy.getSystemHost());
            gprHeaders.put(connectionInfo.getGatewayProxyRedirectHeaderPort(), String.valueOf(sysDpy.getSystemPort()));

            // Build Command
            GwRoutePropertyCommandDto gwRoutePropertyCommandDto =
                    GwRoutePropertyCommandDto.builder()
                            .plugins(
                                    RoutePluginsDto.builder()
                                            .proxyRewritePluginDto(
                                                    ProxyRewritePluginDto.builder()
                                                            .headers(gprHeaders)
                                                            .build())
                                            .build())
                            .build();
            gatewayCommandProxy.patchGwRouteCommand(deployFabId, gwRoutePropertyCommandDto, gwRouteId);
        } else {
            throw new DataSourceAccessException(
                    ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getCompleteMessage()
                            + "GatewayProxyRedirectHeaderHost/ GatewayProxyRedirectHeaderPort");
        }
    }

}
