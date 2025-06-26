package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.RoleAuthApplyDetailHistoryEntity;
import tw.amer.cia.core.model.database.compositeId.RoleAuthApplyDetailHistoryEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleAuthApplyDetailHistoryEntityRepo extends JpaRepository<RoleAuthApplyDetailHistoryEntity, RoleAuthApplyDetailHistoryEntityId>
{
    List<RoleAuthApplyDetailHistoryEntity> findByApplyFormId(String applyFormId);

    void deleteByApplyFormId(String applyFormId);
}
