package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchPluginTemplateDto implements Serializable
{
    private Map<String, Map<String, Object>> plugins;
}