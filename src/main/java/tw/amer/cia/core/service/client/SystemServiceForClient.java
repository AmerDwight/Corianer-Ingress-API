package tw.amer.cia.core.service.client;

import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.functional.gateway.GatewayControlHelper;
import tw.amer.cia.core.component.structural.annotation.ClientService;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.GwUpstreamEntity;
import tw.amer.cia.core.model.database.SystemEntity;
import tw.amer.cia.core.model.database.SystemDpyEntity;
import tw.amer.cia.core.model.database.dao.SystemDpyEntityRepo;
import tw.amer.cia.core.model.database.dao.SystemEntityRepo;
import tw.amer.cia.core.model.database.dao.GwUpstreamEntityRepo;
import tw.amer.cia.core.service.core.ValidateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@ClientService
@Slf4j
public class SystemServiceForClient
{
    @Autowired
    ValidateService validateService;

    @Autowired
    SystemEntityRepo systemEntityRepo;

    @Autowired
    SystemDpyEntityRepo systemDpyEntityRepo;

    @Autowired
    GwUpstreamEntityRepo gwUpstreamEntityRepo;

    @Autowired
    GatewayControlHelper gatewayControlHelper;

    // 0513 CIA，切分開發
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrUpdateSystem(SystemEntity system)
    {
        boolean procedureSuccess = true;

        Optional<SystemEntity> inSearchSystem = systemEntityRepo.findBySystemId(system.getSystemId());
        if (inSearchSystem.isPresent())
        {
            SystemEntity localSystemObject = inSearchSystem.get();
            BeanUtils.copyNonNullProperties(system, localSystemObject);
            systemEntityRepo.save(localSystemObject);
        } else
        {
            systemEntityRepo.save(system);
        }

        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createOrUpdateSystemDeployment(SystemDpyEntity deployDto) throws GatewayControllerException, DataSourceAccessException
    {
        boolean procedureSuccess = true;

        Optional<SystemDpyEntity> inSearchSystemDeploy = systemDpyEntityRepo.findBySystemIdAndFabId(deployDto.getSystemId(), deployDto.getFabId());
        SystemDpyEntity localSystemDeployObject;
        if (inSearchSystemDeploy.isPresent())
        {
            localSystemDeployObject = inSearchSystemDeploy.get();
            BeanUtils.copyNonNullProperties(deployDto, localSystemDeployObject);
        } else
        {
            localSystemDeployObject = deployDto;
        }
        procedureSuccess = this.createOrUpdateGwUpstream(localSystemDeployObject);
        systemDpyEntityRepo.save(localSystemDeployObject);

        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean createOrUpdateGwUpstream(SystemDpyEntity onDeployment) throws GatewayControllerException, DataSourceAccessException
    {
        boolean procedureSuccess = true;
        SystemEntity onModifySystem = validateService.validateSystemId(onDeployment.getSystemId());

        Optional<GwUpstreamEntity> inSearchUpstream = gwUpstreamEntityRepo.findByFabIdAndSystemId(onDeployment.getFabId(), onDeployment.getSystemId());
        boolean isUpdateProcedure = inSearchUpstream.isPresent();
        if (isUpdateProcedure)
        {
            GwUpstreamEntity onUpdateUpstream = inSearchUpstream.get();
            procedureSuccess = gatewayControlHelper.createOrUpdateGwUpstream(onUpdateUpstream.getGwUpstreamId(), onModifySystem.getSystemName(), onDeployment);

        } else
        {
            GwUpstreamEntity newUpstream = GwUpstreamEntity.builder()
                    .gwUpstreamId(RandomStringUtils.random(GeneralSetting.GW_UPSTREAM_ID_LENGTH,
                            GeneralSetting.GW_UPSTREAM_ID_CONTAIN_CHAR, GeneralSetting.GW_UPSTREAM_ID_CONTAIN_NUMBER))
                    .systemId(onDeployment.getSystemId())
                    .fabId(onDeployment.getFabId())
                    .build();
            procedureSuccess = gatewayControlHelper.createOrUpdateGwUpstream(newUpstream.getGwUpstreamId(), onModifySystem.getSystemName(), onDeployment);
            gwUpstreamEntityRepo.save(newUpstream);
        }
        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSystemDeployment(SystemDpyEntity inDto) throws GatewayControllerException
    {
        boolean procedureSuccess = true;

        // 刪除此次 Deployment
        Optional<SystemDpyEntity> inSearchSystemDeploy = systemDpyEntityRepo.findBySystemIdAndFabId(inDto.getSystemId(), inDto.getFabId());
        if (inSearchSystemDeploy.isPresent())
        {
            SystemDpyEntity onDeleteDeployment = inSearchSystemDeploy.get();
            procedureSuccess = this.deleteSystemGwDeployment(onDeleteDeployment);
            // Config Data Store
            systemDpyEntityRepo.delete(onDeleteDeployment);
        }

        // 檢查當前控制FAB區域是否有其他Deployment資料
        // 若沒有，則清除Client端資料
        List<SystemDpyEntity> systemDeployList = systemDpyEntityRepo.findBySystemId(inDto.getSystemId());
        if (CollectionUtils.isEmpty(systemDeployList))
        {
            log.info("Client side has no deploy data, start purge procedure.");
            Optional<SystemEntity> inSearchSystem = systemEntityRepo.findBySystemId(inDto.getSystemId());
            if (inSearchSystem.isPresent())
            {
                // Config Data Store
                systemEntityRepo.delete(inSearchSystem.get());
                log.info("Client Side delete system complete.");
            }
        }

        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean deleteSystemGwDeployment(SystemDpyEntity onDeleteDeployment) throws GatewayControllerException
    {
        boolean procedureSuccess = true;

        Optional<GwUpstreamEntity> onDeleteUpstream = gwUpstreamEntityRepo.findByFabIdAndSystemId(onDeleteDeployment.getFabId(), onDeleteDeployment.getSystemId());
        if (onDeleteUpstream.isPresent())
        {
            GwUpstreamEntity thisUpstream = onDeleteUpstream.get();
            procedureSuccess = gatewayControlHelper.deleteGwUpstream(thisUpstream.getFabId(), thisUpstream.getGwUpstreamId());
            gwUpstreamEntityRepo.delete(thisUpstream);
        }
        return procedureSuccess;
    }
}
