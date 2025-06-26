package tw.amer.cia.core.service.client;

import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.structural.annotation.ClientService;
import tw.amer.cia.core.model.database.dao.RoleAuthorityEntityRepo;
import tw.amer.cia.core.model.database.dao.RoleEntityRepo;
import tw.amer.cia.core.model.database.RoleAuthorityEntity;
import tw.amer.cia.core.model.database.RoleEntity;
import tw.amer.cia.core.model.database.compositeId.RoleAuthoroityEntityId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ClientService
@Slf4j
public class RoleServiceForClient
{
    @Autowired
    RoleEntityRepo roleEntityRepo;
    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;

    // 0516 CIA，切分開發
    public boolean createOrUpdateRole(RoleEntity role)
    {
        boolean procedureSuccess = true;

        Optional<RoleEntity> inSearchRole = roleEntityRepo.findByRoleId(role.getRoleId());
        if (inSearchRole.isPresent())
        {
            RoleEntity localRoleObject = inSearchRole.get();
            BeanUtils.copyNonNullProperties(role, localRoleObject);
            roleEntityRepo.save(localRoleObject);
        } else
        {
            roleEntityRepo.save(role);
        }

        return procedureSuccess;
    }

    public boolean deleteRoleAuthority(List<RoleAuthoroityEntityId> inDto)
    {
        boolean procedureSuccess = true;
        if (CollectionUtils.isNotEmpty(inDto))
        {

            // 事先紀錄 Role ID，若之後Role下面沒有權限，則對Client 進行Role的刪除
            // Role下面的Apikey，由Host來進行control
            Set<String> roleIdListSet = inDto.stream()
                    .map(RoleAuthoroityEntityId::getRoleId)
                    .collect(Collectors.toSet());

            List<RoleAuthorityEntity> targetList = roleAuthorityEntityRepo.findAllById(inDto);
            if (CollectionUtils.isNotEmpty(targetList))
            {
                roleAuthorityEntityRepo.deleteAll(targetList);
            }

            // 檢查Role存在必要性，無存在控制區域權限就對 Client 進行 Role刪除
            for (String roleId : roleIdListSet)
            {
                List<RoleAuthorityEntity> checkRoleAuthority = roleAuthorityEntityRepo.findByRoleId(roleId);
                if (CollectionUtils.isEmpty(checkRoleAuthority))
                {
                    roleEntityRepo.deleteById(roleId);
                }
            }
        }

        return procedureSuccess;
    }
}
