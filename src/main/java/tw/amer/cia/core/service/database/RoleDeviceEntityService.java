package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.RoleDeviceEntity;
import tw.amer.cia.core.model.database.dao.RoleDeviceEntityRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class RoleDeviceEntityService
{
    @Autowired
    RoleDeviceEntityRepo roleDeviceEntityRepo;
    

    public List<RoleDeviceEntity> getAllDevices()
    {
        return roleDeviceEntityRepo.findAll();
    }

    public Optional<RoleDeviceEntity> getRoleDeviceById(String deviceId)
    {
        return roleDeviceEntityRepo.findById(deviceId);
    }

    public RoleDeviceEntity createDevice(RoleDeviceEntity device)
    {
        return roleDeviceEntityRepo.save(device);
    }

    public RoleDeviceEntity updateRoleDevice(String deviceId, RoleDeviceEntity onUpdateDevice) throws DataSourceAccessException
    {
        if (StringUtils.equals(deviceId, onUpdateDevice.getDeviceId()))
        {
            return roleDeviceEntityRepo.findById(deviceId).map(object ->
            {
                BeanUtils.copyNonNullProperties(onUpdateDevice, object);
                return roleDeviceEntityRepo.save(object);

            }).orElseThrow(() -> new EntityNotFoundException("DeviceId not found with " + deviceId));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void deleteDevice(String deviceId)
    {
        roleDeviceEntityRepo.deleteById(deviceId);
    }
}
