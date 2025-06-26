package tw.amer.cia.core.model.pojo.service.common.api;

import tw.amer.cia.core.model.database.ApiDpyEntity;
import tw.amer.cia.core.model.database.ApiEndpointEntity;
import tw.amer.cia.core.model.database.ApiEntity;
import tw.amer.cia.core.model.database.ApiGwPluginDpyEntity;
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
public class CompleteApiDto implements Serializable
{

    private ApiEntity apiEntity;
    private List<ApiDpyEntity> deployList;
    private List<ApiEndpointEntity> endpointList;
    private List<ApiGwPluginDpyEntity> pluginDpyList;
}
