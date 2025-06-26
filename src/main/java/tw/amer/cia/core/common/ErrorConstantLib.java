package tw.amer.cia.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;

public final class ErrorConstantLib
{

    // Validation Service Error Start with 1000000
    public static final ErrorCode VALIDATE_FAB_EMPTY_FAB_ID = new ErrorCode("1000001", "FAB Code is empty or null.");
    public static final ErrorCode VALIDATE_FAB_INVALID_FAB_ID = new ErrorCode("1000002", "FAB Code does not exist.");
    public static final ErrorCode VALIDATE_ROLE_EMPTY_ROLE_ID = new ErrorCode("1000003", "Role Id is empty or null.");
    public static final ErrorCode VALIDATE_ROLE_INVALID_ROLE_ID = new ErrorCode("1000004", "Role Id does not exist.");
    public static final ErrorCode VALIDATE_KEY_EMPTY_KEY_ID = new ErrorCode("1000005", "APIKey is empty or null.");
    public static final ErrorCode VALIDATE_KEY_INVALID_KEY_ID = new ErrorCode("1000006", "APIKey does not exist.");
    public static final ErrorCode VALIDATE_API_INVALID_API_NAME = new ErrorCode("1000007", "Target Api does not exist.");
    public static final ErrorCode VALIDATE_API_INVALID_KEY_PERMISSION = new ErrorCode("1000008", "The Key does not have permission of target api.");
    public static final ErrorCode VALIDATE_SYSTEM_EMPTY_SYSTEM_NAME = new ErrorCode("1000009", "SystemEntity name is empty or null.");
    public static final ErrorCode VALIDATE_SYSTEM_DUPLICATE_SYSTEM_NAME = new ErrorCode("1000010", "SystemEntity name is already exists.");
    public static final ErrorCode VALIDATE_API_EMPTY_NAME = new ErrorCode("1000011", "SystemEntity name or Api name is empty or null.");
    public static final ErrorCode VALIDATE_API_DUPLICATE_API_NAME = new ErrorCode("1000012", "Api is already exists in this system.");
    public static final ErrorCode VALIDATE_API_API_NOT_FOUND = new ErrorCode("1000013", "Can not find the reference Api, please check the input.");
    public static final ErrorCode VALIDATE_KEY_EMPTY_SYSTEM_NAME = new ErrorCode("1000014", "Apikey name is empty or null.");
    public static final ErrorCode VALIDATE_KEY_DUPLICATE_APIKEY_NAME = new ErrorCode("1000015", "Apikey Name is already exists under this role user.");
    public static final ErrorCode VALIDATE_SYSTEM_NOT_FOUND_BY_SYSTEM_NAME = new ErrorCode("1000016", "SystemEntity name does not exist.");
    public static final ErrorCode VALIDATE_SYSTEM_SYSTEM_DEPLOYMENT_NOT_FOUND = new ErrorCode("1000017", "One or more Fab doesn't have specific system deployment, please check input data.");
    public static final ErrorCode VALIDATE_SYSTEM_DELETE_SYSTEM_HAS_API = new ErrorCode("1000018", "On delete SystemEntity still has api.");
    public static final ErrorCode VALIDATE_ROLE_EMPTY_ROLE_NAME = new ErrorCode("1000019", "Role ID is empty or null.");
    public static final ErrorCode VALIDATE_ROLE_DUPLICATE_ROLE_ID_OR_NAME = new ErrorCode("1000020", "Role Id or Name is already exists.");
    public static final ErrorCode VALIDATE_ROLE_AUTHORITY_EMPTY_INPUT = new ErrorCode("1000021", "Role ID or Fab Code is empty or null.");
    public static final ErrorCode VALIDATE_ROLE_AUTHORITY_CONTAINS_INVALID_AUTHORITY = new ErrorCode("1000022", "The Role doesn't have equivalent Authority.");
    public static final ErrorCode VALIDATE_SYSTEM_EMPTY_SYSTEM_ID = new ErrorCode("1000023", "SystemEntity id is empty or null.");
    public static final ErrorCode VALIDATE_SYSTEM_NOT_FOUND_BY_SYSTEM_ID = new ErrorCode("1000024", "SystemEntity id does not exist.");
    public static final ErrorCode VALIDATE_GW_PLUGIN_EMPTY_GW_PLUGIN_ID = new ErrorCode("1000025", "Gateway Plugin id is empty or null.");
    public static final ErrorCode VALIDATE_GW_PLUGIN_NOT_FOUND_BY_GW_PLUGIN_ID = new ErrorCode("1000026", "Gateway Plugin id does not exist.");
    public static final ErrorCode VALIDATE_API_BY_API_ID_LIST_EMPTY_INPUT = new ErrorCode("1000027", "Input api id list is empty.");
    public static final ErrorCode VALIDATE_IP_V4_STRING_FAILED = new ErrorCode("1000028", "Input ip is illegal.");
    public static final ErrorCode VALIDATE_SYSTEM_BY_SYSTEM_ID_LIST_EMPTY_INPUT = new ErrorCode("1000029", "Input system id list is empty.");
    public static final ErrorCode VALIDATE_SYSTEM_SYSTEM_NOT_FOUND = new ErrorCode("1000030", "Can not find the reference SystemEntity, please check the input.");
    public static final ErrorCode VALIDATE_API_EMPTY_ID = new ErrorCode("1000031", "Api Id is empty or null.");
    public static final ErrorCode VALIDATE_USER_EMPTY_USER_ID_OR_ROLE_ID = new ErrorCode("1000032", "User ID or Role ID is empty or null.");
    public static final ErrorCode VALIDATE_USER_USER_ID_OR_ROLE_ID_NOT_MATCH = new ErrorCode("1000033", "User ID and Role ID is not matched, please check input data.");
    public static final ErrorCode VALIDATE_EXTERNAL_ENTITY_EMPTY_ID_OR_KEY = new ErrorCode("1000034", "ID or Key is empty or null.");
    public static final ErrorCode VALIDATE_EXTERNAL_ENTITY_EMPTY_ID_NOT_FOUND = new ErrorCode("1000035", "ID not found.");
    public static final ErrorCode VALIDATE_EXTERNAL_ENTITY_WRONG_KEY = new ErrorCode("1000036", "Wrong Id and Key compare.");
    public static final ErrorCode VALIDATE_USER_INVALID_USER_ID = new ErrorCode("1000037", "User Id does not exist.");
    public static final ErrorCode VALIDATE_GENERAL_INVALID_ID = new ErrorCode("1000037", "Id does not exist.");
    public static final ErrorCode VALIDATE_API_ENDPOINT_EMPTY_ID = new ErrorCode("1000038", "Api Endpoint Id is empty or null.");
    public static final ErrorCode VALIDATE_API_ENDPOINT_NOT_FOUND = new ErrorCode("1000039", "Can not find the reference Api Endpoint, please check the input.");
    public static final ErrorCode VALIDATE_API_GW_PLUGIN_NOT_FOUND = new ErrorCode("1000040", "Api Gw Plugin Deploy does not exist.");
    public static final ErrorCode VALIDATE_API_ENDPOINT_CAN_NOT_REPEAT = new ErrorCode("1000041", "Api Endpoint can not repeat, please check the input.");

