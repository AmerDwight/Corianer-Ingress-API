package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.RoleAuthApplyEntity;
import tw.amer.cia.core.model.database.compositeId.RoleAuthApplyEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleAuthApplyEntityRepo extends JpaRepository<RoleAuthApplyEntity, RoleAuthApplyEntityId>
{
    @Query("SELECT DISTINCT rarad.apiId " +
            "FROM RoleAuthApplyEntity rara, RoleAuthApplyDetailEntity rarad " +
            "WHERE rara.applyFormId = rarad.applyFormId " +
            "AND rara.roleId = :roleId ")
    List<String> findOnAppliedApiIdListByRole(@Param("roleId") String roleId);

    List<RoleAuthApplyEntity> findByApplyFormId(String applyFormId);

    void deleteByApplyFormId(String applyFormId);
}
