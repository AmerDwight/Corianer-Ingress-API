package tw.amer.cia.core.common;

public final class SuccessConstantLib
{

    // Start with 0000000 ~ 0999999
    public static final SuccessCode PURGE_KEY_BY_FAB = new SuccessCode("0000001", "Successfully Purge Apikey data in Target Fab.");
    public static final SuccessCode HOST_PURGE_KEY_ALL_FAB = new SuccessCode("0000002", "Successfully Purge Apikey data among All Fab.");
    public static final SuccessCode CLIENT_PURGE_KEY_AMONG_SCOPE = new SuccessCode("0000003", "Successfully Purge Apikey data among target site.");
    public static final SuccessCode MODIFY_KEY_SCOPE_SUCCESS = new SuccessCode("0000004", "Successfully Modify Apikey data within scopes.");
    public static final SuccessCode MODIFY_KEY_ACTIVE_STATUS_SUCCESS = new SuccessCode("0000005", "Successfully Change Apikey Active status.");

    public static class SuccessCode
    {
        private final String code;
        private final String message;

        public SuccessCode(String code, String message)
        {
            this.code = code;
            this.message = message;
        }

        public String getCode()
        {
            return code;
        }

        public String getMessage()
        {
            return message;
        }

        public String getCompleteMessage()
        {
            return "SUCCESS Code：" + code + ", " + message;
        }

        @Override
        public String toString()
        {
            return code + ": " + message;
        }
    }
}