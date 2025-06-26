package tw.amer.cia.core.controller.host.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.amer.cia.core.common.RoleSetting;
import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.component.structural.annotation.RequireAdminUserVerifyApi;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement.*;
import tw.amer.cia.core.service.host.web.Web_ManagerRoleManageService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequireAdminUserVerifyApi
@HostRestController
@RequestMapping("/web/manager/roleManagement")
public class ManagerRoleManageController {

    @Autowired
    Web_ManagerRoleManageService roleManageService;

    @GetMapping("/role")
    public Object findRoleManagePanelOrderByRoleIdDesc(@RequestParam(defaultValue = "0") int pageNumber,
                                                       @RequestParam(defaultValue = "25") int pageSize,
                                                       @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) {
        return roleManageService.findRoleManagePanelOrderByRoleIdDesc(pageNumber, pageSize);
    }

    @GetMapping("/role/authority/system")
    public Object modifyAuthFindSystemOrderBySystemName(@RequestParam(defaultValue = "0") int pageNumber,
                                                        @RequestParam(defaultValue = "25") int pageSize,
                                                        @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) {
        return roleManageService.findRoleManagementModifyAuthSystemOrderBySystemName(pageNumber, pageSize);
    }

    @GetMapping("/role/authority/api/{onSearchRoleId}/{onSearchSystemId}")
    public Object modifyAuthFindMsWithAuthorityOrderByApiName(@RequestParam(defaultValue = "0") int pageNumber,
                                                              @RequestParam(defaultValue = "50") int pageSize,
                                                              @PathVariable String onSearchRoleId,
                                                              @PathVariable String onSearchSystemId,
                                                              @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) {
        return roleManageService.modifyAuthFindApiWithAuthorityOrderByApiName(onSearchRoleId, onSearchSystemId, pageNumber, pageSize);
    }

    @PutMapping("/role/authority/api/{apiId}")
    public Object modifyRoleAuth(@PathVariable String apiId,
                                 @RequestBody Web_RoleManagementModifyAuthInDto inDto,
                                 @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) throws DataSourceAccessException, CiaProcessorException {
        if (!StringUtils.equalsIgnoreCase(apiId, inDto.getApiId())) {
            return new ResponseEntity("MS_ID miss matched.", HttpStatus.BAD_REQUEST);
        }
        roleManageService.modifyRoleAuth(inDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/role/member/{onSearchRoleId}")
    public Object findRoleMemberPanel(@PathVariable String onSearchRoleId,
                                      @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) throws DataSourceAccessException {
        return roleManageService.findRoleMemberPanel(onSearchRoleId);
    }

    @GetMapping("/user/role/{onSearchUserId}")
    public Object findRoleListByUserId(@PathVariable String onSearchUserId,
                                       @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) throws DataSourceAccessException {
        return roleManageService.findRoleListByUserId(onSearchUserId);
    }

    @PutMapping("role/member/ui-visibility/{onUpdateRoleId}/{onUpdateUserId}")
    public void changeUiVisibilityOfMember(@PathVariable String onUpdateRoleId,
                                           @PathVariable String onUpdateUserId,
                                           @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) throws DataSourceAccessException {
        roleManageService.changeUiVisibilityOfMember(onUpdateRoleId, onUpdateUserId);
    }

    @DeleteMapping("role/member/{onUpdateRoleId}/{onDeleteUserId}")
    public void deleteRoleMember(@PathVariable String onUpdateRoleId,
                                 @PathVariable String onDeleteUserId,
                                 @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) throws DataSourceAccessException {
        roleManageService.deleteRoleMember(onUpdateRoleId, onDeleteUserId);
    }

    // CIA: 4.3.0
    @GetMapping("/role/type/creatable-list")
    public List<Web_RoleManagementCreatableRoleTypeDto> getCreatableRoleType(
            @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) {
        return RoleSetting.ROLE_TYPE.getModifiableRoleType()
                .stream()
                .map(roleType -> new Web_RoleManagementCreatableRoleTypeDto(roleType.name()))
                .collect(Collectors.toList());
    }

    // CIA 4.3.0
    @PostMapping("/role")
    public void createRole(
            @Valid @RequestBody @NotNull Web_RoleManagementCreateRoleInDto inDto,
            @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) throws DataSourceAccessException, CiaProcessorException {
        roleManageService.createRole(inDto);
    }

    // CIA 4.3.0
    @GetMapping("/role/no-authority/api/{onSearchSystemId}")
    public Object findCreateRoleNoAuthMsListBySystemIdOrderByApiName(@RequestParam(defaultValue = "0") int pageNumber,
                                                                     @RequestParam(defaultValue = "50") int pageSize,
                                                                     @PathVariable String onSearchSystemId,
                                                                     @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) {
        return roleManageService.findCreateRoleNoAuthApiListBySystemIdOrderByApiName(onSearchSystemId, pageNumber, pageSize);
    }

    // CIA 4.3.0
    @GetMapping("/role/name/{roleName}")
    public Object findRoleNameByFuzzySearchOrderByRoleName(@RequestParam(defaultValue = "0") int pageNumber,
                                                           @RequestParam(defaultValue = "50") int pageSize,
                                                           @PathVariable String roleName,
                                                           @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) {
        return roleManageService.findRoleNameLikeOrderByRoleName(pageNumber, pageSize, roleName);
    }

    // CIA 4.3.0
    @GetMapping("/role/member/dept/search/{deptCode}")
    public Page<String> findDeptCodeLike(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @PathVariable String deptCode,
            @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) {
        return roleManageService.findDeptCodeLike(pageNumber, pageSize, deptCode);
    }

    // CIA 4.3.0
    @GetMapping("/role/member/dept/member/{deptCode}")
    public Page<Web_RoleManagementMemberSimpleDto> findMembersByDeptCode(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "50") int pageSize,
            @PathVariable String deptCode,
            @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) {
        return roleManageService.findMembersByDeptCode(pageNumber, pageSize, deptCode);
    }

    // CIA 4.3.0
    @PutMapping("/role/member")
    public void modifyRoleMember(@Valid @RequestBody Web_RoleManagementModifyRoleMemberInDto inDto,
                                 @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) throws DataSourceAccessException {
        roleManageService.modifyRoleMember(inDto);
    }
}
