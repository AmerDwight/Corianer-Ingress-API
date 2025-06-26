package tw.amer.cia.core.model.pojo.service.common.role;

import tw.amer.cia.core.model.database.RoleAuthorityEntity;
import tw.amer.cia.core.model.database.RoleDeviceEntity;
import tw.amer.cia.core.model.database.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteRoleDto implements Serializable {

    private RoleEntity role;
    private List<RoleAuthorityEntity> authorityList;
    private List<RoleDeviceEntity> deviceList;

}
