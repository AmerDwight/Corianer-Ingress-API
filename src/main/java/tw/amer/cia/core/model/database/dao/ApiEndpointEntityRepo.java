package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.ApiEndpointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApiEndpointEntityRepo extends JpaRepository<ApiEndpointEntity, String>
{
    Optional<ApiEndpointEntity> findByEndpointId(String endpointId);

    List<ApiEndpointEntity> findByApiId(String apiId);

    @Query("SELECT cme.endpointId FROM ApiEndpointEntity cme WHERE cme.apiId = :apiId " +
            "AND EXISTS " +
            "(SELECT cfs FROM ApiDpyEntity cfs " +
            "WHERE cfs.fabId = :fabId AND cfs.apiId = cme.apiId )")
    List<String> findEndpointIdByFabIdAndApiId(@Param("fabId") String fabId, @Param("apiId") String apiId);

    @Query("SELECT cme FROM ApiEndpointEntity cme " +
            "JOIN ApiEntity cmm ON cmm.apiId = cme.apiId " +
            "JOIN ApiDpyEntity cfs ON cfs.apiId = cme.apiId " +
            "WHERE cmm.apiName = :apiName " +
            "AND  cfs.fabId = :fabId")
    List<ApiEndpointEntity> findByFabIdAndApiName(@Param("fabId") String fabId, @Param("apiName") String apiName);

    long deleteByApiId(String apiId);
}
