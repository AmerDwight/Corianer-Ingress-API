package tw.amer.cia.core.component.structural.property;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.gateway.ApisixProcessorException;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.ApisixPropertyFormat;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@Component
@ConfigurationProperties(prefix = "api-gateway")
@ConditionalOnProperty(name = "api-gateway.gateway-type", havingValue = "apisix")
public class ApisixProperties
{
    private String gatewayType;
    private String scheme;
    private List<ApisixPropertyFormat> deploy;
    private Map<String, ApisixPropertyFormat> deployMapByFab = new HashMap<>();
    private Map<String, ApisixPropertyFormat> deployMapBySite = new HashMap<>();

    @PostConstruct
    public void postConstruct()
    {
        if (CollectionUtils.isNotEmpty(deploy))
        {
            for (ApisixPropertyFormat env : deploy)
            {
                if (CollectionUtils.isNotEmpty(env.getFab()))
                {
                    for (String fabName : env.getFab())
                    {
                        deployMapByFab.put(fabName, env);
                    }
                }
                if (StringUtils.isNotEmpty(env.getSiteName()))
                {
                    deployMapBySite.put(env.getSiteName(), env);
                }
            }
        }
    }

    public ApisixPropertyFormat getPropertiesByFab(String fab) throws ApisixProcessorException
    {
        if (MapUtils.isNotEmpty(deployMapByFab))
        {
            if (deployMapByFab.containsKey(fab))
            {
                return deployMapByFab.get(fab);
            } else
            {
                throw ApisixProcessorException.createExceptionForHttp(HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorConstantLib.GATEWAY_PROPERTY_INVALID_DEPLOYMENT_DATA.getCompleteMessage());
            }
        } else
        {
            throw ApisixProcessorException.createExceptionForHttp(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorConstantLib.GATEWAY_PROPERTY_PROPERTY_UNLOAD.getCompleteMessage());
        }
    }
}
