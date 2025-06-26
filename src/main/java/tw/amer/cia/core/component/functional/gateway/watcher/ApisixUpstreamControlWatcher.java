package tw.amer.cia.core.component.functional.gateway.watcher;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.component.functional.gateway.GatewayCommandProxy;
import tw.amer.cia.core.component.structural.httpClient.HttpRequestSender;
import tw.amer.cia.core.component.structural.property.ApisixProperties;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.GwRouteEntity;
import tw.amer.cia.core.model.database.SystemDpyEntity;
import tw.amer.cia.core.model.database.dao.ApiEntityRepo;
import tw.amer.cia.core.model.database.dao.GwRouteEntityRepo;
import tw.amer.cia.core.model.database.ApiEntity;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.ApisixPropertyFormat;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.GwRoutePropertyCommandDto;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin.ProxyRewritePluginDto;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin.RoutePluginsDto;
import tw.amer.cia.core.service.core.ValidateService;

import java.util.*;

@Aspect
@Slf4j
@Component
@ConditionalOnExpression("'${api-gateway.gateway-type}'=='apisix' && '${coriander-ingress-api.setting.deploy-type}'=='client'")
public class ApisixUpstreamControlWatcher extends HttpRequestSender implements GatewayUpstreamControlWatcher {

    @Autowired
    ValidateService validateService;

    @Autowired
    ApisixProperties configProperties;

    @Autowired
    GatewayCommandProxy gatewayCommandProxy;

    @Autowired
    ApiEntityRepo apiEntityRepo;

    @Autowired
    GwRouteEntityRepo gwRouteEntityRepo;

    @Override
    @Pointcut("execution(* tw.amer.cia.core.component.functional.gateway.GatewayControlHelper+.createOrUpdateGwUpstream(..))")
    public void createOrUpdateUpstreamWatcher() {
    }


    @After("createOrUpdateUpstreamWatcher() && args(gwUpstreamId, systemName, sysDeploy)")
    public void afterCreateOrUpdateUpstream(String gwUpstreamId, String systemName, SystemDpyEntity sysDeploy) throws GatewayControllerException, DataSourceAccessException {
        log.info("Watcher: After CreateOrUpdateUpstream execution");
        if (sysDeploy != null) {
            procedureForProxyRequired(sysDeploy);
        }
    }

    private void procedureForProxyRequired(SystemDpyEntity sysDeploy) throws GatewayControllerException, DataSourceAccessException {
        if (sysDeploy.getProxyRequired().equalsIgnoreCase(GeneralSetting.GENERAL_POSITIVE_STRING)) {
            List<ApiEntity> relatedApiList = apiEntityRepo.findBySystemId(sysDeploy.getSystemId());
            Collection<GwRouteEntity> onPatchGwRoutes = new ArrayList<>();

            for (ApiEntity apiEntity : relatedApiList) {
                onPatchGwRoutes.addAll(gwRouteEntityRepo.findByApiId(apiEntity.getApiId()));
            }

            if (CollectionUtils.isNotEmpty(onPatchGwRoutes)) {
                for (GwRouteEntity rGwRoute : onPatchGwRoutes) {
                    this.processGwRouteRequireProxyRedirect(rGwRoute, sysDeploy);
                }
            }
        }
    }

    private void processGwRouteRequireProxyRedirect(GwRouteEntity rGwRoute, SystemDpyEntity sysDpy) throws GatewayControllerException, DataSourceAccessException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(rGwRoute.getFabId());

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
            gatewayCommandProxy.patchGwRouteCommand(rGwRoute.getFabId(), gwRoutePropertyCommandDto, rGwRoute.getGwRouteId());
        } else {
            throw new DataSourceAccessException(
                    ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getCompleteMessage()
                            + "GatewayProxyRedirectHeaderHost/ GatewayProxyRedirectHeaderPort");
        }
    }

}
