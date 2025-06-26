package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.FabProxyEntity;
import tw.amer.cia.core.model.database.compositeId.FabProxyEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FabProxyEntityRepo extends JpaRepository<FabProxyEntity, FabProxyEntityId>
{
    Optional<FabProxyEntity> findByFabId(FabProxyEntityId fabProxyId);

}
