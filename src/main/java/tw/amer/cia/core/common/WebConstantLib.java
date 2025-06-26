package tw.amer.cia.core.common;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class WebConstantLib {

    // User Auth
    public static final String WEB_UI_CALL_API_REQUEST_HEADER_ACCOUNT = "CIA-HEADER-ACCOUNT";
    public static final String WEB_UI_CALL_API_REQUEST_HEADER_PASSWORD = "CIA-HEADER-PASSWORD";

    // General
    public static final String WEB_UI_CALL_API_REQUEST_HEADER_USER_ID = "CIA-HEADER-USER_ID";
    public static final String WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID = "CIA-HEADER-ROLE_ID";


    //API Card
    public enum WEB_UI_API_CARD_ROLE_AUTHED_STATUS {
        TOTALLY_OBTAINED("TOTALLY_OBTAINED"),
        OPEN_FOR_APPLY("OPEN_FOR_APPLY"),
        ON_APPLYING("ON_APPLYING");

        @Getter
        private final String status;

        WEB_UI_API_CARD_ROLE_AUTHED_STATUS(String status) {
            this.status = status;
        }
    }

    public enum ResourceReference {
        SYSTEM,
        API,
        ROLE,
        APIKEY,
        WEB_PAGE;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }

        public static List<ResourceReference> getAllAsList() {
            return Arrays.asList(ResourceReference.values());
        }

        // 1. 忽略大小寫 (IgnoreCase)
        public static boolean checkLegalityIgnoreCase(String unCheckRefName) {
            if (unCheckRefName == null || unCheckRefName.isEmpty()) {
                return false;
            }
            for (ResourceReference category : ResourceReference.values()) {
                if (category.toString().equalsIgnoreCase(unCheckRefName)) {
                    return true;
                }
            }
            return false;
        }

        // 2. 大小寫敏感 (CaseSensitive)
        public static boolean checkLegality(String unCheckRefName) {
            if (unCheckRefName == null || unCheckRefName.isEmpty()) {
                return false;
            }
            for (ResourceReference category : ResourceReference.values()) {
                if (category.toString().equals(unCheckRefName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public enum WebPageResourceReferenceId {
        USER_APIKEY_MANAGEMENT_GUIDANCE;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }
    public enum ResourceType {
        DOC,
        IMAGE,
        ICON,
        GW_CONFIG;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }

        public static List<ResourceType> getAllAsList() {
            return Arrays.asList(ResourceType.values());
        }

        // 1. 忽略大小寫 (IgnoreCase)
        public static boolean checkLegalityIgnoreCase(String unCheckTypeName) {
            if (unCheckTypeName == null || unCheckTypeName.isEmpty()) {
                return false;
            }
            for (ResourceType category : ResourceType.values()) {
                if (category.toString().equalsIgnoreCase(unCheckTypeName)) {
                    return true;
                }
            }
            return false;
        }

        // 2. 大小寫敏感 (CaseSensitive)
        public static boolean checkLegality(String unCheckTypeName) {
            if (unCheckTypeName == null || unCheckTypeName.isEmpty()) {
                return false;
            }
            for (ResourceType category : ResourceType.values()) {
                if (category.toString().equals(unCheckTypeName)) {
                    return true;
                }
            }
            return false;
        }
    }
}
