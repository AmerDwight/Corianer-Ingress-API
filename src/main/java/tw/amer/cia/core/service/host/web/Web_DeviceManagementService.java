package tw.amer.cia.core.service.host.web;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.model.database.dao.RoleDeviceEntityRepo;
import tw.amer.cia.core.model.database.RoleDeviceEntity;
import tw.amer.cia.core.model.pojo.service.common.role.CreateRoleDeviceHostDto;
import tw.amer.cia.core.model.pojo.service.common.role.UpdateRoleDeviceHostDto;
import tw.amer.cia.core.model.pojo.service.host.web.deviceManagement.Web_RoleDeviceAvailableScopeOutDto;
import tw.amer.cia.core.model.pojo.service.host.web.deviceManagement.Web_RoleDeviceCreateInDto;
import tw.amer.cia.core.model.pojo.service.host.web.deviceManagement.Web_RoleDeviceUpdateInDto;
import tw.amer.cia.core.service.core.ValidateService;
import tw.amer.cia.core.service.host.RoleDeviceServiceForHost;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Slf4j
@HostService
public class Web_DeviceManagementService {

    @Autowired
    ValidateService validateService;

    @Autowired
    RoleDeviceEntityRepo roleDeviceEntityRepo;

    @Autowired
    RoleDeviceServiceForHost roleDeviceServiceForHost;

    public Page<RoleDeviceEntity> findDeviceByRoleIdOrderByDeviceNameAsc(String roleId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(RoleDeviceEntity.Fields.deviceName).ascending());
        Page<RoleDeviceEntity> sourcePage = roleDeviceEntityRepo.findByRoleIdOrderByDeviceNameAsc(roleId, pageable);
        if (CollectionUtils.isNotEmpty(sourcePage.getContent())) {
            return sourcePage;
        }
        return new PageImpl<>(Collections.emptyList());
    }

    public String createRoleDevice(String roleId, Web_RoleDeviceCreateInDto inDto) throws DataSourceAccessException, CiaProcessorException {
        validateService.validateIpStringAcceptIPv4AndSubNetLength(inDto.getDeviceIp());
        String newApikeyId = roleDeviceServiceForHost.createRoleDeviceFromHost(
                CreateRoleDeviceHostDto.builder()
                        .roleId(roleId)
                        .deviceName(inDto.getDeviceName())
                        .deviceIp(inDto.getDeviceIp())
                        .deviceDesc(inDto.getDeviceDesc())
                        .fabId(inDto.getFabId())
                        .build()
        );
        return newApikeyId;
    }

    public String updateRoleDevice(String roleId, String deviceId, Web_RoleDeviceUpdateInDto inDto) throws DataSourceAccessException, CiaProcessorException {
        validateService.validateIpStringAcceptIPv4AndSubNetLength(inDto.getDeviceIp());
        if (StringUtils.equalsIgnoreCase(roleId, inDto.getRoleId())) {
            UpdateRoleDeviceHostDto onUpdateDevice = new UpdateRoleDeviceHostDto();
            BeanUtils.copyNonNullProperties(inDto, onUpdateDevice);
            roleDeviceServiceForHost.updateRoleDeviceFromHost(onUpdateDevice);
            return deviceId;
        }
        throw DataSourceAccessException.createExceptionForHttp(
                HttpStatus.BAD_REQUEST,
                ErrorConstantLib.GENERAL_API_INPUT_ID_MISMATCH.getCompleteMessage()
        );
    }


    public String deleteRoleDevice(String roleId, String onDeleteDeviceId) throws DataSourceAccessException, CiaProcessorException {
        if (StringUtils.isNotBlank(roleId) && StringUtils.isNotBlank(onDeleteDeviceId)) {
            roleDeviceServiceForHost.deleteRoleDeviceFromHost(roleId, onDeleteDeviceId);
            return onDeleteDeviceId;
        }
        throw DataSourceAccessException.createExceptionForHttp(
                HttpStatus.BAD_REQUEST,
                ErrorConstantLib.GENERAL_API_MISSING_CRITICAL_DATA_INPUT.getCompleteMessage()
        );
    }

    public String updateDeviceActiveStatus(String deviceId) throws CiaProcessorException, DataSourceAccessException {
        if (StringUtils.isNotBlank(deviceId)) {
            roleDeviceServiceForHost.updateDeviceActiveStatusFromHost(deviceId);
            return deviceId;
        }
        throw DataSourceAccessException.createExceptionForHttp(
                HttpStatus.BAD_REQUEST,
                ErrorConstantLib.GENERAL_API_MISSING_CRITICAL_DATA_INPUT.getCompleteMessage()
        );
    }

    public Web_RoleDeviceAvailableScopeOutDto searchAvailableRoleDeviceScopes(String roleId) throws DataSourceAccessException {
        if (StringUtils.isNotBlank(roleId)) {
            List<String> availableScopeList = roleDeviceServiceForHost.searchAvailableRoleDeviceScopes(roleId);
            return Web_RoleDeviceAvailableScopeOutDto.builder()
                    .roleId(roleId)
                    .availableFabList(availableScopeList).build();
        }
        throw DataSourceAccessException.createExceptionForHttp(
                HttpStatus.BAD_REQUEST,
                ErrorConstantLib.GENERAL_API_MISSING_CRITICAL_DATA_INPUT.getCompleteMessage()
        );
    }
}
