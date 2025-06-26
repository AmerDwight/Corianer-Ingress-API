package tw.amer.cia.core.service.core;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.database.compositeId.ApiGwPluginDpyEntityId;
import tw.amer.cia.core.model.database.dao.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service class that provides validation operations for various entities within the application.
 *
 * <p>This service is responsible for performing non-logical validations, which primarily involve checking
 * the existence and state of data rather than performing complex business rules or data manipulations.
 * Examples of such validations include verifying whether a given ID or key exists in the database,
 * whether input parameters are empty or null, and if certain conditions are met for data to be considered
 * valid within the context of the application.</p>
 *
 * <p>The purpose of these validations is to ensure that data passed to the business logic layer is
 * already in an acceptable state, thus preventing unnecessary processing of invalid or non-existent data.
 * By segregating these checks into a dedicated service, the codebase maintains a clear separation of
 * concerns, allowing for cleaner and more maintainable code.</p>
 *
 * <p>Typical use cases for this service include form input validation, API request validation, and
 * preliminary checks before data is sent to more complex domain-specific validation routines.</p>
 *
 * @author AmerWu
 * @version 1.0
 * @since 2024-03-21
 */
@Slf4j
@Service
public class ValidateService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true)
            .configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, false);
    @Autowired
    FabEntityRepo cFabFabEntityRepo;
    @Autowired
    ApiEntityRepo apiEntityRepo;
    @Autowired
    ApiEndpointEntityRepo apiEndpointEntityRepo;
    @Autowired
    ApiDpyEntityRepo apiDpyEntityRepo;
    @Autowired
    SystemEntityRepo systemEntityRepo;
    @Autowired
    SystemDpyEntityRepo systemDpyEntityRepo;
    @Autowired
    RoleUserEntityRepo roleUserEntityRepo;
    @Autowired
    UserEntityRepo userEntityRepo;
    @Autowired
    RoleEntityRepo roleEntityRepo;
    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;
    @Autowired
    GwApikeyEntityRepo gwApikeyEntityRepo;
    @Autowired
    GwApikeyPermissionRepo gwApikeyPermissionRepo;
    @Autowired
    GwPluginEntityRepo gwPluginEntityRepo;
    @Autowired
    ApiGwPluginDpyEntityRepo apiGwPluginDpyEntityRepo;
    @Autowired
    ExternalSystemConfigEntityRepo externalSystemConfigEntityRepo;
    private List<String> FAB_LIST = new ArrayList<>();

    // Map<FAB, SITE>
    private Map<String, String> FAB_SITE_Map = new HashMap<>();

    private void updateFabInfo() {

        List<FabEntity> fabList = cFabFabEntityRepo.findAll();
        if (CollectionUtils.isNotEmpty(fabList)) {
            FAB_LIST = fabList.stream().map(FabEntity::getFabId).collect(Collectors.toList());

            Map<String, String> newFabSiteMap = new HashMap<>();
            fabList.forEach(cFabFab -> {
                newFabSiteMap.put(cFabFab.getFabId(), cFabFab.getSite());
            });
            FAB_SITE_Map = newFabSiteMap;
        }


    }

    public boolean validateFabIdExists(String fabId) throws DataSourceAccessException {
        if (StringUtils.isEmpty(fabId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_FAB_EMPTY_FAB_ID.getCompleteMessage());
        } else {

            if (FAB_LIST.contains(fabId)) {
                return true;
            }
            this.updateFabInfo();
            if (FAB_LIST.contains(fabId)) {
                return true;
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_FAB_INVALID_FAB_ID.getCompleteMessage());
            }
        }
    }

    public boolean validateFabIdsExists(Collection<String> fabIds) throws DataSourceAccessException {
        for (String fabId : new HashSet<String>(fabIds)) {
            validateFabIdExists(fabId);
        }
        return true;
    }

    public String validateFabIdExistsReturnSite(String fabId) throws DataSourceAccessException {
        if (StringUtils.isEmpty(fabId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_FAB_EMPTY_FAB_ID.getCompleteMessage());
        } else {

            if (FAB_SITE_Map.containsKey(fabId)) {
                return FAB_SITE_Map.get(fabId);
            }
            this.updateFabInfo();
            if (FAB_SITE_Map.containsKey(fabId)) {
                return FAB_SITE_Map.get(fabId);
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_FAB_INVALID_FAB_ID.getCompleteMessage());
            }
        }
    }

    public boolean validateSystemNameDuplicate(String uncheckSystemName) throws DataSourceAccessException {
        if (StringUtils.isEmpty(uncheckSystemName)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_SYSTEM_EMPTY_SYSTEM_NAME.getCompleteMessage());
        } else {
            Optional<SystemEntity> inSearchSystem = systemEntityRepo.findBySystemName(uncheckSystemName);
            if (inSearchSystem.isPresent()) {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_SYSTEM_DUPLICATE_SYSTEM_NAME.getCompleteMessage());
            } else {
                return false;
            }
        }
    }

    public RoleEntity validateRoleId(String roleId) throws DataSourceAccessException {
        if (StringUtils.isEmpty(roleId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_ROLE_EMPTY_ROLE_ID.getCompleteMessage());
        } else {
            Optional<RoleEntity> inSearchRole = roleEntityRepo.findByRoleId(roleId);
            if (inSearchRole.isPresent()) {
                return inSearchRole.get();
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_ROLE_INVALID_ROLE_ID.getCompleteMessage());
            }
        }
    }

    public UserEntity validateUserId(String userId) throws DataSourceAccessException {
        if (StringUtils.isEmpty(userId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_USER_EMPTY_USER_ID_OR_ROLE_ID.getCompleteMessage());
        } else {
            Optional<UserEntity> inSearchUser = userEntityRepo.findByUserId(userId);
            if (inSearchUser.isPresent()) {
                return inSearchUser.get();
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_USER_INVALID_USER_ID.getCompleteMessage());
            }
        }
    }

    public List<RoleEntity> validateRoleIdCollection(Collection<String> roleIds) throws DataSourceAccessException {
        if (CollectionUtils.isNotEmpty(roleIds)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_ROLE_EMPTY_ROLE_ID.getCompleteMessage());
        } else {
            List<RoleEntity> inSearchRoleList = roleEntityRepo.findByRoleIdIn(roleIds);
            if (CollectionUtils.isNotEmpty(inSearchRoleList)) {
                return inSearchRoleList;
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_ROLE_INVALID_ROLE_ID.getCompleteMessage());
            }
        }
    }

    public GwApikeyEntity validateApikeyByApikeyId(String apikeyId) throws DataSourceAccessException {
        if (StringUtils.isEmpty(apikeyId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_KEY_EMPTY_KEY_ID.getCompleteMessage());
        } else {
            Optional<GwApikeyEntity> inSearchKey = gwApikeyEntityRepo.findByApikeyId(apikeyId);
            if (inSearchKey.isPresent()) {
                return inSearchKey.get();
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_KEY_INVALID_KEY_ID.getCompleteMessage());
            }
        }
    }

    public GwApikeyEntity validateApikeyByApikeyIdAndRoleId(String apikeyId, String roleId) throws DataSourceAccessException {
        if (StringUtils.isEmpty(apikeyId) || StringUtils.isEmpty(roleId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_KEY_EMPTY_KEY_ID.getCompleteMessage());
        } else {
            Optional<GwApikeyEntity> inSearchKey = gwApikeyEntityRepo.findByApikeyIdAndRoleId(apikeyId, roleId);
            if (inSearchKey.isPresent()) {
                return inSearchKey.get();
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_KEY_INVALID_KEY_ID.getCompleteMessage());
            }
        }
    }

    public boolean validateKeyPermissionByName(String apikeyId, String fabId, String systemName, String apiName) throws DataSourceAccessException {
        Optional<String> inSearchApiId = apiEntityRepo.findApiIdByName(systemName, apiName);
        if (inSearchApiId.isPresent()) {
            return this.validateKeyPermissionByApiId(apikeyId, fabId, inSearchApiId.get());
        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_API_INVALID_API_NAME.getCompleteMessage());
        }
    }

    public boolean validateKeyPermissionByApiId(String apikeyId, String fabId, String apiId) throws DataSourceAccessException {
        Optional<GwApikeyPermissionEntity> inSearchPermission = gwApikeyPermissionRepo.findByFabIdAndApikeyIdAndApiId(fabId, apikeyId, apiId);
        if (inSearchPermission.isPresent()) {
            return true;
        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_API_INVALID_KEY_PERMISSION.getCompleteMessage());
        }
    }

    public SystemEntity validateSystemName(String systemName) throws DataSourceAccessException {
        if (StringUtils.isEmpty(systemName)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_SYSTEM_EMPTY_SYSTEM_NAME.getCompleteMessage());
        } else {
            Optional<SystemEntity> inSearchSystem = systemEntityRepo.findBySystemName(systemName);
            if (inSearchSystem.isPresent()) {
                return inSearchSystem.get();
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_SYSTEM_NOT_FOUND_BY_SYSTEM_NAME.getCompleteMessage());
            }
        }
    }

    public ApiEntity validateApiByName(String systemName, String uncheckApiName) throws DataSourceAccessException {
        if (StringUtils.isEmpty(systemName) || StringUtils.isEmpty(uncheckApiName)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_API_EMPTY_NAME.getCompleteMessage());
        } else {
            Optional<ApiEntity> inSearchApi = apiEntityRepo.findBySystemNameAndApiName(systemName, uncheckApiName);
            if (inSearchApi.isPresent()) {
                return inSearchApi.get();
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.NOT_FOUND,
                        ErrorConstantLib.VALIDATE_API_API_NOT_FOUND.getCompleteMessage()
                                + "\n " + systemName + " " + uncheckApiName);
            }
        }
    }

    public ApiEntity validateApiByApiId(String apiId) throws DataSourceAccessException {
        if (StringUtils.isEmpty(apiId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_API_EMPTY_ID.getCompleteMessage());
        } else {
            Optional<ApiEntity> inSearchApi = apiEntityRepo.findByApiId(apiId);
            if (inSearchApi.isPresent()) {
                return inSearchApi.get();
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.NOT_FOUND,
                        ErrorConstantLib.VALIDATE_API_API_NOT_FOUND.getCompleteMessage()
                                + "\n Unsearchable ApiId = " + apiId);
            }
        }
    }

    public ApiEndpointEntity validateApiEndpointById(String id) throws DataSourceAccessException {
        if (StringUtils.isEmpty(id)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_API_ENDPOINT_EMPTY_ID.getCompleteMessage());
        } else {
            Optional<ApiEndpointEntity> inSearchApiEndpoint = apiEndpointEntityRepo.findByEndpointId(id);
            if (inSearchApiEndpoint.isPresent()) {
                return inSearchApiEndpoint.get();
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.NOT_FOUND,
                        ErrorConstantLib.VALIDATE_API_ENDPOINT_NOT_FOUND.getCompleteMessage()
                                + "\n Unsearchable ApiEndpointId = " + id);
            }
        }
    }

    public List<ApiEntity> validateApiByApiIdCollection(Collection<String> apiIdList) throws DataSourceAccessException {
        if (CollectionUtils.isNotEmpty(apiIdList)) {
            List<ApiEntity> insearchmApiList = apiEntityRepo.findAllById(apiIdList);
            Set<String> realApiIdSet = insearchmApiList.stream().map(ApiEntity::getApiId).collect(Collectors.toSet());
            for (String apiId : apiIdList) {
                if (!realApiIdSet.contains(apiId)) {
                    log.error("ApiId: {} is not exists.", apiId);
                    throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                            ErrorConstantLib.VALIDATE_API_API_NOT_FOUND.getCompleteMessage());
                }
            }
            if (realApiIdSet.size() != (apiIdList.stream().collect(Collectors.toSet())).size()) {
                log.error("Validate api by apiIdList found data size is not match.");
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_API_API_NOT_FOUND.getCompleteMessage());
            } else {
                return insearchmApiList;
            }

        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_API_BY_API_ID_LIST_EMPTY_INPUT.getCompleteMessage());
        }
    }

    public List<SystemEntity> validateSystemSystemIdList(List<String> systemIdList) throws DataSourceAccessException {
        if (CollectionUtils.isNotEmpty(systemIdList)) {
            List<SystemEntity> insearchmSystemList = systemEntityRepo.findAllById(systemIdList);
            Set<String> realSystemIdSet = insearchmSystemList.stream().map(SystemEntity::getSystemId).collect(Collectors.toSet());
            for (String systemId : systemIdList) {
                if (!realSystemIdSet.contains(systemId)) {
                    log.error("SystemId: {} is not exists.", systemId);
                    throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                            ErrorConstantLib.VALIDATE_SYSTEM_NOT_FOUND_BY_SYSTEM_ID.getCompleteMessage());
                }
            }
            if (realSystemIdSet.size() != (new HashSet<>(systemIdList)).size()) {
                log.error("Validate api by apiIdList found data size is not match.");
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_SYSTEM_SYSTEM_NOT_FOUND.getCompleteMessage());
            } else {
                return insearchmSystemList;
            }

        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_SYSTEM_BY_SYSTEM_ID_LIST_EMPTY_INPUT.getCompleteMessage());
        }
    }

    public boolean validateApiNameDuplicate(String systemName, String uncheckApiName) throws DataSourceAccessException {
        if (StringUtils.isEmpty(systemName) || StringUtils.isEmpty(uncheckApiName)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_API_EMPTY_NAME.getCompleteMessage());
        } else {
            Optional<ApiEntity> inSearchApi = apiEntityRepo.findBySystemNameAndApiName(systemName, uncheckApiName);
            if (inSearchApi.isPresent()) {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_API_DUPLICATE_API_NAME.getCompleteMessage());
            } else {
                return false;
            }
        }
    }

    public boolean validateApikeyNameDuplicate(String roleId, String keyName) throws DataSourceAccessException {
        if (StringUtils.isEmpty(keyName)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_KEY_EMPTY_SYSTEM_NAME.getCompleteMessage());
        } else {
            Optional<GwApikeyEntity> inSearchApikey = gwApikeyEntityRepo.findByRoleIdAndKeyName(roleId, keyName);
            if (inSearchApikey.isPresent()) {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_KEY_DUPLICATE_APIKEY_NAME.getCompleteMessage());
            } else {
                return false;
            }
        }
    }

    public boolean validateSystemDeployBySystemNameAndFabList(String systemName, List<String> fabIdList) throws DataSourceAccessException {
        List<SystemDpyEntity> inSearchDeploymentList = systemDpyEntityRepo.findBySystemIdAndFabIds(systemName, fabIdList);
        if (inSearchDeploymentList.size() == fabIdList.size()) {
            return true;
        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_SYSTEM_SYSTEM_DEPLOYMENT_NOT_FOUND.getCompleteMessage());
        }
    }

    public boolean validateSystemHasApi(String systemId) {
        List<ApiEntity> inSearchApiList = apiEntityRepo.findBySystemId(systemId);
        return CollectionUtils.isNotEmpty(inSearchApiList);
    }

    public boolean validateSystemHasApiDeployInFab(String systemId, String fabId) {
        boolean apiDeployExists = apiDpyEntityRepo.existsBySystemIdAndFabId(systemId, fabId);
        return BooleanUtils.isTrue(apiDeployExists);
    }

    public boolean validateUserIdMatchedRoleId(String userId, String roleId) throws DataSourceAccessException {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(roleId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_USER_EMPTY_USER_ID_OR_ROLE_ID.getCompleteMessage());
        } else {
            Optional<RoleUserEntity> onSearchData = roleUserEntityRepo.findByUserIdAndRoleId(userId, roleId);
            if (onSearchData.isPresent()) {
                return true;
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_USER_USER_ID_OR_ROLE_ID_NOT_MATCH.getCompleteMessage());
            }
        }
    }

    public boolean validateRoleIdOrNameDuplicate(String roleId, String roleName) throws DataSourceAccessException {
        if (StringUtils.isEmpty(roleId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_ROLE_EMPTY_ROLE_NAME.getCompleteMessage());
        } else {
            Optional<RoleEntity> inSearchRole = roleEntityRepo.findFirstByRoleIdOrRoleName(roleId, roleName);
            if (inSearchRole.isPresent()) {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_ROLE_DUPLICATE_ROLE_ID_OR_NAME.getCompleteMessage());
            } else {
                return false;
            }
        }
    }

    public boolean validateRoleAuthorities(String roleId, String fabId, List<String> permissionApiIdList) throws DataSourceAccessException {
        if (StringUtils.isEmpty(roleId) || StringUtils.isEmpty(fabId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_ROLE_AUTHORITY_EMPTY_INPUT.getCompleteMessage());
        }
        if (CollectionUtils.isNotEmpty(permissionApiIdList)) {
            Set<String> permissionApiIdSet = new HashSet<>(permissionApiIdList);
            List<RoleAuthorityEntity> checkedAuthorities = roleAuthorityEntityRepo.findByRoleIdAndFabIdAndApiIdIn(roleId, fabId, permissionApiIdSet);
            if (CollectionUtils.isEmpty(checkedAuthorities)
                    || checkedAuthorities.size() != permissionApiIdSet.size()) {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_ROLE_AUTHORITY_CONTAINS_INVALID_AUTHORITY.getCompleteMessage());
            }
        }
        return true;
    }

    public boolean illegalRoleAuthoritiesByApiId(String roleId, String fabId, List<String> targetList) {
        List<String> roleAuthorityList = roleAuthorityEntityRepo.findApiIdByRoleIdAndFabId(roleId, fabId);

        boolean noAuthorities = false;
        for (String targetApiId : targetList) {
            if (!roleAuthorityList.contains(targetApiId)) {
                noAuthorities = true;
                break;
            }
        }
        return noAuthorities;
    }

    public SystemEntity validateSystemId(String systemId) throws DataSourceAccessException {
        if (StringUtils.isEmpty(systemId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_SYSTEM_EMPTY_SYSTEM_ID.getCompleteMessage());
        } else {
            Optional<SystemEntity> inSearchSystem = systemEntityRepo.findBySystemId(systemId);
            if (inSearchSystem.isPresent()) {
                return inSearchSystem.get();
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_SYSTEM_NOT_FOUND_BY_SYSTEM_ID.getCompleteMessage());
            }
        }
    }

    public GwPluginEntity validateGwPluginId(String gwPluginId) throws DataSourceAccessException {
        if (StringUtils.isEmpty(gwPluginId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_GW_PLUGIN_EMPTY_GW_PLUGIN_ID.getCompleteMessage());
        } else {
            Optional<GwPluginEntity> inSearchGwPlugin = gwPluginEntityRepo.findById(gwPluginId);
            if (inSearchGwPlugin.isPresent()) {
                return inSearchGwPlugin.get();
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_GW_PLUGIN_NOT_FOUND_BY_GW_PLUGIN_ID.getCompleteMessage());
            }
        }
    }

    public ApiGwPluginDpyEntity validateApiGwPluginDpy(String apiId, String gwPluginId, String fabId) throws DataSourceAccessException {
        if (StringUtils.isBlank(apiId) || StringUtils.isBlank(gwPluginId) || StringUtils.isBlank(fabId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getCompleteMessage());
        } else {
            Optional<ApiGwPluginDpyEntity> inSearchGwPlugin = apiGwPluginDpyEntityRepo.findById(
                    ApiGwPluginDpyEntityId.builder()
                            .apiId(apiId)
                            .gwPluginId(gwPluginId)
                            .fabId(fabId).build());
            if (inSearchGwPlugin.isPresent()) {
                return inSearchGwPlugin.get();
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_API_GW_PLUGIN_NOT_FOUND.getCompleteMessage());
            }
        }
    }

    public boolean validateIpStringAcceptIPv4AndSubNetLength(String onCheckIpString) throws DataSourceAccessException {
        if (StringUtils.isNotBlank(onCheckIpString)) {
            String IP_PATTERN =
                    "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\/([0-9]|[1-2][0-9]|3[0-2]))?$";
            Pattern pattern = Pattern.compile(IP_PATTERN);
            Matcher matcher = pattern.matcher(onCheckIpString);
            if (matcher.matches()) {
                return true;
            }
        }
        throw DataSourceAccessException.createExceptionForHttp(
                HttpStatus.BAD_REQUEST,
                ErrorConstantLib.VALIDATE_IP_V4_STRING_FAILED.getCompleteMessage()
        );
    }

    /**
     * 檢查給定的端口範圍是否有效。
     *
     * @param onCheckPort 待確認端口
     * @return 如果有效端口則返回 true，否則返回 false。
     */
    public static boolean validatePortValid(int onCheckPort) {
        if (onCheckPort < 0 || onCheckPort > 65535) {
            return false;
        }
        return true;
    }

    public boolean validateExtEntityIdAndKey(String extEntityId, String extEntityKey) throws DataSourceAccessException {
        if (StringUtils.isEmpty(extEntityId) || StringUtils.isEmpty(extEntityKey)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.VALIDATE_EXTERNAL_ENTITY_EMPTY_ID_OR_KEY.getCompleteMessage());
        } else {
            Optional<ExternalSystemConfigEntity> onSearchEntity = externalSystemConfigEntityRepo.findById(extEntityId);
            if (onSearchEntity.isPresent()) {
                if (StringUtils.equals(onSearchEntity.get().getExtSystemKey(), extEntityKey)) {
                    return true;
                } else {
                    throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                            ErrorConstantLib.VALIDATE_EXTERNAL_ENTITY_WRONG_KEY.getCompleteMessage());
                }
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.VALIDATE_EXTERNAL_ENTITY_EMPTY_ID_NOT_FOUND.getCompleteMessage());
            }
        }
    }

    public boolean validateIsNotSandBoxFab(String onCheckFab) {
        return !StringUtils.equalsIgnoreCase(onCheckFab, GeneralSetting.SANDBOX_FAB.getFabId());
    }

    public boolean validateIsNotVirtualSite(String onCheckSiteName) {
        return !StringUtils.equalsIgnoreCase(onCheckSiteName, GeneralSetting.SANDBOX_FAB.getSite());
    }

    /**
     * 驗證並解析JSON文件為鍵值對映射
     *
     * @param file 上傳的文件，必須是JSON格式
     * @return Map<String, String> 包含JSON中所有鍵值對的映射
     * @throws DataSourceAccessException 當文件格式不正確或包含複雜結構時拋出
     * @throws IOException               當文件讀取失敗時拋出
     */
    public Map<String, String> validateJsonFileAsKeyValueMap(MultipartFile file)
            throws DataSourceAccessException, IOException {

        // 驗證文件是否為空
        if (file == null || file.isEmpty()) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getCompleteMessage() + "FILE."
            );
        }

        // 驗證文件擴展名
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename) ||
                !originalFilename.toLowerCase().endsWith(".json")) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getCompleteMessage() + "文件必須是JSON格式(.json)"
            );
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        try {
            // 解析JSON文件
            jsonNode = objectMapper.readTree(file.getInputStream());
        } catch (IOException e) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getCompleteMessage() + "無效的JSON文件格式: "
            );
        }

        // 驗證JSON是否為對象類型（不是數組或基本類型）
        if (!jsonNode.isObject()) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getCompleteMessage() + "JSON文件必須是對象格式，不能是數組或基本類型"
            );
        }

        Map<String, String> resultMap = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();

        // 遍歷JSON對象的所有字段
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            JsonNode valueNode = field.getValue();

            // 驗證值是否為簡單類型（字符串、数字、布爾值、null）
            if (valueNode.isObject() || valueNode.isArray()) {
                throw DataSourceAccessException.createExceptionForHttp(
                        HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getCompleteMessage() +
                                String.format("JSON文件包含複雜結構，鍵 '%s' 的值不是Key-Value類型", key)
                );
            }

            // 將值轉換為字符串
            String value;
            if (valueNode.isNull()) {
                value = null;
            } else if (valueNode.isTextual()) {
                value = valueNode.asText();
            } else {
                // 對於数字和布爾值，轉換為字符串
                value = valueNode.asText();
            }

            resultMap.put(key, value);
        }

        return resultMap;
    }

    /**
     * 驗證MultipartFile是否為有效的JSON文件
     *
     * @param file MultipartFile對象
     * @return void
     */
    public static void validateJsonFile(MultipartFile file) throws DataSourceAccessException {
        if (file == null || file.isEmpty()) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getMessage() + " " +
                            "文件為空"
            );
        }

        try {
            byte[] fileBytes = file.getBytes();
            String jsonContent = new String(fileBytes, StandardCharsets.UTF_8).trim();

            // 檢查內容是否為空
            if (jsonContent.isEmpty()) {
                throw DataSourceAccessException.createExceptionForHttp(
                        HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getMessage() + " " +
                                "文件內容為空"
                );
            }

            // 使用JsonParser進行嚴格驗證
            try (JsonParser parser = OBJECT_MAPPER.getFactory().createParser(jsonContent)) {
                // 解析整個JSON
                OBJECT_MAPPER.readTree(parser);

                // 檢查是否還有剩餘的token
                if (parser.nextToken() != null) {
                    throw DataSourceAccessException.createExceptionForHttp(
                            HttpStatus.BAD_REQUEST,
                            ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getMessage() + " " +
                                    "JSON後存在額外字符"
                    );
                }
            }

        } catch (Exception e) {
            log.warn("JSON文件驗證失敗: {}", e.getMessage());
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getMessage() + " " +
                            "無效的JSON格式: " + e.getMessage()
            );
        }
    }
}

