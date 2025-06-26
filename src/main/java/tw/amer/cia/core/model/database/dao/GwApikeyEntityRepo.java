package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.GwApikeyEntity;
import tw.amer.cia.core.model.pojo.component.gateway.GwApikeyNameDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface GwApikeyEntityRepo extends JpaRepository<GwApikeyEntity, String> {
    Optional<GwApikeyEntity> findByApikeyId(String apikeyId);

    List<GwApikeyEntity> findByRoleId(String roleId);

    Page<GwApikeyEntity> findByRoleIdAndIsUiVisibleOrderByKeyNameAsc(String roleId, String isUiVisible, Pageable pageable);

    Optional<GwApikeyEntity> findByApikeyIdAndRoleId(String apikeyId, String roleId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("DELETE FROM GwApikeyEntity rka WHERE rka.apikeyId = :apikeyId")
    void deleteByApikeyId(@Param("apikeyId") String apikeyId);

    List<GwApikeyEntity> findByKeyName(String keyName);

    Optional<GwApikeyEntity> findByRoleIdAndKeyName(String roleId, String keyName);

    @Query("SELECT new tw.amer.cia.core.model.pojo.component.gateway.GwApikeyNameDto( cka.roleId, cka.keyName ) " +
            "FROM GwApikeyEntity cka " +
            "JOIN GwApikeyPermissionEntity ckp on ckp.apikeyId = cka.apikeyId " +
            "WHERE ckp.fabId = :fabId " +
            "AND ckp.apiId = :apiId")
    List<GwApikeyNameDto> findNameListOfExistsApikeyByFabIdAndApiId(@Param("fabId") String fabId, @Param("apiId") String apiId);


    @Query("SELECT DISTINCT cka FROM GwApikeyEntity cka " +
            "JOIN GwApikeyPermissionEntity ckp ON ckp.apikeyId = cka.apikeyId " +
            "WHERE cka.roleId = :roleId " +
            "AND ckp.fabId = :fabId " +
            "AND ckp.apiId IN :apiIds")
    List<GwApikeyEntity> findByRoleIdAndKeyPermissions(@Param("roleId") String roleId, @Param("fabId") String fabId, @Param("apiIds") List<String> apiIds);

    @Query("SELECT cka FROM GwApikeyEntity cka " +
            "WHERE cka.apikeyId IN :apikeyIdSet " +
            "AND NOT EXISTS (SELECT ckp.apikeyId FROM GwApikeyPermissionEntity ckp WHERE ckp.apikeyId = cka.apikeyId)")
    List<GwApikeyEntity> findNoUsageApikeyByApikeyIds(@Param("apikeyIdSet") Iterable<String> apikeyIdSet);


}
