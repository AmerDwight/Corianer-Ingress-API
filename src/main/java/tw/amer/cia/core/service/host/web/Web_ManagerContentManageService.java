package tw.amer.cia.core.service.host.web;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ApiEntity;
import tw.amer.cia.core.model.database.FabEntity;
import tw.amer.cia.core.model.database.FabSignOffConfigEntity;
import tw.amer.cia.core.model.database.ApiDpyEntity;
import tw.amer.cia.core.model.database.dao.*;
import tw.amer.cia.core.model.pojo.service.host.web.manager.contentManagement.*;
import tw.amer.cia.core.service.core.ValidateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@HostService
public class Web_ManagerContentManageService {
    @Autowired
    ValidateService validateService;
    @Autowired
    SystemEntityRepo systemEntityRepo;
    @Autowired
    ApiEntityRepo apiEntityRepo;
    @Autowired
    ApiDpyEntityRepo apiDpyEntityRepo;
    @Autowired
    FabEntityRepo fabEntityRepo;
    @Autowired
    UserEntityRepo userEntityRepo;
    @Autowired
    FabSignOffConfigEntityRepo fabSignOffConfigEntityRepo;

    public List<Web_SystemItemDto> listSystemsForContentManage() {
        return systemEntityRepo.listSystemsForContentManageOrderByNameAsc();
    }

    public List<Web_ApiDto> listApiForContentManageBySystemId(String systemId) throws DataSourceAccessException {
        // Verify
        validateService.validateSystemId(systemId);

        // 前置資料準備
        List<Web_ApiDto> result = new ArrayList<>();
        List<ApiEntity> apiList = apiEntityRepo.findBySystemIdOrderByApiName(systemId);

        // 資料組建
        if (CollectionUtils.isNotEmpty(apiList)) {
            List<ApiDpyEntity> allDpyList = apiDpyEntityRepo.findByApiIdIn(apiList.stream().map(ApiEntity::getApiId).collect(Collectors.toSet()));
            Map<String, List<String>> fabListByApiIdMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(allDpyList)) {
                allDpyList.forEach(
                        apiDpy -> {
                            fabListByApiIdMap.computeIfAbsent(
                                    apiDpy.getApiId(), k -> new ArrayList<>()
                            ).add(apiDpy.getFabId());
                        }
                );
            }

            apiList.forEach(
                    api -> {
                        List<String> siteListForApi = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(fabListByApiIdMap.get(api.getApiId()))) {
                            siteListForApi = fabEntityRepo.findDistinctSiteByFabIdIn(fabListByApiIdMap.get(api.getApiId()));
                        }
                        result.add(
                                Web_ApiDto.builder()
                                        .apiId(api.getApiId())
                                        .apiName(api.getApiName())
                                        .apiType(api.getApiType())
                                        .activeStatus(api.getActiveStatus())
                                        .site(siteListForApi).build()
                        );
                    }
            );
        }
        return result;
    }

    public List<Web_SiteOperateChargerDto> listSiteOperateCharger() {
        return fabSignOffConfigEntityRepo.listSiteOperateChargerByFabIdAsc();
    }

    public void updateSiteOperateCharger(Collection<Web_SiteOperateChargerDto> inDto) throws DataSourceAccessException {
        if (CollectionUtils.isNotEmpty(inDto)) {
            Set<String> fabIds = inDto.stream().map(Web_SiteOperateChargerDto::getFabId).collect(Collectors.toSet());
            validateService.validateFabIdsExists(fabIds);

            // Organize by fabId
            List<FabSignOffConfigEntity> chargerList = fabSignOffConfigEntityRepo.findDistinctByFabIdIn(fabIds);
            Map<String, FabSignOffConfigEntity> chargerByFabIdMap =
                    chargerList.stream().collect(Collectors.toMap(FabSignOffConfigEntity::getFabId, Function.identity()));

            for (Web_SiteOperateChargerDto dto : inDto) {
                if (chargerByFabIdMap.containsKey(dto.getFabId())) {
                    chargerByFabIdMap.get(dto.getFabId()).setSiteManagerId(dto.getUserId());
                } else {
                    chargerByFabIdMap.put(dto.getFabId(),
                            FabSignOffConfigEntity.builder()
                                    .fabId(dto.getFabId())
                                    .siteManagerId(dto.getUserId()).build());
                }
            }
            fabSignOffConfigEntityRepo.saveAll(chargerByFabIdMap.values());
        } else {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getMessage()
            );
        }
    }

    public List<Web_UserDto> listUserByUserIdLike(String userId) {
        userId = "%" + userId + "%";
        return Web_UserDto.importFromUserTable(userEntityRepo.findByUserIdLikeOrderByUserId(userId));
    }

    public Web_SiteWithFabDto getSiteInfoByFabId(String fabId) throws DataSourceAccessException {
        Optional<FabEntity> onSearchFab = fabEntityRepo.findByFabId(fabId);
        if (onSearchFab.isPresent()) {
            List<FabEntity> fabInSite = fabEntityRepo.findBySite(onSearchFab.get().getSite());
            return Web_SiteWithFabDto.builder()
                    .site(onSearchFab.get().getSite())
                    .fabList(fabInSite.stream().map(FabEntity::getFabId).collect(Collectors.toList()))
                    .build();
        }
        throw DataSourceAccessException.createExceptionForHttp(
                HttpStatus.NO_CONTENT,
                "NO_CONTENT"
        );
    }

    public Object getContentAttributeStatus() {
        return Arrays.asList(GeneralSetting.GENERAL_ACTIVE_STATUS_ACTIVE,GeneralSetting.GENERAL_ACTIVE_STATUS_NON_ACTIVE);
    }
}
