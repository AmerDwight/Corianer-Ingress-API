package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.DnsProxyReferenceConfigEntity;
import tw.amer.cia.core.model.database.dao.DnsProxyReferenceConfigEntityRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class DnsProxyReferenceConfigEntityService
{
    @Autowired
    private DnsProxyReferenceConfigEntityRepo dnsProxyReferenceConfigEntityRepo;

    public List<DnsProxyReferenceConfigEntity> getAll()
    {
        return dnsProxyReferenceConfigEntityRepo.findAll();
    }

    public Optional<DnsProxyReferenceConfigEntity> getById(String idIndex)
    {
        return dnsProxyReferenceConfigEntityRepo.findById(idIndex);
    }

    public DnsProxyReferenceConfigEntity create(DnsProxyReferenceConfigEntity entity)
    {
        return dnsProxyReferenceConfigEntityRepo.save(entity);
    }

    public DnsProxyReferenceConfigEntity update(String hostname, DnsProxyReferenceConfigEntity entity) throws DataSourceAccessException
    {

        if (StringUtils.equals(hostname, entity.getHostname()))
        {
            return dnsProxyReferenceConfigEntityRepo.findById(hostname).map(object ->
            {
                BeanUtils.copyNonNullProperties(entity, object);
                return dnsProxyReferenceConfigEntityRepo.save(object);

            }).orElseThrow(() -> new EntityNotFoundException("Hostname not found with id " + hostname));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void delete(String hostname)
    {
        dnsProxyReferenceConfigEntityRepo.deleteById(hostname);
    }
}