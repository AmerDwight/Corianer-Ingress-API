package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.RoleEntity;
import tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement.Web_RoleManagementPanelDataDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleEntityRepo extends JpaRepository<RoleEntity, String> {
    Optional<RoleEntity> findByRoleId(String roleId);

    Optional<RoleEntity> findByRoleName(String roleName);

    Optional<RoleEntity> findFirstByRoleIdOrRoleName(String roleId, String roleName);

    @Query("SELECT crr FROM RoleEntity crr WHERE crr.roleId in ( " +
            "SELECT DISTINCT cru.roleId FROM RoleUserEntity cru WHERE cru.userId = :userId)")
    List<RoleEntity> findRoleListByUserId(@Param("userId") String userId);

    List<RoleEntity> findByRoleIdIn(Collection<String> roleIds);

    @Query(value = "SELECT new tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement.Web_RoleManagementPanelDataDto" +
            "(crr.roleId, crr.roleType, crr.roleName, crr.roleDesc) " +
            "FROM RoleEntity crr",
            countQuery = "SELECT COUNT(*) FROM RoleEntity")
    Page<Web_RoleManagementPanelDataDto> findRoleManagePanelDataOrderByRoleIdDesc(Pageable pageable);

    @Query(value = "SELECT DISTINCT new tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement.Web_RoleManagementPanelDataDto" +
            "(crr.roleId, crr.roleType, crr.roleName, crr.roleDesc) " +
            "FROM RoleEntity crr " +
            "WHERE crr.roleName like :fuzzyRoleName ",
            countQuery = "SELECT COUNT(*) FROM RoleEntity crr WHERE crr.roleName like :fuzzyRoleName ")
    Page<Web_RoleManagementPanelDataDto> findByRoleNameLike(Pageable pageable, @Param("fuzzyRoleName") String fuzzyRoleName);

}
