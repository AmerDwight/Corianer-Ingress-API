package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.GwPluginEntity;
import tw.amer.cia.core.model.pojo.service.common.api.ApiGwPluginInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GwPluginEntityRepo extends JpaRepository<GwPluginEntity, String>
{

    @Query("SELECT new tw.amer.cia.core.model.pojo.service.common.api.ApiGwPluginInfoDto" +
            "( cgp.gwPluginName, cgp.gwPluginType, cgp.gwPluginSample ) " +
            "FROM GwPluginEntity cgp ")
    List<ApiGwPluginInfoDto> getAllGwPluginInfo();
}
