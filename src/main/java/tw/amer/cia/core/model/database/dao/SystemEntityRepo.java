package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.SystemEntity;
import tw.amer.cia.core.model.pojo.service.host.web.manager.contentManagement.Web_SystemItemDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement.Web_RoleManagementModifyAuthSystemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SystemEntityRepo extends JpaRepository<SystemEntity, String> {
    Optional<SystemEntity> findBySystemId(String systemId);

    Optional<SystemEntity> findBySystemName(String systemName);

    Page<SystemEntity> findAllByOrderBySystemNameAsc(Pageable pageable);

    @Query("SELECT DISTINCT css FROM SystemEntity css " +
            "JOIN ApiEntity cmm ON cmm.systemId = css.systemId " +
            "JOIN RoleAuthorityEntity cra ON cra.apiId = cmm.apiId " +
            "WHERE cra.roleId = :roleId AND " +
            "cra.fabId IN :fabList ")
    List<SystemEntity> findRoleAuthoritySystemByRoleIdAndFabList(@Param("roleId")String roleId, @Param("fabList") List<String> fabList);

    @Query(value = "SELECT new tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement.Web_RoleManagementModifyAuthSystemDto" +
            "(css) " +
            "FROM SystemEntity css",
            countQuery = "SELECT COUNT(*) FROM SystemEntity")
    Page<Web_RoleManagementModifyAuthSystemDto> findRoleManagementModifyAuthSystemOrderBySystemName(Pageable pageable);

    @Query("SELECT new tw.amer.cia.core.model.pojo.service.host.web.manager.contentManagement.Web_SystemItemDto(" +
            " css.systemId, css.systemName, css.activeStatus, count(DISTINCT cmm.apiId ) ) " +
            "FROM SystemEntity css LEFT JOIN ApiEntity cmm ON cmm.systemId = css.systemId " +
            "GROUP BY css.systemId, css.systemName, css.activeStatus " +
            "ORDER BY css.systemName ")
    List<Web_SystemItemDto> listSystemsForContentManageOrderByNameAsc();
}
