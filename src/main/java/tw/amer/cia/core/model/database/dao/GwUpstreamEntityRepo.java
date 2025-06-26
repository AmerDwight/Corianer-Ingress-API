package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.GwUpstreamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GwUpstreamEntityRepo extends JpaRepository<GwUpstreamEntity, String>
{
    Optional<GwUpstreamEntity> findByGwUpstreamId(String gwUpstreamId);

    Optional<GwUpstreamEntity> findByFabIdAndSystemId(String fabId, String systemId);

    @Query("SELECT rgu FROM GwUpstreamEntity rgu " +
            "JOIN ApiEntity cmm ON cmm.systemId = rgu.systemId " +
            "WHERE rgu.fabId = :fabId " +
            "AND cmm.apiId = :apiId ")
    Optional<GwUpstreamEntity> findByFabIdAndApiId(@Param("fabId") String fabId, @Param("apiId") String apiId);
}
