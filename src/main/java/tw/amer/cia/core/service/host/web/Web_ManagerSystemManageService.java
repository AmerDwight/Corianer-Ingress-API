package tw.amer.cia.core.service.host.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.SystemEntity;
import tw.amer.cia.core.model.pojo.service.common.system.CreateSystemHostDto;
import tw.amer.cia.core.model.pojo.service.common.system.UpdateSystemHostDto;
import tw.amer.cia.core.model.pojo.service.host.web.info.detail.Web_DetailSystemInfoOutDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.systemManagement.*;
import tw.amer.cia.core.service.core.ValidateService;
import tw.amer.cia.core.service.host.SystemServiceForHost;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@HostService
public class Web_ManagerSystemManageService {
    @Autowired
    ValidateService validateService;
    @Autowired
    HostWebFrontApiService hostWebFrontApiService;
    @Autowired
    SystemServiceForHost systemServiceForHost;

    public void deleteSystemById(String systemId) throws DataSourceAccessException, CiaProcessorException {
        systemServiceForHost.deleteSystemById(systemId);
    }

    public void createPureSystem(Web_CreatePureSystemDto inDto) throws DataSourceAccessException, CiaProcessorException {
        CreateSystemHostDto createSystemHostDto = new CreateSystemHostDto();
        BeanUtils.copyNonNullProperties(inDto, createSystemHostDto);
        systemServiceForHost.createSystem(createSystemHostDto);
    }

    public void updateBasicSystemInfo(Web_UpdateBasicSystemInfoDto inDto) throws DataSourceAccessException, CiaProcessorException {
        SystemEntity system = validateService.validateSystemId(inDto.getSystemId());
        UpdateSystemHostDto updateSystemHostDto = new UpdateSystemHostDto();

        // Construct Data
        updateSystemHostDto.setSystemName(system.getSystemName());
        BeanUtils.copyNonNullProperties(inDto, updateSystemHostDto);
        systemServiceForHost.updateSystem(updateSystemHostDto);
    }

    public void createOrUpdateSystemDeploy(Web_UpdateSystemDeploymentDto inDto) throws DataSourceAccessException, CiaProcessorException {
        systemServiceForHost.createOrUpdateSystemDeployWithOnlyModifiedDeployment(inDto.getSystemId(), inDto.getDeployment());
    }

    public void deleteSystemDeploy(String fabId, String systemId) throws DataSourceAccessException, CiaProcessorException {
        systemServiceForHost.deleteSystemDeployWithSystemAndFabId(systemId, fabId);
    }

    public Web_DetailSystemInfoOutDto getBasicSystemInfoById(String systemId) throws DataSourceAccessException {
        return hostWebFrontApiService.webGetSystemDetailInfo(systemId);
    }

    public Web_SystemDeployOverviewDto getSystemDeployInfoById(String systemId) throws DataSourceAccessException {
        return systemServiceForHost.webGetSystemDeployInfoById(systemId);
    }

    public Web_SystemNewAvailableDeploymentDto getSystemDeployAvailableFabIdBySystemId(String systemId) throws DataSourceAccessException {
        List<String> availableFabList = systemServiceForHost.getSystemDeployAvailableFabIdsBySystemId(systemId);
        String virtualFabId = GeneralSetting.SANDBOX_FAB.getFabId();
        return Web_SystemNewAvailableDeploymentDto.builder()
                .systemId(systemId)
                .availableRealFabList(availableFabList.stream().filter(fabId -> !StringUtils.equals(fabId, virtualFabId)).collect(Collectors.toList()))
                .availableVirtualFabList(availableFabList.stream().filter(fabId -> StringUtils.equals(fabId, virtualFabId)).collect(Collectors.toList()))
                .build();
    }
}
