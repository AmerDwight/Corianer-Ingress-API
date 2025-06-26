package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.SystemSignOffConfigEntity;
import tw.amer.cia.core.model.database.compositeId.SystemSignOffConfigEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemSignOffConfigEntityRepo extends JpaRepository<SystemSignOffConfigEntity, SystemSignOffConfigEntityId> {
    List<SystemSignOffConfigEntity> findAllBySystemId(String systemId);
}
