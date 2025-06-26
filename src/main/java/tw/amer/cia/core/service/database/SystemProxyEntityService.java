package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.SystemProxyEntity;
import tw.amer.cia.core.model.database.dao.SystemProxyEntityRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SystemProxyEntityService
{
    @Autowired
    SystemProxyEntityRepo systemProxyEntityRepo;

    public List<SystemProxyEntity> getAll()
    {
        return systemProxyEntityRepo.findAll();
    }

    public Optional<SystemProxyEntity> getById(String proxyId)
    {
        return systemProxyEntityRepo.findByProxyId(proxyId);
    }

    public SystemProxyEntity create(SystemProxyEntity proxy)
    {
        return systemProxyEntityRepo.save(proxy);
    }

    public SystemProxyEntity update(String proxyId, SystemProxyEntity proxy) throws DataSourceAccessException
    {
        if (StringUtils.equals(proxyId, proxy.getProxyId()))
        {
            Optional<SystemProxyEntity> onSearchProxy = systemProxyEntityRepo.findByProxyId(proxyId);
            if (onSearchProxy.isPresent())
            {
                SystemProxyEntity onUpdateProxy = onSearchProxy.get();
                BeanUtils.copyNonNullProperties(proxy, onUpdateProxy);
                return systemProxyEntityRepo.save(onUpdateProxy);
            } else
            {
                return this.create(proxy);
            }
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }


    public void delete(String proxyId)
    {
        systemProxyEntityRepo.deleteById(proxyId);
    }
}
