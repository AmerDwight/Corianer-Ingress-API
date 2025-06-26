package tw.amer.cia.core.controller.host.web;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.component.structural.annotation.RequireUserVerifyApi;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement.Web_ApikeyCreateInDto;
import tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement.Web_ApikeyDuplicateInDto;
import tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement.Web_ApikeyModifyExistsKeyScopeInDto;
import tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement.Web_ApikeyUpdateInDto;
import tw.amer.cia.core.service.host.web.HostWebFrontApiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@HostRestController
@RequireUserVerifyApi
@RequestMapping("/web/apikeyManagement")
public class ApikeyManagementController {
    @Autowired
    HostWebFrontApiService hostWebFrontApiService;

    // Query
    @GetMapping("/apikey")
    public Object webApikeyManagementFindApikeyByRoleIdOrderByKeyNameAsc(@RequestParam(defaultValue = "0") int pageNumber,
                                                                         @RequestParam(defaultValue = "10") int pageSize,
                                                                         @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                                         @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) {
        return hostWebFrontApiService.findVisibleApikeyByRoleIdOrderByKeyNameAsc(roleId, pageNumber, pageSize);
    }

    @GetMapping("/apikey/action/scope/{apikeyId}")
    public Object webApikeyManagementFindApikeyScopeByApikeyId(@PathVariable String apikeyId,
                                                               @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                               @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) throws DataSourceAccessException {
        return hostWebFrontApiService.findApikeyEffectedScopeByRoleIdAndApikeyId(roleId, apikeyId);
    }

    @GetMapping("/role/authority/scope")
    public Object webApikeyManagementFindRoleAuthorityScopeByRoleId(@RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                                    @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) throws DataSourceAccessException {
        return hostWebFrontApiService.findRoleAuthorityScopeByRoleId(roleId);
    }

    @GetMapping("/role/authority/system")
    public Object webApikeyManagementFindRoleAuthoritySystemByRoleId(@RequestParam List<String> scopes,
                                                                     @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                                     @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) {
        return hostWebFrontApiService.findRoleAuthoritySystemByRoleIdAndFabIdList(roleId, scopes);

    }

    @GetMapping("/role/authority/api")
    public Object webApikeyManagementFindRoleAuthorityApiByRoleIdAndSystemId(@RequestParam String systemId,
                                                                             @RequestParam List<String> scopes,
                                                                             @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                                             @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) throws DataSourceAccessException {
        return hostWebFrontApiService.findRoleAuthorityApiByRoleIdAndSystemIdAndFabIdList(roleId, systemId, scopes);
    }

    @PutMapping("/apikey/action/scope/{apikeyId}")
    public Object webApikeyManagementUpdateApikeyScope(@PathVariable String apikeyId,
                                                       @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                       @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId,
                                                       @Valid @RequestBody Web_ApikeyModifyExistsKeyScopeInDto inDto) throws DataSourceAccessException, CiaProcessorException {
        return hostWebFrontApiService.updateApikeyPermissionByScope(
                roleId, apikeyId, inDto.getEnableScopeList(), inDto.getDisableScopeList());
    }

    @PutMapping("/apikey/action/change/active/{apikeyId}")
    public Object webApikeyManagementUpdateApikeyActiveStatus(@PathVariable String apikeyId,
                                                              @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                              @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) throws DataSourceAccessException, CiaProcessorException {
        return hostWebFrontApiService.updateApikeyActiveStatus(apikeyId);
    }

    @PostMapping("/apikey/action/duplicate")
    public Object webApikeyManagementDuplicateApikey(@RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                     @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId,
                                                     @Valid @RequestBody Web_ApikeyDuplicateInDto inDto) throws CiaProcessorException, DataSourceAccessException {
        return hostWebFrontApiService.duplicateApikeyWithKeyName(roleId, inDto.getSourceApikeyId(), inDto.getNewApikeyName());
    }

    @DeleteMapping("/apikey/action/delete/{apikeyId}")
    public Object webApikeyManagementDeleteApikey(@PathVariable String apikeyId,
                                                  @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                  @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) throws CiaProcessorException, DataSourceAccessException {
        return hostWebFrontApiService.deleteApikey(roleId, apikeyId);
    }

    @PostMapping("/apikey/action/create")
    public Object webApikeyManagementCreateApikey(@RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                  @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId,
                                                  @Valid @RequestBody Web_ApikeyCreateInDto inDto) throws CiaProcessorException, DataSourceAccessException {
        return hostWebFrontApiService.createApikey(roleId, inDto.getNewApikeyName(), inDto.getNewApikeyDesc(), inDto.getFabIdApiListMap());
    }

    @GetMapping("/apikey/action/function/{apikeyId}")
    public Object webApikeyManagementFindApikeyFunction(@PathVariable String apikeyId,
                                                        @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                        @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) throws DataSourceAccessException {
        return hostWebFrontApiService.findApikeyFunction(apikeyId);
    }

    @GetMapping("/api/info/gateway/endpoint/{apiId}")
    public Object webApikeyManagementFindApiGatewayEndpoint(@PathVariable String apiId,
                                                            @RequestParam List<String> scopes,
                                                            @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                            @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) throws

    DataSourceAccessException {
        return hostWebFrontApiService.findApiGwEndpointListByApiIdAndScopes(apiId, scopes);
    }

    @PutMapping("/apikey/action/function/{apikeyId}")
    public Object webApikeyManagementUpdateApikeyFunction(@PathVariable String apikeyId,
                                                          @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                          @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId,
                                                          @Valid @RequestBody Web_ApikeyUpdateInDto inDto) throws CiaProcessorException, DataSourceAccessException {
        boolean isDataMismatch = !StringUtils.equals(apikeyId, inDto.getApikeyId());
        if (isDataMismatch) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST, ErrorConstantLib.API_APIKEY_INCORRECT_INPUT_MISMATCHED_INPUT_BETWEEN_PATH_AND_BODY.getCompleteMessage());
        }
        return hostWebFrontApiService.updateApikeyFunction(roleId, inDto.getApikeyId(), inDto.getFabIdApiListMap());
    }

}
