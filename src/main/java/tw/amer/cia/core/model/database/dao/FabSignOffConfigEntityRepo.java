package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.FabSignOffConfigEntity;
import tw.amer.cia.core.model.pojo.service.host.web.manager.contentManagement.Web_SiteOperateChargerDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface FabSignOffConfigEntityRepo extends JpaRepository<FabSignOffConfigEntity, String> {
    List<FabSignOffConfigEntity> findDistinctByFabIdIn(Collection<String> fabIds);


    @Query("SELECT new tw.amer.cia.core.model.pojo.service.host.web.manager.contentManagement.Web_SiteOperateChargerDto" +
            "(cfsa.fabId, cfsa.siteManagerId, cuu.userName ) FROM FabSignOffConfigEntity cfsa LEFT JOIN UserEntity cuu on cuu.userId = cfsa.siteManagerId " +
            "ORDER BY cfsa.fabId ")
    List<Web_SiteOperateChargerDto> listSiteOperateChargerByFabIdAsc();
}
