package tw.amer.cia.core.service.host;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.component.functional.coriander.CallClientApiComponent;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.component.structural.property.CoreProperties;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.model.pojo.component.property.ClientDeployProperty;
import tw.amer.cia.core.model.pojo.component.property.ClientDeployPropertyFormat;
import tw.amer.cia.core.model.pojo.service.common.gateway.ExternalGatewayInfoDto;
import tw.amer.cia.core.service.core.ValidateService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@HostService
public class GatewayInfoServiceForHost {


    @Setter
    @Value("${coriander-ingress-api.setting.default-info.gateway-https:false}")
    private boolean DEFAULT_GATEWAY_HTTPS;

    @Setter
    @Value("${coriander-ingress-api.setting.default-info.normal-gateway-port:8651}")
    private int DEFAULT_GATEWAY_PORT;

    @Setter
    @Value("${coriander-ingress-api.setting.default-info.sandbox-gateway-port:8080}")
    private int DEFAULT_SANDBOX_GATEWAY_PORT;

    @Getter
    @Setter
    @Value("${coriander-ingress-api.setting.default-info.grafana-https:false}")
    private boolean DEFAULT_GRAFANA_HTTPS;

    @Getter
    @Setter
    @Value("${coriander-ingress-api.setting.default-info.grafana-port:8655}")
    private int DEFAULT_GRAFANA_PORT;

    @Getter
    @Setter
    @Value("#{${coriander-ingress-api.setting.default-info.grafana-path}}")
    private Map<String, String> DEFAULT_GRAFANA_PATH_MAP;

    @Getter
    private Map<String, Set<ExternalGatewayInfoDto>> extGwInfoSetByFabIdMap = new ConcurrentHashMap<>();

    @Autowired
    CoreProperties coreProperties;
    @Autowired
    ValidateService validateService;
    @Autowired
    CallClientApiComponent callClientApiComponent;

    @PostConstruct
    public void initData() {
        Map<String, ClientDeployPropertyFormat> clientPropertiesByFabIdMap = coreProperties.getClientDeployMapByFabId();
        for (String fabId : clientPropertiesByFabIdMap.keySet()) {
            ClientDeployPropertyFormat clientProperty = clientPropertiesByFabIdMap.get(fabId);
            for (ClientDeployProperty detailDeploy : clientProperty.getDeployList()) {
                extGwInfoSetByFabIdMap.computeIfAbsent(
                        fabId, k -> new HashSet<>()
                ).add(
                        ExternalGatewayInfoDto.builder()
                                .enableHttps(false)
                                .extGatewayHost(detailDeploy.getClientDns())
                                .extGatewayPort(
                                        validateService.validateIsNotSandBoxFab(fabId) ?
                                                DEFAULT_GATEWAY_PORT : DEFAULT_SANDBOX_GATEWAY_PORT)
                                .extGrafanaHost(detailDeploy.getClientDns())
                                .extGrafanaPort(DEFAULT_GRAFANA_PORT)
                                .fabList(Arrays.asList(fabId))
                                .build()
                );
            }
        }
    }

    public Set<ExternalGatewayInfoDto> getCompleteExternalGatewayInfoByFabId(String fabId) {
        if (this.extGwInfoSetByFabIdMap.containsKey(fabId)) {
            return this.extGwInfoSetByFabIdMap.get(fabId);
        } else {
            return new HashSet<>();
        }
    }

    public ExternalGatewayInfoDto getSingleExternalGatewayInfoByFabId(String fabId) throws DataSourceAccessException {
        return Optional.ofNullable(this.extGwInfoSetByFabIdMap)
                .map(map -> map.get(fabId))
                .filter(set -> !set.isEmpty())
                .flatMap(set -> set.stream()
                        .sorted(Comparator.comparing(ExternalGatewayInfoDto::getExtGatewayHost))
                        .findFirst())
                .orElseThrow(() -> DataSourceAccessException.createExceptionForHttp(
                        HttpStatus.NOT_FOUND,
                        ErrorConstantLib.WEB_APIKEY_PERMISSION_DATA_ERROR_FAB_GATEWAY_NO_FOUND.getCompleteMessage()
                ));
    }

    public Map<String, ExternalGatewayInfoDto> getSingleExternalGatewayInfoForAllFab() throws DataSourceAccessException {
        Map<String, ExternalGatewayInfoDto> result = new HashMap<>();
        for (String fabId : coreProperties.getClientDeployMapByFabId().keySet()) {
            result.put(fabId, getSingleExternalGatewayInfoByFabId(fabId));
        }
        return result;
    }

    public void updateExternalGatewayInfoFromClient() throws CiaProcessorException {
        List<ExternalGatewayInfoDto> dataList = callClientApiComponent.tryLoadAllClientGatewayInfoBroadcast().values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        // 先分類數據
        Map<String, Set<ExternalGatewayInfoDto>> extGwListByFab = new HashMap<>();
        dataList.forEach(
                extGw -> {
                    extGw.getFabList().forEach(
                            fabId -> {
                                extGwListByFab.computeIfAbsent(
                                        fabId, k -> new HashSet<>()
                                ).add(extGw);
                            }
                    );
                }
        );

        for (String fabId : extGwListByFab.keySet()) {
            this.getExtGwInfoSetByFabIdMap().put(fabId, extGwListByFab.get(fabId));
        }
    }
}
