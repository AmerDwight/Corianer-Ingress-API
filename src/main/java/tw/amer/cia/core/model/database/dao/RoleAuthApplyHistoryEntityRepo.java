package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.RoleAuthApplyHistoryEntity;
import tw.amer.cia.core.model.database.compositeId.RoleAuthApplyHistoryEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleAuthApplyHistoryEntityRepo extends JpaRepository<RoleAuthApplyHistoryEntity, RoleAuthApplyHistoryEntityId>
{
    @Query("SELECT DISTINCT harad.apiId " +
            "FROM RoleAuthApplyHistoryEntity hara, RoleAuthApplyDetailHistoryEntity harad " +
            "WHERE hara.applyFormId = harad.applyFormId " +
            "AND hara.roleId = :roleId ")
    List<String> findOnAppliedApiIdListByRole(@Param("roleId") String roleId);

    List<RoleAuthApplyHistoryEntity> findByApplyFormId(String applyFormId);

    void deleteByApplyFormId(String applyFormId);
}
