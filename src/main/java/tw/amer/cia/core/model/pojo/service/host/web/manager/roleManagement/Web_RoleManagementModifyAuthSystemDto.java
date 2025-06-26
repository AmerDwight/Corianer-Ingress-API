package tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement;

import tw.amer.cia.core.model.database.SystemEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_RoleManagementModifyAuthSystemDto implements Serializable {
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("SYSTEM_DESC")
    private String systemDesc;

    @JsonProperty("SERVICE_LEVEL")
    private Integer serviceLevel;

    @JsonProperty("OWNER")
    private String owner;

    public Web_RoleManagementModifyAuthSystemDto(SystemEntity cSysSystem) {
        this.systemId = cSysSystem.getSystemId();
        this.systemName = cSysSystem.getSystemName();
        this.systemDesc = cSysSystem.getSystemDesc();
        this.serviceLevel = cSysSystem.getServiceLevel();
        this.owner = cSysSystem.getOwner();
    }

}
