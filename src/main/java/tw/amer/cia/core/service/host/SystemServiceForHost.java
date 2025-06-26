package tw.amer.cia.core.service.host;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.functional.coriander.CallClientApiComponent;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.component.structural.property.CoreProperties;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.FabEntity;
import tw.amer.cia.core.model.database.SystemDpyEntity;
import tw.amer.cia.core.model.database.SystemEntity;
import tw.amer.cia.core.model.database.dao.FabEntityRepo;
import tw.amer.cia.core.model.database.dao.SystemDpyEntityRepo;
import tw.amer.cia.core.model.database.dao.SystemEntityRepo;
import tw.amer.cia.core.model.pojo.service.common.system.*;
import tw.amer.cia.core.model.pojo.service.host.web.manager.systemManagement.Web_SystemDeployBySiteDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.systemManagement.Web_SystemDeployOverviewDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.systemManagement.Web_SystemDeployOverviewStatusDto;
import tw.amer.cia.core.service.core.ValidateService;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@HostService
@Slf4j
public class SystemServiceForHost {
    @Autowired
    ValidateService validateService;
    @Autowired
    CallClientApiComponent callClientApiComponent;
    @Autowired
    SystemEntityRepo systemEntityRepo;
    @Autowired
    SystemDpyEntityRepo systemDpyEntityRepo;
    @Autowired
    FabEntityRepo fabEntityRepo;
    @Autowired
    CoreProperties coreProperties;

