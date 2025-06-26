package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.GwUpstreamEntity;
import tw.amer.cia.core.model.database.dao.GwUpstreamEntityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class GwUpstreamEntityService
{
    @Autowired
    GwUpstreamEntityRepo gwUpstreamEntityRepo;

    public List<GwUpstreamEntity> getAllUpstreams()
    {
        return gwUpstreamEntityRepo.findAll();
    }

    public Optional<GwUpstreamEntity> getUpstreamById(String gwUsId)
    {
        return gwUpstreamEntityRepo.findByGwUpstreamId(gwUsId);
    }

    public GwUpstreamEntity createUpstream(GwUpstreamEntity upstream)
    {
        return gwUpstreamEntityRepo.save(upstream);
    }

    public GwUpstreamEntity updateUpstream(String gwUsId, GwUpstreamEntity gwUpstreamEntityUpdate) throws DataSourceAccessException
    {
        if (gwUsId.equals(gwUpstreamEntityUpdate.getGwUpstreamId()))
        {
            return gwUpstreamEntityRepo.findById(gwUsId).map(existingGwUpstream ->
            {
                // Use null-safe checks to update properties, ignore auditing fields
                if (gwUpstreamEntityUpdate.getFabId() != null)
                {
                    existingGwUpstream.setFabId(gwUpstreamEntityUpdate.getFabId());
                }
                if (gwUpstreamEntityUpdate.getSystemId() != null)
                {
                    existingGwUpstream.setSystemId(gwUpstreamEntityUpdate.getSystemId());
                }
                // Note: LM_USER and LM_TIME are not updated as they are managed by auditing

                return gwUpstreamEntityRepo.save(existingGwUpstream);

            }).orElseThrow(() -> new EntityNotFoundException("GW_UPSTREAM not found with id " + gwUsId));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void deleteUpstream(String gwUsId)
    {
        gwUpstreamEntityRepo.deleteById(gwUsId);
    }
}
