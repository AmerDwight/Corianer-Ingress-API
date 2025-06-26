package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.FabEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FabEntityRepo extends JpaRepository<FabEntity, String>, JpaSpecificationExecutor<FabEntity> {
    Optional<FabEntity> findByFabId(String fabId);

    List<FabEntity> findBySite(String site);

    @Query(" SELECT cff.fabId FROM FabEntity cff ")
    List<String> findFabIdListAll();

    @Query("SELECT cff.fabId FROM FabEntity cff WHERE cff.site = (SELECT c.site FROM FabEntity c WHERE c.fabId = :fabId)")
    List<String> findFabIdListBySiteOfFabId(@Param("fabId") String fabId);

    @Query("SELECT DISTINCT cff.site FROM FabEntity cff WHERE cff.fabId in :fabIds")
    List<String> findDistinctSiteByFabIdIn(@Param("fabIds") Collection<String> fabIds);
}
