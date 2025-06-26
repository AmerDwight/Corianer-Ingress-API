package tw.amer.cia.core.service.host.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import tw.amer.cia.core.common.APISetting;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ApiEndpointEntity;
import tw.amer.cia.core.model.database.ApiEntity;
import tw.amer.cia.core.model.database.SystemEntity;
import tw.amer.cia.core.model.pojo.service.common.api.*;
import tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement.*;
import tw.amer.cia.core.service.core.ValidateService;
import tw.amer.cia.core.service.host.ApiServiceForHost;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@HostService
public class Web_ManagerApiManageService {
    @Autowired
    ValidateService validateService;
    @Autowired
    HostWebFrontApiService hostWebFrontApiService;
    @Autowired
    ApiServiceForHost apiServiceForHost;

    // 微服務基本操作
    public void createPureApi(Web_CreatePureApiDto inDto) throws DataSourceAccessException, CiaProcessorException {
        // Verify
        SystemEntity system = validateService.validateSystemId(inDto.getSystemId());
        APISetting.API_TYPE apiType = APISetting.API_TYPE.getByName(inDto.getApiType());

        // Adoption of CIA 2.X API
        CreateApiHostDto onCreateDto = new CreateApiHostDto();
        BeanUtils.copyNonNullProperties(inDto, onCreateDto);
        onCreateDto.setSystemName(system.getSystemName());
        onCreateDto.setApiType(apiType.getChineseName());

        apiServiceForHost.createApi(onCreateDto);
    }

    public void deleteApi(String apiId) throws DataSourceAccessException, CiaProcessorException {
        // Verify
        ApiEntity api = validateService.validateApiByApiId(apiId);
        SystemEntity system = validateService.validateSystemId(api.getSystemId());

        // Adoption of CIA 2.X API
        apiServiceForHost.deleteApi(new DeleteApiHostDto(system.getSystemName(), api.getApiName()));
    }

    // 基本資訊
    public Web_ApiBasicInfoOutDto getBasicApiInfoById(String apiId) throws DataSourceAccessException {
        // verify
        validateService.validateApiByApiId(apiId);
        return hostWebFrontApiService.webGetBasicApiInfoById(apiId);
    }

    public void updateBasicApiInfo(Web_ApiBasicInfoUpdateDto dto) throws DataSourceAccessException, CiaProcessorException {
        // verify
        ApiEntity api = validateService.validateApiByApiId(dto.getApiId());
        SystemEntity system = validateService.validateSystemId(dto.getSystemId());
        APISetting.API_TYPE apiType = APISetting.API_TYPE.getByName(dto.getApiType());


        // Adoption of CIA 2.X API
        UpdateApiHostDto onUpdateDto = new UpdateApiHostDto();
        onUpdateDto.setSystemName(system.getSystemName());
        onUpdateDto.setApiName(api.getApiName());
        // Loading Previous Data
        BeanUtils.copyNonNullProperties(api, onUpdateDto);
        // Update Data
        BeanUtils.copyNonNullProperties(dto, onUpdateDto);
        onUpdateDto.setApiType(apiType.getChineseName());
        apiServiceForHost.updateApi(onUpdateDto);
    }

    // 微服務類型、分類、製造階段查詢
    public List<Web_BilingualContentDto> getApiAttributeType() {
        return Arrays.stream(APISetting.API_TYPE.values())
                .map(apiType -> Web_BilingualContentDto.builder()
                        .chinese(apiType.getChineseName())
                        .english(apiType.name()).build())
                .collect(Collectors.toList());
    }

    // 端口資訊
    public List<Web_ApiEndpointDto> getApiEndpointByApiId(String apiId) throws DataSourceAccessException {
        // verify
        ApiEntity api = validateService.validateApiByApiId(apiId);

        return apiServiceForHost.webRetrieveApiEndpointInfo(api.getApiId());
    }