    // API Error Start with 2000000
    public static final ErrorCode API_KEY_EMPTY_MAINTAIN_KEY_ACTION = new ErrorCode("2000001", "MAINTAIN_ACTION should not be empty.");
    public static final ErrorCode API_KEY_INVALID_MAINTAIN_KEY_ACTION = new ErrorCode("2000002", "The maintain action is invalid or not supported.");
    public static final ErrorCode API_KEY_INCORRECT_MAINTAIN_KEY_RETRIEVE_INPUT = new ErrorCode("2000003", "Incorrect input data, Apikey and FAB must be provided for RETRIEVE action.");
    public static final ErrorCode API_INCORRECT_LIST_API_BY_NAME_INPUT = new ErrorCode("2000004", "Incorrect input data, please give FAB Code, and system name is required if you wants to search by api name.");
    public static final ErrorCode API_INCORRECT_LIST_API_BY_ROLE_INPUT = new ErrorCode("2000005", "Incorrect input data, missing RoleId or FAB Code.");
    public static final ErrorCode API_KEY_RETRIEVE_DATA_NOT_FOUND = new ErrorCode("2000006", "Can not find data with the FAB and Apikey");
    public static final ErrorCode API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT = new ErrorCode("2000007", "Refuse action, Id conflict.");
    public static final ErrorCode API_RESTFUL_DATABASE_UPDATE_DATA_NOT_FOUND = new ErrorCode("2000008", "Update object not found.");
    public static final ErrorCode API_APIKEY_INCORRECT_INPUT_MISSING_ROLE_ID = new ErrorCode("2000009", "Incorrect input data, missing RoleId.");
    public static final ErrorCode API_APIKEY_NO_EXISTS_APIKEY_FOUND = new ErrorCode("2000010", "No exists apikey found for role.");
    public static final ErrorCode API_DOES_NOT_EXISTS = new ErrorCode("2000011", "Incorrect input data, api is not exists.");
    public static final ErrorCode API_KEY_SERVICE_DOES_NOT_EXISTS = new ErrorCode("2000012", "Illegal Request, API service is not exists.");
    public static final ErrorCode API_CLIENT_APIKEY_INCORRECT_INPUT_MISSING_API_PERMISSION = new ErrorCode("2000013", "Incorrect input data, missing Apikey permission list.");
    public static final ErrorCode API_APIKEY_INCORRECT_INPUT_MISMATCHED_INPUT_BETWEEN_PATH_AND_BODY = new ErrorCode("2000014", "Incorrect input data, mismatched path variable and request body.");
    public static final ErrorCode GENERAL_API_INPUT_ID_MISMATCH = new ErrorCode("2000015", "Incorrect input data, data id mismatched.");
    public static final ErrorCode GENERAL_API_MISSING_CRITICAL_DATA_INPUT = new ErrorCode("2000016", "Incorrect input data or missing critical input.");
    public static final ErrorCode LIVE_UPDATE_API_DEPARTMENT_NOT_FOUND = new ErrorCode("2000017", "Can not find the target department.");
    public static final ErrorCode GENERAL_API_DUPLICATE_DATA = new ErrorCode("2000018", "The data or id is already exists.");
    public static final ErrorCode GENERAL_API_ID_MISMATCH = new ErrorCode("2000019", "The id is mismatched.");
    public static final ErrorCode GENERAL_API_ILLEGAL_LOGIC_OPERATION = new ErrorCode("2000020", "Required Operation is illegal.");
    public static final ErrorCode USER_UPDATE_CRON_JOB_DEPARTMENT_NOT_FOUND = new ErrorCode("2000021", "Can not find the target department.");
    public static final ErrorCode API_WRONG_FILE_TYPE = new ErrorCode("2000022", "Unacceptable file type, please follow : ");

