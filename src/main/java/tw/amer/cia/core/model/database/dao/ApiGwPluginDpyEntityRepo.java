package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.ApiGwPluginDpyEntity;
import tw.amer.cia.core.model.database.compositeId.ApiGwPluginDpyEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ApiGwPluginDpyEntityRepo extends JpaRepository<ApiGwPluginDpyEntity, ApiGwPluginDpyEntityId> {
    List<ApiGwPluginDpyEntity> findByApiId(String apiId);

    List<ApiGwPluginDpyEntity> findByApiIdAndFabId(String apiId, String fabId);

    List<ApiGwPluginDpyEntity> findByApiIdAndFabIdIn(String apiId, Collection<String> fabIds);

    List<ApiGwPluginDpyEntity> findByGwPluginId(String gwPluginId);

    List<ApiGwPluginDpyEntity> findByApiIdAndGwPluginId(String apiId, String gwPluginId);

    Optional<ApiGwPluginDpyEntity> findById(ApiGwPluginDpyEntityId id);

}
