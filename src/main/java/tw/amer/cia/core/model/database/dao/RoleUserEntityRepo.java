package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.RoleUserEntity;
import tw.amer.cia.core.model.database.compositeId.RoleUserEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleUserEntityRepo extends JpaRepository<RoleUserEntity, RoleUserEntityId>
{
    List<RoleUserEntity> findByRoleId(String roleId);

    Integer deleteByRoleId(String roleId);

    Optional<RoleUserEntity> findByUserIdAndRoleId(String userId, String roleId);

    List<RoleUserEntity> findByUserIdIn(Collection<String> userIds);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM RoleUserEntity cru WHERE cru.userId = :userId " +
            " AND EXISTS ( SELECT 1 FROM RoleEntity crr WHERE crr.roleId = cru.roleId AND crr.roleType = 'DEPT' ) ")
    void exitEveryDeptByUserId(@Param("userId") String userId);

    List<RoleUserEntity> findByUserId(String onSearchUserId);
}