    // Fundamental Service Error Starts with 3000000
    public static final ErrorCode SERVICE_KEY_CREATE_KEY_INCORRECT_INPUT = new ErrorCode("3000001", "Incorrect input data, all fields must be provided for CREATE action: ROLE_ID, FAB, and AUTHORITIES.");
    public static final ErrorCode SERVICE_KEY_UPDATE_KEY_INCORRECT_INPUT = new ErrorCode("3000002", "Incorrect input data, all fields must be provided for UPDATE action: Apikey, Role ID, FAB, and Authorities.");
    public static final ErrorCode SERVICE_KEY_DELETE_KEY_INCORRECT_INPUT = new ErrorCode("3000003", "Incorrect input data, all fields must be provided for DELETE action: FAB, Role ID, Apikey ID.");
    public static final ErrorCode SERVICE_AUTHORITY_INSUFFICIENT_ROLE_AUTHORITY = new ErrorCode("3000004", "Incorrect input data, the role has insufficient authorities to grant.");
    public static final ErrorCode SERVICE_LOGICAL_ERROR_RELATION_ENTITY_NO_UPDATE = new ErrorCode("3000005", "Logical error, the relation-defined entity is not supposed to be update.");
    public static final ErrorCode SERVICE_SYSTEM_SYSTEM_NAME_INCORRECT_INPUT = new ErrorCode("3000006", "Incorrect input data, all fields must be provided for actions: SystemEntity Name");
    public static final ErrorCode SERVICE_API_NAME_INCORRECT_INPUT = new ErrorCode("3000007", "Incorrect input data, all fields must be provided for actions: SystemEntity Name, Api name");
    public static final ErrorCode SERVICE_API_MOUNT_SYSTEM_NOT_FOUND = new ErrorCode("3000008", "Can not find the reference SystemEntity, please check the input.");
    public static final ErrorCode SERVICE_API_NOT_FOUND = new ErrorCode("3000009", "Can not find the reference Api, please check the input.");
    public static final ErrorCode SERVICE_API_ID_BLANK_INPUT = new ErrorCode("3000010", "Can not find the reference Api Id, all fields must be verified for actions: SystemEntity Name, Api name");
    public static final ErrorCode SERVICE_ROLE_CREATE_ROLE_INCORRECT_INPUT = new ErrorCode("3000011", "Incorrect input data, all fields must be provided for actions: ROLE_ID, ROLE_NAME, ROLE_TYPE");
    public static final ErrorCode SERVICE_ROLE_INCORRECT_INPUT_EMPTY_ROLE_ID = new ErrorCode("3000012", "Incorrect input data, ROLE_ID is empty or blank.");
    public static final ErrorCode SERVICE_KEY_DELETE_KEY_BY_ROLE_INCORRECT_INPUT = new ErrorCode("3000013", "Incorrect input data, Role ID must be provided for delete apikey by role action.");
    public static final ErrorCode SERVICE_HOST_SYSTEM_DELETE_UNDEPLOY_NOT_COMPLETE = new ErrorCode("3000014", "Procedure of undeploy system among clients is not complete, please check with adm.");
    public static final ErrorCode SERVICE_ROLE_ROLE_NAME_CAN_NOT_CHANGE = new ErrorCode("3000015", "Role name cannot be changed.");