    @Transactional(rollbackFor = {Exception.class})
    public void createSystem(CreateSystemHostDto dto) throws DataSourceAccessException, CiaProcessorException {

        // Data Check
        boolean illegalInput = StringUtils.isEmpty(dto.getSystemName());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_SYSTEM_SYSTEM_NAME_INCORRECT_INPUT.getCompleteMessage());
        }
        validateService.validateSystemNameDuplicate(dto.getSystemName());
        validateService.validateUserId(dto.getOwner());

        // Action
        SystemEntity newSystem = SystemEntity.builder()
                .systemId(RandomStringUtils.random(GeneralSetting.SYSTEM_ID_LENGTH,
                        GeneralSetting.SYSTEM_ID_CONTAIN_CHAR, GeneralSetting.SYSTEM_ID_CONTAIN_NUMBER))
                .systemName(dto.getSystemName())
                .activeStatus(GeneralSetting.GENERAL_ACTIVE_STATUS_ACTIVE)
                .createTime(Instant.now())
                .applicableFlag("Y")
                .build();
        BeanUtils.copyNonNullProperties(dto, newSystem);

        // Data
        systemEntityRepo.save(newSystem);

        // Deployment Control
        if (CollectionUtils.isNotEmpty(dto.getDeployment())) {
            this.manageSystemDeployment(newSystem, dto.getDeployment());
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void manageSystemDeployment(SystemEntity system, List<SystemDeploymentDto> newDeployList) throws CiaProcessorException, DataSourceAccessException {
        List<SystemDpyEntity> existedList = systemDpyEntityRepo.findBySystemId(system.getSystemId());

        newDeployList = CollectionUtils.isNotEmpty(newDeployList) ? newDeployList : new ArrayList<>();

        newDeployList.forEach(SystemDeploymentDto ->
        {
            try {
                validateService.validateFabIdExists(SystemDeploymentDto.getFabId());
            } catch (DataSourceAccessException e) {
                throw new RuntimeException(e);
            }
        });

        // Build Index Map for comparing
        Map<String, SystemDpyEntity> onModifyDeployMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(existedList)) {
            for (SystemDpyEntity deploy : existedList) {
                onModifyDeployMap.put(deploy.getFabId(), deploy);
            }
        }
        Map<String, SystemDeploymentDto> onUpdateDeployMap = new HashMap<>();
        for (SystemDeploymentDto deploy : newDeployList) {
            onUpdateDeployMap.put(deploy.getFabId(), deploy);
        }

        // Action
        // modifyDeployment
        // buildDeployment
        if (MapUtils.isNotEmpty(onModifyDeployMap)) {
            for (String fabId : onModifyDeployMap.keySet()) {
                if (onUpdateDeployMap.containsKey(fabId)) {
                    // UPDATE DEPLOY
                    SystemDeploymentDto onUpdateObject = onUpdateDeployMap.get(fabId);
                    SystemDpyEntity onModifyObject = onModifyDeployMap.get(fabId);
                    BeanUtils.copyNonNullProperties(onUpdateObject, onModifyObject);

                    // CIA Data Store
                    systemDpyEntityRepo.save(onModifyObject);

                    // Call Client service
                    callClientApiComponent.createOrUpdateSystem(fabId, system);
                    callClientApiComponent.createOrUpdateSystemDeployment(fabId, onModifyObject);

                    // Erase updated single item
                    onUpdateDeployMap.remove(fabId);
                } else {
                    // Deployment Data
                    SystemDpyEntity onDeleteDeployment = onModifyDeployMap.get(fabId);

                    // 確認是否仍有部署API
                    boolean hasApiDeployInFab = validateService.validateSystemHasApiDeployInFab(
                            onDeleteDeployment.getSystemId(), onDeleteDeployment.getFabId());
                    if (hasApiDeployInFab) {
                        String message = "Target SystemEntity " + onDeleteDeployment.getSystemId() + " still has api in fab: " + onDeleteDeployment.getFabId();
                        log.info(message);
                        throw DataSourceAccessException.createExceptionForHttp(
                                HttpStatus.BAD_REQUEST,
                                message
                        );
                    } else {
                        // Call Client service
                        callClientApiComponent.deleteSystemDeployment(fabId, onDeleteDeployment);

                        // CIA Data Store
                        systemDpyEntityRepo.delete(onDeleteDeployment);
                    }
                }
            }
        }

        if (MapUtils.isNotEmpty(onUpdateDeployMap)) {
            for (SystemDeploymentDto newDeployment : onUpdateDeployMap.values()) {
                SystemDpyEntity newDeployEntity = SystemDpyEntity.builder()
                        .systemId(system.getSystemId())
                        .fabId(newDeployment.getFabId())
                        .activeStatus("ACTIVE")
                        .createTime(Instant.now())
                        .build();
                BeanUtils.copyNonNullProperties(newDeployment, newDeployEntity);

                // CIA Data Store
                systemDpyEntityRepo.save(newDeployEntity);

                // Call Client service
                callClientApiComponent.createOrUpdateSystem(newDeployment.getFabId(), system);
                callClientApiComponent.createOrUpdateSystemDeployment(newDeployment.getFabId(), newDeployEntity); // CiaProcessorException
            }
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void createOrUpdateSystemDeployWithOnlyModifiedDeployment(String systemId, List<SystemDeploymentDto> modifiedDeployment) throws DataSourceAccessException, CiaProcessorException {
        // Follows mechanism of CIA 2.X
        SystemEntity system = validateService.validateSystemId(systemId);
        List<SystemDpyEntity> dpyList = systemDpyEntityRepo.findBySystemId(systemId);
        Map<String, SystemDpyEntity> existsDpyByFabIdMap = dpyList.stream().collect(Collectors.toMap(SystemDpyEntity::getFabId, Function.identity()));
        List<String> modifiedFabList = modifiedDeployment.stream().map(SystemDeploymentDto::getFabId).collect(Collectors.toList());

        List<SystemDeploymentDto> modifiedAbleDpyList = new ArrayList<>(modifiedDeployment);
        for (String fabId : existsDpyByFabIdMap.keySet()) {
            if (!modifiedFabList.contains(fabId)) {
                // 若有FAB是沒有被修改的，則補上原資料
                SystemDeploymentDto originDpy = new SystemDeploymentDto();
                BeanUtils.copyNonNullProperties(existsDpyByFabIdMap.get(fabId), originDpy);
                modifiedAbleDpyList.add(originDpy);
            }
        }
        // 採用 CIA 2.X的規格進行更新
        manageSystemDeployment(system, modifiedAbleDpyList);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteSystemDeployWithSystemAndFabId(String systemId, String onCancelFabId) throws DataSourceAccessException, CiaProcessorException {
        // Follows mechanism of CIA 2.X
        SystemEntity system = validateService.validateSystemId(systemId);
        List<SystemDpyEntity> dpyList = systemDpyEntityRepo.findBySystemId(systemId);
        List<SystemDeploymentDto> newDpyList = dpyList.stream()
                .map(cSysDpy -> {
                    SystemDeploymentDto dpyDto = new SystemDeploymentDto();
                    BeanUtils.copyNonNullProperties(cSysDpy, dpyDto);
                    return dpyDto;
                })
                .filter(dto -> !StringUtils.equals(dto.getFabId(), onCancelFabId))
                .collect(Collectors.toList());
        // 採用 CIA 2.X的規格進行更新
        manageSystemDeployment(system, newDpyList);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void updateSystem(UpdateSystemHostDto dto) throws DataSourceAccessException, CiaProcessorException {

        // Data Check
        boolean illegalInput = StringUtils.isEmpty(dto.getSystemName());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_SYSTEM_SYSTEM_NAME_INCORRECT_INPUT.getCompleteMessage());
        }
        SystemEntity onUpdateSystem = validateService.validateSystemName(dto.getSystemName());
        validateService.validateUserId(dto.getOwner());

        // Action
        // updateDeploy
        // updateSystem

        // Update Data
        BeanUtils.copyNonNullProperties(dto, onUpdateSystem);
        systemEntityRepo.save(onUpdateSystem);
        this.updateSystemToDeployedClients(onUpdateSystem);

        // Update Deploy
        if (CollectionUtils.isNotEmpty(dto.getDeployment())) {
            this.manageSystemDeployment(onUpdateSystem, dto.getDeployment());
        }
    }

    private void updateSystemToDeployedClients(SystemEntity onUpdateSystem) throws CiaProcessorException {
        List<String> deployFabList = systemDpyEntityRepo.findFabIdBySystemId(onUpdateSystem.getSystemId());
        if (CollectionUtils.isNotEmpty(deployFabList)) {
            callClientApiComponent.updateSystemToDeployedClients(deployFabList, onUpdateSystem);
        }
    }

    public void deleteSystemById(String systemId) throws DataSourceAccessException, CiaProcessorException {
        // Follows Delete Rules in CIA 2.X
        SystemEntity onDeleteSystem = validateService.validateSystemId(systemId);
        deleteSystem(new DeleteSystemHostDto(onDeleteSystem.getSystemName()));
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteSystem(DeleteSystemHostDto dto) throws DataSourceAccessException, CiaProcessorException {

        // Data Check
        boolean illegalInput = StringUtils.isEmpty(dto.getSystemName());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_SYSTEM_SYSTEM_NAME_INCORRECT_INPUT.getCompleteMessage());
        }
        SystemEntity onDeleteSystem = validateService.validateSystemName(dto.getSystemName());
        boolean hasApi = validateService.validateSystemHasApi(onDeleteSystem.getSystemId());
        if (hasApi) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_SYSTEM_DELETE_SYSTEM_HAS_API.getCompleteMessage());
        }

        // Action
        // eraseDeployment
        // deleteSystem

        // Get DeploymentList
        List<String> deployFabList = systemDpyEntityRepo.findFabIdBySystemId(onDeleteSystem.getSystemId());

        // Erase Deployment
        this.manageSystemDeployment(onDeleteSystem, null);

        // Clean DB
        systemEntityRepo.delete(onDeleteSystem);
    }

    public List<SystemDeploymentDto> retrieveSystemDeploymentList(String systemId) {
        List<SystemDpyEntity> deployData = systemDpyEntityRepo.findBySystemId(systemId);
        return deployData.stream()
                .map(SystemDeploymentDto::new)
                .collect(Collectors.toList());
    }

    public List<CompleteSystemDto> retrieveCompleteSystemDataByFabId(Collection<String> fabIdSet) {
        // Build Result
        List<CompleteSystemDto> result = new ArrayList<>();

        // Process
        List<SystemDpyEntity> dpyList = systemDpyEntityRepo.findByFabIdIn(fabIdSet);
        if (CollectionUtils.isNotEmpty(dpyList)) {
            Set<String> systemIdSet = dpyList.stream().map(SystemDpyEntity::getSystemId).collect(Collectors.toSet());
            systemIdSet.forEach(systemId ->
            {
                Optional<SystemEntity> onSearchSystem = systemEntityRepo.findBySystemId(systemId);
                if (onSearchSystem.isPresent()) {
                    SystemEntity targetSystem = onSearchSystem.get();
                    List<SystemDpyEntity> targetList = dpyList.stream()
                            .filter(obj -> StringUtils.isNotEmpty(obj.getSystemId()) &&
                                    StringUtils.equals(obj.getSystemId(), systemId))
                            .collect(Collectors.toList());
                    result.add(CompleteSystemDto.builder()
                            .system(targetSystem)
                            .deployList(targetList)
                            .build());
                }
            });
        }
        return result;
    }

    public Web_SystemDeployOverviewDto webGetSystemDeployInfoById(String systemId) throws DataSourceAccessException {
        SystemEntity system = validateService.validateSystemId(systemId);
        List<SystemDpyEntity> deployments = systemDpyEntityRepo.findBySystemId(systemId);

        Web_SystemDeployOverviewDto result = Web_SystemDeployOverviewDto.builder().systemId(systemId).build();
        result.setOverviewStatus(Web_SystemDeployOverviewStatusDto.importFromTableData(deployments));

        // Using Map toward a better retrieve efficiency
        Map<String, String> siteByFabId =
                fabEntityRepo.findAllById(deployments.stream()
                                .map(SystemDpyEntity::getFabId)
                                .collect(Collectors.toSet()))
                        .stream()
                        .collect(Collectors.toMap(FabEntity::getFabId, FabEntity::getSite));
        Map<String, List<SystemDpyEntity>> deployBySiteMap = deployments.stream()
                .collect(Collectors.groupingBy(cSysDpy -> siteByFabId.get(cSysDpy.getFabId())));

        List<Web_SystemDeployBySiteDto> siteDeployments = deployBySiteMap.keySet().stream()
                .map(site -> {
                    return Web_SystemDeployBySiteDto.builder()
                            .site(site)
                            .deploymentList(
                                    deployBySiteMap.get(site).stream()
                                            .map(SystemDeploymentDto::new)
                                            .sorted(Comparator.comparing(SystemDeploymentDto::getFabId))
                                            .collect(Collectors.toList())
                            )
                            .build();
                })
                .sorted(Comparator.comparing(Web_SystemDeployBySiteDto::getSite))
                .collect(Collectors.toList());
        result.setDeployBySiteList(siteDeployments);
        return result;
    }

    public List<String> getSystemDeployAvailableFabIdsBySystemId(String systemId) throws DataSourceAccessException {
        validateService.validateSystemId(systemId);
        List<String> existsFab = systemDpyEntityRepo.findFabIdBySystemId(systemId).stream().collect(Collectors.toList());
        return coreProperties.getClientDeployMapByFabId().keySet().stream()
                .filter(fab -> !existsFab.contains(fab))
                .collect(Collectors.toList());
    }
}