    public void createOrUpdateApiEndpoint(Web_ApiEndpointDto dto) throws DataSourceAccessException, CiaProcessorException {
        boolean isUpdateOperation = StringUtils.isNotBlank(dto.getEndpointId());

        // verify & prepare
        ApiEntity api = validateService.validateApiByApiId(dto.getApiId());
        SystemEntity system = validateService.validateSystemId(api.getSystemId());
        List<ApiEndpointEntity> apiAllEndpoints =
                new ArrayList<>(apiServiceForHost.retrieveApiEndpointList(api.getApiId()));
        if (isUpdateOperation) {
            validateService.validateApiEndpointById(dto.getEndpointId());
        }

        // Giving Update
        if (isUpdateOperation) {
            for (ApiEndpointEntity onUpdateEndpoint : apiAllEndpoints) {
                if (StringUtils.equals(onUpdateEndpoint.getEndpointId(), dto.getEndpointId())) {
                    BeanUtils.copyNonNullProperties(dto, onUpdateEndpoint);
                    onUpdateEndpoint.setApiGwUri(
                            apiServiceForHost.buildApiGwUri(
                                    api.getApiEngName(), onUpdateEndpoint.getApiItfType()
                            ));
                }
            }
        } else {
            // Check duplicate
            for (ApiEndpointEntity cmd : apiAllEndpoints) {
                if (StringUtils.equals(cmd.getApiHostUri(), dto.getApiHostUri()) &&
                        StringUtils.equals(cmd.getApiItfType(), dto.getApiItfType())) {
                    throw DataSourceAccessException.createExceptionForHttp(
                            HttpStatus.BAD_REQUEST,
                            ErrorConstantLib.VALIDATE_API_ENDPOINT_CAN_NOT_REPEAT.getCompleteMessage()
                    );
                }
            }

            ApiEndpointEntity newEndpoint = new ApiEndpointEntity();
            BeanUtils.copyNonNullProperties(dto, newEndpoint);
            newEndpoint.setApiGwUri(
                    apiServiceForHost.buildApiGwUri(
                            api.getApiEngName(), newEndpoint.getApiItfType()
                    ));
            apiAllEndpoints.add(newEndpoint);
        }

        // Adoption of CIA 2.X API
        List<ApiEndpointDto> newEndpointList =
                apiAllEndpoints.stream().map(ApiEndpointDto::new).collect(Collectors.toList());

        UpdateApiHostDto onUpdateDto = new UpdateApiHostDto();
        onUpdateDto.setSystemName(system.getSystemName());
        onUpdateDto.setApiName(api.getApiName());
        onUpdateDto.setEndpoint(newEndpointList);
        apiServiceForHost.updateApi(onUpdateDto);
    }

    public void deleteApiEndpoint(String endpointId) throws DataSourceAccessException, CiaProcessorException {
        // verify & prepare
        ApiEndpointEntity apiEndpoint = validateService.validateApiEndpointById(endpointId);
        ApiEntity api = validateService.validateApiByApiId(apiEndpoint.getApiId());
        SystemEntity system = validateService.validateSystemId(api.getSystemId());
        List<ApiEndpointEntity> apiAllEndpoints =
                new ArrayList<>(apiServiceForHost.retrieveApiEndpointList(api.getApiId()));
        apiAllEndpoints.remove(apiEndpoint);

        // Adoption of CIA 2.X API
        List<ApiEndpointDto> newEndpointList =
                apiAllEndpoints.stream().map(ApiEndpointDto::new).collect(Collectors.toList());

        UpdateApiHostDto onUpdateDto = new UpdateApiHostDto();
        onUpdateDto.setSystemName(system.getSystemName());
        onUpdateDto.setApiName(api.getApiName());
        onUpdateDto.setEndpoint(newEndpointList);
        apiServiceForHost.updateApi(onUpdateDto);
    }

    public List<String> getApiAttributeEndpointInterface() {
        return APISetting.API_INTERFACE;
    }

    // 部署資訊
    public List<Web_ApiDeployedBySiteDto> getApiDeploymentByApiId(String apiId) throws DataSourceAccessException {
        return apiServiceForHost.retrieveApiDeploymentByApiId(apiId);
    }

    public void updateApiDeploymentByApiId(Web_ApiUpdateDeployDto dto) throws DataSourceAccessException, CiaProcessorException {
        // Verify
        ApiEntity api = validateService.validateApiByApiId(dto.getApiId());
        SystemEntity system = validateService.validateSystemId(api.getSystemId());

        //
        UpdateApiHostDto onUpdateDto = new UpdateApiHostDto();
        onUpdateDto.setSystemName(system.getSystemName());
        onUpdateDto.setApiName(api.getApiName());
        onUpdateDto.setDeployment(
                dto.getDeployedBySiteDtoList().stream()
                        .map(Web_ApiDeployedBySiteDto::getFabMap)
                        .map(Map::entrySet)
                        .flatMap(Collection::stream)
                        .filter(entry -> entry.getValue())
                        .map(Map.Entry::getKey)
                        .map(ApiDeployedFabDto::new)
                        .collect(Collectors.toList())
        );
        apiServiceForHost.updateApi(onUpdateDto);
    }
}