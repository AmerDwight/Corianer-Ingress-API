package tw.amer.cia.core.model.pojo.component.property;

import lombok.Data;

import java.io.Serializable;

@Data
public class CoreSettingPropertyFormat implements Serializable
{
    private String identify;
    private String scheme;
    private String deployType;
    private CoreApiSetting api;

}
