package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.RoleAuthApplyDetailEntity;
import tw.amer.cia.core.model.database.compositeId.RoleAuthApplyDetailEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleAuthApplyDetailEntityRepo extends JpaRepository<RoleAuthApplyDetailEntity, RoleAuthApplyDetailEntityId>
{
    List<RoleAuthApplyDetailEntity> findByApplyFormId(String applyFormId);

    void deleteByApplyFormId(String applyFormId);
}
