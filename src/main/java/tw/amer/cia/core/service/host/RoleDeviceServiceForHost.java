package tw.amer.cia.core.service.host;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.functional.coriander.CallClientApiComponent;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.RoleDeviceEntity;
import tw.amer.cia.core.model.database.dao.RoleAuthorityEntityRepo;
import tw.amer.cia.core.model.database.dao.RoleDeviceEntityRepo;
import tw.amer.cia.core.model.pojo.service.common.role.CreateRoleDeviceHostDto;
import tw.amer.cia.core.model.pojo.service.common.role.UpdateRoleDeviceHostDto;
import tw.amer.cia.core.service.core.ValidateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@HostService
public class RoleDeviceServiceForHost {

    @Autowired
    ValidateService validateService;

    @Autowired
    RoleDeviceEntityRepo roleDeviceEntityRepo;

    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;

    @Autowired
    CallClientApiComponent callClientApiComponent;

    public String createRoleDeviceFromHost(CreateRoleDeviceHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        validateService.validateFabIdExists(inDto.getFabId());
        RoleDeviceEntity newDevice = RoleDeviceEntity.builder()
                .deviceId(RandomStringUtils.random(
                        GeneralSetting.ROLE_DEVICE_ID_LENGTH,
                        GeneralSetting.ROLE_DEVICE_ID_CONTAIN_CHAR,
                        GeneralSetting.ROLE_DEVICE_ID_CONTAIN_NUMBER))
                .roleId(inDto.getRoleId())
                .fabId(inDto.getFabId())
                .deviceName(inDto.getDeviceName())
                .deviceIp(inDto.getDeviceIp())
                .deviceDesc(inDto.getDeviceDesc())
                .isActive(GeneralSetting.GENERAL_POSITIVE_STRING)
                .build();
        log.debug("Created Role Device: {}", newDevice.toString());

        // Apikey資料儲存
        roleDeviceEntityRepo.save(newDevice);

        // Pushing To Client
        callClientApiComponent.createOrUpdateRoleDeviceFromHost(inDto.getFabId(), newDevice);

        return newDevice.getDeviceId();
    }

    public void updateRoleDeviceFromHost(UpdateRoleDeviceHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        validateService.validateFabIdExists(inDto.getFabId());
        Optional<RoleDeviceEntity> inSearchDevice = roleDeviceEntityRepo.findById(inDto.getDeviceId());
        if (inSearchDevice.isPresent()) {
            RoleDeviceEntity onUpdateDevice = inSearchDevice.get();

            // 優先紀錄 FAB 資訊
            String originalFabId = onUpdateDevice.getFabId();

            // 實行資料複製作業
            BeanUtils.copyNonNullProperties(inDto, onUpdateDevice);
            roleDeviceEntityRepo.save(onUpdateDevice);

            // 檢查是否需要異地重新部署
            if (StringUtils.equalsIgnoreCase(originalFabId, onUpdateDevice.getFabId())) {
                // 不需要異地部署，只需要更新對應FAB的資訊即可
                callClientApiComponent.createOrUpdateRoleDeviceFromHost(
                        onUpdateDevice.getFabId(), onUpdateDevice
                );
            } else {
                // 異地部署，需要先清除原FAB的資訊，再新建於新FAB
                callClientApiComponent.deleteRoleDevice(originalFabId, onUpdateDevice.getDeviceId());
                callClientApiComponent.createOrUpdateRoleDeviceFromHost(onUpdateDevice.getFabId(), onUpdateDevice);
            }
        } else {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.WEB_DEVICE_MANAGEMENT_CAN_NOT_FIND_DEVICE.getCompleteMessage()
            );
        }

    }

    public void deleteRoleDeviceFromHost(@NotBlank String roleId, @NotBlank String deviceId) throws DataSourceAccessException, CiaProcessorException {
        validateService.validateRoleId(roleId);

        Optional<RoleDeviceEntity> inSearchDevice = roleDeviceEntityRepo.findById(deviceId);
        if (inSearchDevice.isPresent()) {
            RoleDeviceEntity onDeleteDevice = inSearchDevice.get();

            // 對Client 進行刪除動作
            callClientApiComponent.deleteRoleDevice(onDeleteDevice.getFabId(), onDeleteDevice.getDeviceId());

            roleDeviceEntityRepo.delete(onDeleteDevice);
        }


    }

    public void updateDeviceActiveStatusFromHost(String deviceId) throws CiaProcessorException {
        Optional<RoleDeviceEntity> inSearchDevice = roleDeviceEntityRepo.findById(deviceId);
        if (inSearchDevice.isPresent()) {
            RoleDeviceEntity onChangeDevice = inSearchDevice.get();

            // 如果開啟則關閉，刪除Client的資料
            // 如果關閉則開啟，構建Client的資料
            if (StringUtils.equalsIgnoreCase(onChangeDevice.getIsActive(), GeneralSetting.GENERAL_POSITIVE_STRING)) {
                callClientApiComponent.deleteRoleDevice(onChangeDevice.getFabId(), onChangeDevice.getDeviceId());
                onChangeDevice.setIsActive(GeneralSetting.GENERAL_NEGATIVE_STRING);
            } else {
                onChangeDevice.setIsActive(GeneralSetting.GENERAL_POSITIVE_STRING);
                callClientApiComponent.createOrUpdateRoleDeviceFromHost(onChangeDevice.getFabId(), onChangeDevice);

            }
            roleDeviceEntityRepo.save(onChangeDevice);
        }
    }

    public List<String> searchAvailableRoleDeviceScopes(String roleId) throws DataSourceAccessException {
        validateService.validateRoleId(roleId);
        List<String> availableFabList = roleAuthorityEntityRepo.findFabIdListByRoleId(roleId);
        return availableFabList;
    }


    public List<RoleDeviceEntity> retrieveRoleDeviceDataByFabId(List<String> fabList) {
        // Build Result
        List<RoleDeviceEntity> result = new ArrayList<>();

        // Process
        List<RoleDeviceEntity> deviceList = roleDeviceEntityRepo.findDistinctByFabIdIn(fabList);
        if (CollectionUtils.isNotEmpty(deviceList)) {
            return deviceList;
        }
        return result;
    }

    public void deleteAllRoleDeviceFromHost(String roleId) throws DataSourceAccessException, CiaProcessorException {
        List<RoleDeviceEntity> roleDeviceList = roleDeviceEntityRepo.findByRoleId(roleId);
        for(RoleDeviceEntity roleDevice : roleDeviceList){
            this.deleteRoleDeviceFromHost(roleId,roleDevice.getDeviceId());
        }

    }
}