    // Gateway Control Error Starts with 4000000
    public static final ErrorCode GATEWAY_PROPERTY_PROPERTY_UNLOAD = new ErrorCode("4000001", "Gateway properties is not loaded.");
    public static final ErrorCode GATEWAY_PROPERTY_PROPERTY_UNLOAD_SITE = new ErrorCode("4000002", "Gateway properties Site data is not loaded .");
    public static final ErrorCode GATEWAY_PROPERTY_INVALID_DEPLOYMENT_DATA = new ErrorCode("4000003", "Invalid or incorrect data of gateway deployment.");
    public static final ErrorCode GATEWAY_COMMAND_UNABLE_PARSE_JSON = new ErrorCode("4000004", "Internal Error：Unable to parse json from object.");
    public static final ErrorCode GATEWAY_COMMAND_UNABLE_DELETE_KEY = new ErrorCode("4000005", "Internal Error：Unable to delete apikey.");
    public static final ErrorCode GATEWAY_COMMAND_UNABLE_REVOKE_KEY_PERMISSION = new ErrorCode("4000006", "Internal Error：Unable to revoke apikey permission.");
    public static final ErrorCode GATEWAY_COMMAND_ERROR = new ErrorCode("4000007", "Internal Error：Unable to execute command in gateway.");
    public static final ErrorCode GATEWAY_COMMAND_SYSTEM_DELETE_ID_EMPTY = new ErrorCode("4000008", "Gateway SystemEntity is not found.");
    public static final ErrorCode GATEWAY_COMMAND_ROUTE_DELETE_ID_EMPTY = new ErrorCode("4000008", "Gateway Route id is not found.");
    public static final ErrorCode GATEWAY_COMMAND_APIKEY_NAME_PROPERTY_LOST = new ErrorCode("4000009", "Requirement of Compositing a gateway apikey name is missing.");
    public static final ErrorCode GATEWAY_COMMAND__VERIFY_APIKEY_NAME_FAIL = new ErrorCode("4000010", "Apikey Name is not exists on gateway.");

    // CIA Standard Error Starts with 5000000
    public static final ErrorCode CORE_PROPERTY_PROPERTY_UNLOAD = new ErrorCode("5000001", "Client properties is not loaded.");
    public static final ErrorCode CORE_PROPERTY_PROPERTY_UNLOAD_SITE = new ErrorCode("5000002", "Client properties Site data is not loaded .");
    public static final ErrorCode CORE_PROPERTY_INVALID_DEPLOYMENT_DATA = new ErrorCode("5000003", "Invalid or incorrect data of client deployment.");
    public static final ErrorCode CORE_PROPERTY_USER_VERIFICATION_LOGIN_FAIL = new ErrorCode("5000004", "Login Fail, Please check your login information.");
    public static final ErrorCode CORE_PROPERTY_USER_VERIFICATION_NO_ROLE = new ErrorCode("5000005", "No role for on log-in user, please contact the platform manager.");
    public static final ErrorCode JSON_STRING_PROCESSOR_ERROR_ON_REMOVE_TAGS = new ErrorCode("5000006", "Unexpected error occurred while remove tag of Json String, please contact the platform manager.");
    public static final ErrorCode JSON_STRING_PROCESSOR_ERROR_ON_OBTAIN_LOCAL_TAG_AREA = new ErrorCode("5000007", "Unexpected error occurred while obtain local tag of Json String, please contact the platform manager.");
    public static final ErrorCode CRITICAL_PROPERTY_MISSING_OR_WRONG = new ErrorCode("5000008", "Critical config property missing or wrong : ");

