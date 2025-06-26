package tw.amer.cia.core.controller.host.web;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.component.structural.annotation.RequireUserVerifyApi;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.pojo.service.host.web.deviceManagement.Web_RoleDeviceCreateInDto;
import tw.amer.cia.core.model.pojo.service.host.web.deviceManagement.Web_RoleDeviceUpdateInDto;
import tw.amer.cia.core.service.host.web.Web_DeviceManagementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@HostRestController
@RequireUserVerifyApi
@RequestMapping("/web/deviceManagement")
public class DeviceManagementController {

    @Autowired
    Web_DeviceManagementService webDeviceManagementService;

    @GetMapping("/device")
    public Object webDeviceManagementFindDeviceByRoleIdOrderByDeviceNameAsc(@RequestParam(defaultValue = "0") int pageNumber,
                                                                            @RequestParam(defaultValue = "10") int pageSize,
                                                                            @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                                            @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) {
        return webDeviceManagementService.findDeviceByRoleIdOrderByDeviceNameAsc(roleId, pageNumber, pageSize);
    }

    @PostMapping("/device")
    public Object webDeviceManagementCreateDevice(@RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                  @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId,
                                                  @Valid @RequestBody Web_RoleDeviceCreateInDto inDto) throws CiaProcessorException, DataSourceAccessException {
        return webDeviceManagementService.createRoleDevice(roleId, inDto);
    }

    @PutMapping("/device/{deviceId}")
    public Object webDeviceManagementUpdateRoleDevice(@PathVariable String deviceId,
                                                      @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                      @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId,
                                                      @Valid @RequestBody Web_RoleDeviceUpdateInDto inDto) throws CiaProcessorException, DataSourceAccessException {
        boolean isDataMismatch = !StringUtils.equals(deviceId, inDto.getDeviceId());
        if (isDataMismatch) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST, ErrorConstantLib.GENERAL_API_INPUT_ID_MISMATCH.getCompleteMessage());
        }
        return webDeviceManagementService.updateRoleDevice(roleId, deviceId, inDto);
    }

    @DeleteMapping("/device/{deviceId}")
    public Object webDeviceManagementCreateDevice(@PathVariable String deviceId,
                                                  @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                  @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) throws CiaProcessorException, DataSourceAccessException {
        return webDeviceManagementService.deleteRoleDevice(roleId, deviceId);
    }

    @PutMapping("/device/action/change/active/{deviceId}")
    public Object webDeviceManagementUpdateDeviceActiveStatus(@PathVariable String deviceId,
                                                              @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                              @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) throws DataSourceAccessException, CiaProcessorException {
        return webDeviceManagementService.updateDeviceActiveStatus(deviceId);
    }

    @GetMapping("/role/available/scope/{pathRoleId}")
    public Object webDeviceManagementSearchAvailableRoleDeviceScopes(@PathVariable String pathRoleId,
                                                                     @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                                     @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) throws CiaProcessorException, DataSourceAccessException {
        return webDeviceManagementService.searchAvailableRoleDeviceScopes(pathRoleId);
    }

}
