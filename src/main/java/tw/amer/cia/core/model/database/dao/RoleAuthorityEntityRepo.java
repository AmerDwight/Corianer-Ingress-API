package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.RoleAuthorityEntity;
import tw.amer.cia.core.model.database.compositeId.RoleAuthoroityEntityId;
import tw.amer.cia.core.model.pojo.service.common.role.SimpleAuthorityDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface RoleAuthorityEntityRepo extends JpaRepository<RoleAuthorityEntity, RoleAuthoroityEntityId> {
    List<RoleAuthorityEntity> findByRoleId(String roleId);

    List<RoleAuthorityEntity> findByApiId(String apiId);

    List<RoleAuthorityEntity> findByRoleIdAndFabId(String roleId, String fabId);

    @Query("SELECT new tw.amer.cia.core.model.pojo.service.common.role.SimpleAuthorityDto" +
            "( css.systemName, cmm.apiName )" +
            "FROM RoleAuthorityEntity cra, ApiEntity cmm, SystemEntity css " +
            "WHERE cra.apiId = cmm.apiId AND css.systemId = cmm.systemId " +
            "AND cra.roleId = :roleId AND cra.fabId = :fabId")
    List<SimpleAuthorityDto> apiListAuthoritiesByRoleIdAndFabId(@Param("roleId") String roleId, @Param("fabId") String fabId);

    @Query("SELECT cmm.apiId FROM ApiEntity cmm, RoleAuthorityEntity cra " +
            "WHERE cra.apiId = cmm.apiId " +
            "AND cra.roleId = :roleId " +
            "AND cra.fabId = :fabId")
    List<String> findApiIdByRoleIdAndFabId(@Param("roleId") String roleId, @Param("fabId") String fabId);


    List<RoleAuthorityEntity> findByRoleIdAndFabIdAndApiIdIn(String roleId, String fabId, Collection<String> apiId);

    @Query("SELECT DISTINCT new tw.amer.cia.core.model.pojo.service.common.role.SimpleAuthorityDto(" +
            "css.systemName, cmm.apiName ) " +
            "FROM RoleAuthorityEntity cra " +
            "JOIN ApiEntity cmm ON cmm.apiId = cra.apiId " +
            "JOIN SystemEntity css ON css.systemId = cmm.systemId " +
            "WHERE cra.roleId = :roleId " +
            "AND cra.fabId = :fabId")
    List<SimpleAuthorityDto> findAuthorizedSimpleAuthorityDtoByRoleIdAndFabId(@Param("roleId") String roleId, @Param("fabId") String fabId);

    @Query("SELECT DISTINCT cra.fabId FROM RoleAuthorityEntity cra " +
            "WHERE cra.roleId = :roleId ")
    List<String> findFabIdListByRoleId(@Param("roleId") String roleId);

    @Transactional(rollbackFor = Exception.class)
    void deleteByApiId(String apiId);

    List<RoleAuthorityEntity> findByFabIdIn(Collection<String> fabIdSet);

    List<RoleAuthorityEntity> findByRoleIdAndApiIdIn(String roleId, Collection<String> collect);

    List<RoleAuthorityEntity> findByRoleIdIn(Collection<String> roleIds);

    List<RoleAuthorityEntity> findByRoleIdAndApiId(String roleId, String apiId);
}
