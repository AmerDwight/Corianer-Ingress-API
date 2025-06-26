package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.SystemDpyEntity;
import tw.amer.cia.core.model.database.compositeId.SystemDpyEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SystemDpyEntityRepo extends JpaRepository<SystemDpyEntity, SystemDpyEntityId>
{
    List<SystemDpyEntity> findBySystemId(String systemId);

    @Query("SELECT c.fabId FROM SystemDpyEntity c WHERE c.systemId = :systemId")
    List<String> findFabIdBySystemId(String systemId);

    @Query("SELECT csd FROM SystemDpyEntity csd WHERE csd.systemId in :systemIdList")
    List<SystemDpyEntity> findBySystemIds(List<String> systemIdList);

    @Query("SELECT cfs FROM SystemDpyEntity cfs " +
            "JOIN SystemEntity css on css.systemId = cfs.systemId " +
            "WHERE css.systemName = :systemName " +
            "AND cfs.fabId IN :fabIds")
    List<SystemDpyEntity> findBySystemIdAndFabIds(@Param("systemName") String systemName, @Param("fabIds") List<String> fabIds);

    Optional<SystemDpyEntity> findBySystemIdAndFabId(String systemName, String fabId);

    List<SystemDpyEntity> findByFabId(String fabId);

    List<SystemDpyEntity> findByFabIdIn(Collection<String> fabIds);


}
