package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.GwRouteEntity;
import tw.amer.cia.core.model.database.dto.GwRouteDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GwRouteEntityRepo extends JpaRepository<GwRouteEntity, String>
{
    Optional<GwRouteEntity> findByGwRouteId(String GwRouteId);

    Optional<GwRouteEntity> findByFabIdAndEndpointId(String fabId, String endpointId);

    @Query("SELECT rgr.gwRouteId FROM GwRouteEntity rgr, ApiEndpointEntity cme " +
            "WHERE cme.apiId = :apiId " +
            "AND cme.endpointId = rgr.endpointId " +
            "AND rgr.fabId = :fabId ")
    List<String> findGwRouteIdByFabIdAndApiId(@Param("fabId") String fabId, @Param("apiId") String apiId);

    @Query("SELECT rgr.gwRouteId FROM GwRouteEntity rgr, ApiEndpointEntity cme " +
            "WHERE cme.apiId in :apiId " +
            "AND cme.endpointId = rgr.endpointId " +
            "AND rgr.fabId = :fabId ")
    List<String> findGwRouteIdByFabIdAndApiIdIn(@Param("fabId") String fabId, @Param("apiId") Collection<String> apiIds);

    @Query("SELECT rgr FROM GwRouteEntity rgr, ApiEndpointEntity cme " +
            "WHERE cme.apiId = :apiId " +
            "AND cme.endpointId = rgr.endpointId ")
    List<GwRouteEntity> findByApiId(@Param("apiId") String apiId);

    @Query("SELECT rgr FROM GwRouteEntity rgr, ApiEndpointEntity cme " +
            "WHERE cme.apiId = :apiId " +
            "AND rgr.fabId = :fabId " +
            "AND cme.endpointId = rgr.endpointId ")
    List<GwRouteEntity> findByApiIdAndFabId(String apiId, String fabId);

    @Query("SELECT new tw.amer.cia.core.model.database.dto.GwRouteDto" +
            "(rgr.gwRouteId, rgr.fabId, cme.endpointId ) " +
            "FROM GwRouteEntity rgr, ApiEndpointEntity cme " +
            "WHERE cme.apiId = :apiId " +
            "AND rgr.fabId = :fabId " +
            "AND cme.endpointId = rgr.endpointId ")
    List<GwRouteDto> findDtoByApiIdAndFabId(@Param("apiId") String apiId, @Param("fabId") String fabId);

    List<GwRouteEntity> findByEndpointId(String endpointId);

    long deleteByFabIdAndEndpointId(String fabId, String endpointId);

    @Transactional
    @Modifying
    @Query("DELETE FROM GwRouteEntity rgr WHERE rgr.endpointId in ( " +
            "SELECT cme.endpointId FROM ApiEndpointEntity cme " +
            "WHERE cme.apiId = :apiId )")
    Integer deleteByApiId(@Param("apiId") String apiId);
}
