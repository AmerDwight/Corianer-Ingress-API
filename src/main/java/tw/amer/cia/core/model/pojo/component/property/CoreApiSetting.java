package tw.amer.cia.core.model.pojo.component.property;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CoreApiSetting implements Serializable
{
    private String controlHeader;
    private List<String> adminKey;
    private List<String> viewerKey;
}
