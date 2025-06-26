package tw.amer.cia.core.component.structural.property;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.model.pojo.component.property.ClientDeployPropertyFormat;
import tw.amer.cia.core.model.pojo.component.property.ClientPropertyFormat;
import tw.amer.cia.core.model.pojo.component.property.CoreSettingPropertyFormat;
import tw.amer.cia.core.model.pojo.component.property.HostPropertyFormat;
import tw.amer.cia.core.service.core.ValidateService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;


@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "coriander-ingress-api")
public class CoreProperties {

    @Value("${coriander-ingress-api.setting.identify-header}")
    String identifyHeader;

    @Autowired
    ValidateService validateService;

    private CoreSettingPropertyFormat setting;
    private HostPropertyFormat host;
    private ClientPropertyFormat client;
    private String selfIdentify = "Undetermined-Identifier";

    private Map<String, ClientDeployPropertyFormat> clientDeployMapByFabId = new HashMap<>();
    private Map<String, ClientDeployPropertyFormat> clientDeployMapBySite = new HashMap<>();

    @PostConstruct
    public void postConstruct() {
        if (CollectionUtils.isNotEmpty(client.getDeploy())) {
            for (ClientDeployPropertyFormat env : client.getDeploy()) {
                if (CollectionUtils.isNotEmpty(env.getFab())) {
                    for (String fabId : env.getFab()) {
                        clientDeployMapByFabId.put(fabId, env);
                    }
                }
                if (StringUtils.isNotEmpty(env.getSiteName())) {
                    clientDeployMapBySite.put(env.getSiteName(), env);
                }
            }
        }
        selfIdentify = (StringUtils.isNotEmpty(setting.getIdentify())) ? setting.getIdentify() : selfIdentify;
    }

    public ClientDeployPropertyFormat getClientPropertiesByFab(String fabId) throws CiaProcessorException {
        if (MapUtils.isNotEmpty(clientDeployMapByFabId)) {
            if (clientDeployMapByFabId.containsKey(fabId)) {
                return clientDeployMapByFabId.get(fabId);
            } else {
                throw CiaProcessorException.createExceptionForHttp(HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorConstantLib.CORE_PROPERTY_INVALID_DEPLOYMENT_DATA.getCompleteMessage());
            }
        } else {
            throw CiaProcessorException.createExceptionForHttp(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorConstantLib.CORE_PROPERTY_PROPERTY_UNLOAD.getCompleteMessage());
        }
    }

    public ClientDeployPropertyFormat getClientPropertiesBySite(String Site) throws CiaProcessorException {
        if (MapUtils.isNotEmpty(clientDeployMapBySite)) {
            if (clientDeployMapBySite.containsKey(Site)) {
                return clientDeployMapBySite.get(Site);
            } else {
                throw CiaProcessorException.createExceptionForHttp(HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorConstantLib.CORE_PROPERTY_INVALID_DEPLOYMENT_DATA.getCompleteMessage());
            }
        } else {
            throw CiaProcessorException.createExceptionForHttp(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorConstantLib.CORE_PROPERTY_PROPERTY_UNLOAD.getCompleteMessage());
        }
    }

    public Map<String, List<String>> fabIdListTransferTofabIdsBySiteMap(Collection<String> fabIdList) {
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
}
