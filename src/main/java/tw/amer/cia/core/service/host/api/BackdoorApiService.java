package tw.amer.cia.core.service.host.api;

import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.dao.ApiDpyEntityRepo;
import tw.amer.cia.core.model.database.dao.RoleAuthorityEntityRepo;
import tw.amer.cia.core.model.pojo.service.common.role.UpdateRoleHostDto;
import tw.amer.cia.core.service.core.ValidateService;
import tw.amer.cia.core.service.host.RoleServiceForHost;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@HostService
public class BackdoorApiService {
    @Autowired
    ApiDpyEntityRepo apiDpyEntityRepo;
    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;
    @Autowired
    RoleServiceForHost roleServiceForHost;
    @Autowired
    ValidateService validateService;

    public void liveUpdateSupremeAuthority(String roleId) {
        List<Map<String, String>> apiFabCombinations = apiDpyEntityRepo.findMissingAuthorityCombineByRoleId(roleId);

        Map<String, List<String>> forServiceToUpdateFormat = new HashMap<>();
        apiFabCombinations.forEach(combination ->
        {
            String apiId = combination.get("apiId");
            String fabId = combination.get("fabId");
            if(validateService.validateIsNotSandBoxFab(fabId)){
                forServiceToUpdateFormat.computeIfAbsent(fabId, k -> new ArrayList<>()).add(apiId);
            }
        });

        forServiceToUpdateFormat.keySet().forEach(fabId ->
        {
            try {
                List<String> finalApiIds = (List<String>) CollectionUtils.union(roleAuthorityEntityRepo.findApiIdByRoleIdAndFabId(roleId, fabId),
                        forServiceToUpdateFormat.get(fabId));

                roleServiceForHost.updateRole(
                        UpdateRoleHostDto.builder()
                                .roleId(roleId)
                                .fabId(fabId)
                                .apiIdList(finalApiIds)
                                .build());
            } catch (DataSourceAccessException | CiaProcessorException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
