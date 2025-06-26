package tw.amer.cia.core.controller.host.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.component.structural.annotation.RequireAdminUserVerifyApi;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement.Web_ApiBasicInfoUpdateDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement.Web_ApiEndpointDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement.Web_ApiUpdateDeployDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement.Web_CreatePureApiDto;
import tw.amer.cia.core.service.core.ValidateService;
import tw.amer.cia.core.service.host.web.Web_ManagerApiManageService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@HostRestController
@RequireAdminUserVerifyApi
@CrossOrigin
@RequestMapping("/web/manager/apiManagement")
public class ManagerApiManageController {

    @Autowired
    ValidateService validateService;
    @Autowired
    Web_ManagerApiManageService apiManageService;

    private final String ACCEPT_GW_CONFIG_FILE_TYPE = "json";

    // 微服務基本操作
    @PostMapping("/api")
    public void createPureApi(@Valid @RequestBody Web_CreatePureApiDto inDto) throws DataSourceAccessException, CiaProcessorException {
        apiManageService.createPureApi(inDto);
    }

    @DeleteMapping("/api/{apiId}")
    public void deleteApi(@PathVariable String apiId) throws DataSourceAccessException, CiaProcessorException {
        apiManageService.deleteApi(apiId);
    }

    // 基本資訊
    @GetMapping("/api/basic/{apiId}")
    public Object getBasicApiInfoById(@PathVariable String apiId) throws DataSourceAccessException, CiaProcessorException {
        return apiManageService.getBasicApiInfoById(apiId);
    }

    @PutMapping("/api/basic")
    public void updateBasicApiInfo(@Valid @RequestBody Web_ApiBasicInfoUpdateDto dto) throws DataSourceAccessException, CiaProcessorException {
        apiManageService.updateBasicApiInfo(dto);
    }

    // 微服務類型、分類、製造階段查詢
    @GetMapping("/api/attribute/type")
    public Object getApiAttributeType() {
        return apiManageService.getApiAttributeType();
    }

    // 端口資訊
    @GetMapping("/api/endpoint/{apiId}")
    public Object getApiEndpointByApiId(@PathVariable("apiId") String apiId) throws DataSourceAccessException {
        return apiManageService.getApiEndpointByApiId(apiId);
    }

    @PatchMapping("/api/endpoint")
    public void createOrUpdateApiEndpoint(@Valid @RequestBody Web_ApiEndpointDto dto) throws DataSourceAccessException, CiaProcessorException {
        apiManageService.createOrUpdateApiEndpoint(dto);
    }

    @DeleteMapping("/api/endpoint/{endpointId}")
    public void deleteApiEndpoint(@PathVariable("endpointId") String endpointId) throws DataSourceAccessException, CiaProcessorException {
        apiManageService.deleteApiEndpoint(endpointId);
    }

    @GetMapping("/api/attribute/endpoint/interface")
    public Object getApiAttributeEndpointInterface() {
        return apiManageService.getApiAttributeEndpointInterface();
    }

    // 部署資訊
    @GetMapping("/api/deployment/{apiId}")
    public Object getApiDeploymentByApiId(@PathVariable("apiId") String apiId) throws DataSourceAccessException {
        return apiManageService.getApiDeploymentByApiId(apiId);
    }

    @PutMapping("/api/deployment")
    public void updateApiDeploymentByApiId(@Valid @RequestBody Web_ApiUpdateDeployDto dto) throws DataSourceAccessException, CiaProcessorException {
        apiManageService.updateApiDeploymentByApiId(dto);
    }
    private HttpHeaders getFileRequestHeaderTemplate(@Nullable String fileName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (StringUtils.isNotBlank(fileName)) {
            httpHeaders.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(fileName)
                    .build());
        }
        List<String> allowedHeaders = new ArrayList<>();
        allowedHeaders.add("Content-Disposition");
        httpHeaders.set("Access-Control-Expose-Headers","Content-Disposition");
        return httpHeaders;
    }
}
