package tw.amer.cia.core.common;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import tw.amer.cia.core.model.database.FabEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class GeneralSetting {
    public enum CiaDeployType {
        HOST("host"),
        CLIENT("client");
        @Getter
        private String displayName;

        private CiaDeployType(String _disPlayName) {
            this.displayName = _disPlayName;
        }
    }

    public static final FabEntity SANDBOX_FAB = FabEntity.builder()
            .site("VIRTUAL")
            .fabId("SANDBOX")
            .build();

    public static final int APIKEY_ID_LENGTH = 20;
    public static final boolean APIKEY_ID_CONTAIN_CHAR = true;
    public static final boolean APIKEY_ID_CONTAIN_NUMBER = true;
    public static final String APIKEY_DISABLED_KEY_STRING = "DisabledApikey";

    public static final int SYSTEM_ID_LENGTH = 16;
    public static final boolean SYSTEM_ID_CONTAIN_CHAR = true;
    public static final boolean SYSTEM_ID_CONTAIN_NUMBER = true;

    public static final int API_ID_LENGTH = 16;
    public static final boolean API_ID_CONTAIN_CHAR = true;
    public static final boolean API_ID_CONTAIN_NUMBER = true;

    public static final int API_ENDPOINT_ID_LENGTH = 16;
    public static final boolean API_ENDPOINT_ID_CONTAIN_CHAR = true;
    public static final boolean API_ENDPOINT_ID_CONTAIN_NUMBER = true;

    public static final int ROLE_DEVICE_ID_LENGTH = 16;
    public static final boolean ROLE_DEVICE_ID_CONTAIN_CHAR = true;
    public static final boolean ROLE_DEVICE_ID_CONTAIN_NUMBER = true;

    public static final int GW_UPSTREAM_ID_LENGTH = 16;
    public static final boolean GW_UPSTREAM_ID_CONTAIN_CHAR = true;
    public static final boolean GW_UPSTREAM_ID_CONTAIN_NUMBER = true;

    public static final int GW_ROUTE_ID_LENGTH = 16;
    public static final boolean GW_ROUTE_ID_CONTAIN_CHAR = true;
    public static final boolean GW_ROUTE_ID_CONTAIN_NUMBER = true;

    public static final int GW_PLUGIN_ID_LENGTH = 16;
    public static final boolean GW_PLUGIN_ID_CONTAIN_CHAR = true;
    public static final boolean GW_PLUGIN_ID_CONTAIN_NUMBER = true;

    public static final int GENERAL_ID_LENGTH = 16;
    public static final boolean GENERAL_ID_CONTAIN_CHAR = true;
    public static final boolean GENERAL_ID_CONTAIN_NUMBER = true;

    // Text Component
    public static final String GENERAL_POSITIVE_STRING = "Y";
    public static final String GENERAL_NEGATIVE_STRING = "N";

    public static final String GENERAL_ACTIVE_STATUS_ACTIVE = "ACTIVE";

    public static final String GENERAL_ACTIVE_STATUS_NON_ACTIVE = "NON_ACTIVE";

    public static boolean isGeneralPositive(String onJudgeString) {
        return StringUtils.equalsIgnoreCase(onJudgeString, GENERAL_POSITIVE_STRING);
    }

    public static String getGeneralBooleanString(boolean onTransfer) {
        return onTransfer ? GENERAL_POSITIVE_STRING : GENERAL_NEGATIVE_STRING;
    }

    public static final String SEPARATE_LINE = "-------------------";
    public static final String SEPARATE_LINE_DOUBLE = "--------------------------------------";

    public static List<String> getDefaultHttpMethods() {
        return Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "CONNECT", "TRACE", "PURGE"
        );
    }

    public static String validateUriFormat(String uncheckUri) throws URISyntaxException {
        if (!uncheckUri.startsWith("/")) {
            uncheckUri = "/" + uncheckUri;
        }
        // 使用URI構造器來驗證URI的有效性
        URI tryTransfer = new URI(uncheckUri);
        return uncheckUri;
    }
}
