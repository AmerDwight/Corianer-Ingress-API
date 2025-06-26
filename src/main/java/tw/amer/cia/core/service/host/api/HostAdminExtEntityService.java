package tw.amer.cia.core.service.host.api;

import tw.amer.cia.core.component.functional.coriander.CallClientApiComponent;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ExternalSystemConfigEntity;
import tw.amer.cia.core.model.database.dao.ExternalSystemConfigEntityRepo;
import tw.amer.cia.core.service.database.ExternalSystemConfigEntityService;
import org.springframework.beans.factory.annotation.Autowired;

@HostService
public class HostAdminExtEntityService {

    @Autowired
    ExternalSystemConfigEntityRepo cExtCtlRepo;

    @Autowired
    ExternalSystemConfigEntityService cExtCtlService;

    @Autowired
    CallClientApiComponent callClientApiComponent;

    public void createOrUpdateEntityFromHost(ExternalSystemConfigEntity entity) throws DataSourceAccessException {
        ExternalSystemConfigEntity onUpdateEntity = cExtCtlService.createOrUpdateExtEntity(entity);
        callClientApiComponent.tryUpdateExtEntityBroadcastNoReply(onUpdateEntity);
    }

    public void deleteEntityFromHost(String entityId) {
        cExtCtlService.delete(entityId);
        callClientApiComponent.tryDeleteExtEntityBroadcastNoReply(entityId);
    }
}
