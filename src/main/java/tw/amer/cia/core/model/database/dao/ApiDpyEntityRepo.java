package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.ApiDpyEntity;
import tw.amer.cia.core.model.database.compositeId.ApiDpyEntityId;
import tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority.ApplyRoleAuthorityApiDeployDataForDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ApiDpyEntityRepo extends JpaRepository<ApiDpyEntity, ApiDpyEntityId>
{
    Optional<ApiDpyEntity> findByFabId(String fabId);

    Optional<ApiDpyEntity> findByFabIdAndApiId(String fabId, String apiId);

    List<ApiDpyEntity> findByApiId(String apiId);

    @Query("SELECT cmd FROM ApiDpyEntity cmd WHERE cmd.apiId = :apiId AND cmd.fabId NOT IN (" +
            "SELECT cff.fabId FROM FabEntity cff WHERE cff.site = 'VIRTUAL' ) ")
    List<ApiDpyEntity> findByApiIdWithoutVirtualSite(@Param("apiId") String apiId);

    Optional<ApiDpyEntity> findByApiIdAndFabId(String apiId, String fabId);

    long deleteByApiIdAndFabId(String apiId, String fabId);

    @Query("SELECT new map(cfs.apiId as apiId, cfs.fabId as fabId) FROM ApiDpyEntity cfs")
    List<Map<String, String>> findAllApiAndFabIds();

    @Query("SELECT new map(cfs.apiId as apiId, cfs.fabId as fabId) FROM ApiDpyEntity cfs WHERE NOT EXISTS " +
            "( SELECT 1 FROM RoleAuthorityEntity cra WHERE cra.roleId = :roleId " +
            "   AND cra.apiId = cfs.apiId " +
            "   AND cra.fabId = cfs.fabId)")
    List<Map<String, String>> findMissingAuthorityCombineByRoleId(@Param("roleId") String roleId);

    @Query("SELECT c.fabId FROM ApiDpyEntity c WHERE c.apiId = :apiId")
    List<String> findFabIdByApiId(String apiId);

    @Query("SELECT CASE WHEN COUNT(cmd) > 0 THEN true ELSE false END " +
            "FROM ApiDpyEntity cmd " +
            "JOIN ApiEntity cmm on cmm.apiId = cmd.apiId " +
            "WHERE cmm.systemId = :systemId " +
            "AND cmd.fabId = :fabId ")
    boolean existsBySystemIdAndFabId(@Param("systemId") String systemId, @Param("fabId") String fabId);

    @Query("SELECT new tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority.ApplyRoleAuthorityApiDeployDataForDto( " +
            "cmd.apiId, cmd.fabId ) FROM ApiDpyEntity cmd " +
            "WHERE cmd.apiId in :apiIdList ")
    List<ApplyRoleAuthorityApiDeployDataForDto> searchApiAndDeployForSignatureByApiIdList(@Param("apiIdList") Collection<String> apiIdList);

    List<ApiDpyEntity> findByFabIdIn(Collection<String> fabIdSet);

    List<ApiDpyEntity> findByApiIdIn(Collection<String> totalApiIdList);

    <T> Collection<T> findByFabIdAndApiIdIn(String fabId, Collection<String> apiIds, Class<T> resultClass);

    @Query("SELECT DISTINCT csd.fabId FROM ApiEntity cmm " +
            "INNER JOIN SystemDpyEntity csd ON csd.systemId = cmm.systemId " +
            "WHERE cmm.apiId = :apiId ")
    List<String> findSystemDeployedFabIdListByApiId(@Param("apiId") String apiId);
}
