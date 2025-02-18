package bdisfer1410.controldepresencia.view.login.api;

public class AuthResponse {
    private static final String ERROR_KEYNAME_SUFFIX = "login_error_authservice_";

    private boolean success;
    private String token;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public String getToken() {
        return token;
    }

    public String getError() {
        return error;
    }

    public String getErrorKey() {
        return String.format("%s%s", ERROR_KEYNAME_SUFFIX, error);
    }
}