package tw.amer.cia.core.model.pojo.service.common.system;

import tw.amer.cia.core.model.database.SystemEntity;
import tw.amer.cia.core.model.database.SystemDpyEntity;
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
public class CompleteSystemDto implements Serializable
{

    private SystemEntity system;
    private List<SystemDpyEntity> deployList;

}
