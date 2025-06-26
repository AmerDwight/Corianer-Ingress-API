package tw.amer.cia.core.model.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tw.amer.cia.core.model.database.ApiEntity;
import tw.amer.cia.core.model.pojo.service.common.api.ApiNameDto;

import java.util.List;
import java.util.Optional;

public interface ApiEntityRepo extends JpaRepository<ApiEntity, String>
{
    Optional<ApiEntity> findByApiId(String apiId);

    List<ApiEntity> findBySystemId(String systemId);
    List<ApiEntity> findBySystemIdOrderByApiName(String systemId);

    @Query("SELECT cmm.apiId FROM ApiEntity cmm " +
            "JOIN SystemEntity css on cmm.systemId = css.systemId " +
            "WHERE  " +
            " css.systemName = :systemName " +
            " AND cmm.apiName = :apiName ")
    Optional<String> findApiIdByName(@Param("systemName") String systemName, @Param("apiName") String apiName);

    @Query("SELECT cmm FROM ApiEntity cmm " +
            "JOIN  SystemEntity css on cmm.systemId = css.systemId " +
            "WHERE css.systemName = :systemName " +
            "AND cmm.apiName = :apiName ")
    Optional<ApiEntity> findBySystemNameAndApiName(@Param("systemName") String systemName, @Param("apiName") String apiName);

    @Query("SELECT new tw.amer.cia.core.model.pojo.service.common.api.ApiNameDto( " +
            "css.systemName, " +
            "cmm.apiName ) " +
            "FROM ApiEntity cmm " +
            "JOIN SystemEntity css on cmm.systemId = css.systemId " +
            "WHERE cmm.apiId = :apiId ")
    Optional<ApiNameDto> findNameByApiId(@Param("apiId") String apiId);


    @Query("SELECT DISTINCT cmm FROM ApiEntity cmm " +
            "JOIN RoleAuthorityEntity cra ON cra.apiId = cmm.apiId " +
            "JOIN RoleEntity crr ON crr.roleId = cra.roleId " +
            "WHERE crr.roleId = :roleId " +
            "AND cra.fabId IN :fabIdList " +
            "AND cmm.systemId = :systemId ")
    List<ApiEntity> findRoleAuthorityApiByRoleIdAndSystemIdAndFabIdList(@Param("roleId") String roleId,
                                                                        @Param("systemId") String systemId,
                                                                        @Param("fabIdList") List<String> fabIdList);
}
