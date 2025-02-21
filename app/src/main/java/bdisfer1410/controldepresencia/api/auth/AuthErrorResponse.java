package bdisfer1410.controldepresencia.api.auth;

public class AuthErrorResponse {
    private static final String ERROR_KEYNAME_SUFFIX = "login_error_authservice_";

    private final String error;

    public AuthErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return String.format("%s%s", ERROR_KEYNAME_SUFFIX, error);
    }

    public String getShortError() {
        return error;
    }
}