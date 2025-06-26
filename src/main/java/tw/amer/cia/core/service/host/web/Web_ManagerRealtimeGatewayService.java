package tw.amer.cia.core.service.host.web;

import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.component.structural.property.CoreProperties;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.pojo.component.property.ClientDeployPropertyFormat;
import tw.amer.cia.core.model.pojo.service.common.gateway.ExternalGatewayInfoDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.realtimeGateway.Web_RealtimeGatewayGrafanaUrlPathDto;
import tw.amer.cia.core.service.core.ValidateService;
import tw.amer.cia.core.service.host.GatewayInfoServiceForHost;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@HostService
public class Web_ManagerRealtimeGatewayService {

    private final String APISIX_DASHBOARD_GRAFANA_PATH_KEY = "apisixDashboard";

    @Autowired
    CoreProperties coreProperties;

    @Autowired
    ValidateService validateService;

    @Autowired
    GatewayInfoServiceForHost gatewayInfoServiceForHost;

    public Map<String, List<Web_RealtimeGatewayGrafanaUrlPathDto>> getAllRealtimeGatewayGrafanaUrlBySite() throws DataSourceAccessException {
        Map<String, ClientDeployPropertyFormat> deployDataBySiteMap = coreProperties.getClientDeployMapBySite();
        Map<String, ExternalGatewayInfoDto> gatewayDataByFabIdMap = gatewayInfoServiceForHost.getSingleExternalGatewayInfoForAllFab();
        return deployDataBySiteMap.keySet().stream()
                .filter(siteName -> validateService.validateIsNotVirtualSite(siteName))
                .collect(
                        Collectors.toMap(
                                Function.identity(),
                                siteName -> deployDataBySiteMap.get(siteName).getFab()
                                        .stream()
                                        .map(
                                                fabId -> {
                                                    try {
                                                        ExternalGatewayInfoDto extGwInfo = gatewayDataByFabIdMap.get(fabId);
                                                        return Web_RealtimeGatewayGrafanaUrlPathDto.builder()
                                                                .fabId(fabId)
                                                                .url(
                                                                        new URL(
                                                                                gatewayInfoServiceForHost.isDEFAULT_GRAFANA_HTTPS() ? "https" : "http",
                                                                                extGwInfo.getExtGrafanaHost(),
                                                                                extGwInfo.getExtGrafanaPort(),
                                                                                Optional.ofNullable(gatewayInfoServiceForHost.getDEFAULT_GRAFANA_PATH_MAP().get(APISIX_DASHBOARD_GRAFANA_PATH_KEY))
                                                                                        .orElse("")
                                                                        ))
                                                                .build();
                                                    } catch (MalformedURLException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                        )
                                        .collect(Collectors.toList())
                        )
                );
    }
}