    // CIA WEB UI Error Starts with 6000000
    public static final ErrorCode WEB_DEVICE_MANAGEMENT_CAN_NOT_FIND_DEVICE = new ErrorCode("6000001", "Can not find target user device, please check the input data.");
    public static final ErrorCode WEB_API_DOC_CAN_NOT_FIND_DOCUMENT = new ErrorCode("6000002", "Document not found.");
    public static final ErrorCode WEB_API_DOC_CAN_NOT_PARSE_YAML_DATA = new ErrorCode("6000003", "Unable to parse YAML data, please recheck.");
    public static final ErrorCode WEB_APIKEY_PERMISSION_DATA_ERROR_FAB_GATEWAY_NO_FOUND = new ErrorCode("6000004", "Unable to find gateway data by missing fab id.");
    public static final ErrorCode WEB_MANAGER_UNAUTHORISED_USER = new ErrorCode("6000005", "Unauthorised user.");

    // Sign Off Type Starts with 6000200
    public static final ErrorCode WEB_SIGN_OFF_APPLY_ROLE_AUTHORITY_EXISTS_ROLE_AUTH = new ErrorCode("6000201", "Capable authority for role detected in apply list.");
    public static final ErrorCode WEB_SIGN_OFF_APPLY_ROLE_AUTHORITY_APPLY_ITEM_NOT_EXISTS = new ErrorCode("6000202", "Apply Api and deployment is not exists.");
    public static final ErrorCode WEB_SIGN_OFF_APPLY_ROLE_AUTHORITY_APPLY_DATA_MISSING = new ErrorCode("6000203", "Apply Data is missing or wrong.");


    // Web Service Error Starts with 8000000
    public static final ErrorCode WEB_SERVICE_PROCESS_ERROR_WRONG_TYPE_OF_INPUT = new ErrorCode("8000001", "The input type is wrong. Please contact the system owner.");
    public static final ErrorCode WEB_SERVICE_PROCESS_ERROR_CAN_NOT_RETRIEVE_SPECIFIC_URL = new ErrorCode("8000002", "Can not retrieve specific url path. Please contact the system owner.");
    public static final ErrorCode WEB_SERVICE_PROCESS_ERROR_CAN_NOT_READ_SOAP_RESPONSE = new ErrorCode("8000003", "Internal Server Error, can not read the response from signature system.");

    // Soap Starts with 8000100
    public static final ErrorCode SOAP_WEB_SERVICE_CALLING_ERROR_NOT_MATCHED_RESPONSE_TYPE = new ErrorCode("8000101", "The response type does not match the expected type.");


    // Unknowing Exception Occurred, Start with 9000000 .
    public static final ErrorCode UNKNOWN_EXCEPTION_API_ERROR = new ErrorCode("9000001", "Unknown problems occurred. Please contact the owner.");
    public static final ErrorCode UNKNOWN_EXCEPTION_WEB_ERROR = new ErrorCode("9000002", "Unknown problems occurred. Please contact the owner.");
    public static final ErrorCode UNKNOWN_EXCEPTION_WEB_ERROR_ON_SIGN_OFF = new ErrorCode("9000003", "Unknown problems occurred. Partial apply items are failed. Please contact the owner.");
    public static final ErrorCode UNKNOWN_EXCEPTION_CLIENT_INIT_FAIL = new ErrorCode("9000004", "Unknown problems occurred while initializing client data.");
    public static final ErrorCode UNKNOWN_EXCEPTION_ON_UPDATING_USER_DATA_ERROR = new ErrorCode("9000005", "Unknown problems occurred while updating user data.");

    private ErrorConstantLib()
    {
    }

    @Data
    @AllArgsConstructor
    public static class ErrorCode
    {
        private final String code;
        private final String message;

        public String getCompleteMessage()
        {
            return "CIA Error：" + code + " - " + message;
        }
    }
}