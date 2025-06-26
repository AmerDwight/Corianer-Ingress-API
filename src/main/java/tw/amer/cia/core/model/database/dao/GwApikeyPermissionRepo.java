package tw.amer.cia.core.model.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import tw.amer.cia.core.model.database.GwApikeyPermissionEntity;
import tw.amer.cia.core.model.database.compositeId.GwApikeyPermissionEntityId;
import tw.amer.cia.core.model.pojo.service.common.apikey.SimplePermissionDto;
import tw.amer.cia.core.model.pojo.service.common.apikey.SimplePermissionWithIdDto;
import tw.amer.cia.core.model.pojo.service.host.control.ListApikeyPermissionDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GwApikeyPermissionRepo extends JpaRepository<GwApikeyPermissionEntity, GwApikeyPermissionEntityId>
{
    List<GwApikeyPermissionEntity> findByApikeyId(String apikeyId);

    List<GwApikeyPermissionEntity> findByApiId(String apiId);

    List<GwApikeyPermissionEntity> findByFabIdAndApikeyId(String fabId, String apikeyId);

    List<GwApikeyPermissionEntity> findByFabIdNotAndApikeyId(String fabId, String apikeyId);

    Optional<GwApikeyPermissionEntity> findByFabIdAndApikeyIdAndApiId(String fabId, String apikeyId, String apiId);

    @Query("SELECT DISTINCT ckp FROM GwApikeyPermissionEntity ckp " +
            "WHERE ckp.apikeyId = :apikeyId " +
            "AND ckp.fabId = :fabId " +
            "AND ckp.apiId IN :apiIds")
    List<GwApikeyPermissionEntity> findByFabIdAndApikeyIdAndApiIds(@Param("fabId") String fabId, @Param("apikeyId") String apikeyId, @Param("apiIds") List<String> apiIds);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("DELETE FROM GwApikeyPermissionEntity rkp WHERE rkp.apikeyId = :apikeyId")
    void deleteByApikeyId(@Param("apikeyId") String apikeyId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("DELETE FROM GwApikeyPermissionEntity rkp WHERE rkp.apikeyId = :apikeyId AND rkp.fabId = :fabId")
    void deleteByFabIdAndApikeyId(@Param("fabId") String fabId, @Param("apikeyId") String apikeyId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("DELETE FROM GwApikeyPermissionEntity rkp WHERE rkp.apikeyId = :apikeyId AND rkp.fabId = :fabId AND rkp.apiId = :apiId ")
    void deleteByFabIdAndApikeyIdAndApiId(@Param("fabId") String fabId, @Param("apikeyId") String apikeyId, @Param("apiId") String apiId);


    @Transactional(rollbackFor = Exception.class)
    void deleteByFabIdAndApikeyIdAndApiIdIn(String fabId, String apikeyId, List<String> apiIds);

    @Query("SELECT DISTINCT ckp.fabId FROM GwApikeyPermissionEntity ckp WHERE ckp.apikeyId = :apikeyId")
    List<String> findPermittedFabListOfApikeyByApikeyId(@Param("apikeyId") String apikeyId);

    @Query("SELECT DISTINCT new tw.amer.cia.core.model.pojo.service.common.apikey.SimplePermissionDto(" +
            "css.systemName, cmm.apiName ) " +
            "FROM GwApikeyPermissionEntity ckp " +
            "JOIN ApiEntity cmm ON cmm.apiId = ckp.apiId " +
            "JOIN SystemEntity css ON css.systemId = cmm.systemId " +
            "WHERE ckp.apikeyId = :apikeyId " +
            "AND ckp.fabId = :fabId")
    List<SimplePermissionDto> findPermittedSimplePermissionDtoByApikeyIdAndFabId(@Param("apikeyId") String apikeyId, @Param("fabId") String fabId);

    @Query("SELECT DISTINCT new tw.amer.cia.core.model.pojo.service.common.apikey.SimplePermissionWithIdDto(" +
            "css.systemId, css.systemName, cmm.apiId , cmm.apiName ) " +
            "FROM GwApikeyPermissionEntity ckp " +
            "JOIN ApiEntity cmm ON cmm.apiId = ckp.apiId " +
            "JOIN SystemEntity css ON css.systemId = cmm.systemId " +
            "WHERE ckp.apikeyId = :apikeyId ")
    List<SimplePermissionWithIdDto> findPermittedSimplePermissionWithIdDtoByApikeyId(@Param("apikeyId") String apikeyId);


    @Transactional(rollbackFor = Exception.class)
    void deleteByApiId(String apiId);

    List<GwApikeyPermissionEntity> findByFabIdAndApiId(String fabId, String apiId);

    List<GwApikeyPermissionEntity> findByFabIdIn(Collection<String> fabIdSet);

    @Query("SELECT DISTINCT new tw.amer.cia.core.model.pojo.service.host.control.ListApikeyPermissionDto" +
            "(cmm.apiType, css.systemName, cmm.apiName )" +
            "FROM GwApikeyPermissionEntity ckp, ApiEntity cmm, SystemEntity css " +
            "WHERE ckp.apiId = cmm.apiId AND css.systemId = cmm.systemId  " +
            "AND ckp.fabId = :fabId " +
            "AND ckp.apikeyId = :apikeyId ")
    List<ListApikeyPermissionDto> apiListApikeyPermissionByFabIdAndApikeyId
            (@Param("fabId") String fabId, @Param("apikeyId") String apikeyId);


}
