package bdisfer1410.controldepresencia.view.login.api;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    private static final String ERROR_KEYNAME_SUFFIX = "login_error_authservice_";

    private final boolean success;
    @SerializedName("access_token") private final String accessToken;
    @SerializedName("refresh_token") private final String refreshToken;
    private final String error;

    public AuthResponse(boolean success, String accessToken, String refreshToken, String error) {
        this.success = success;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getError() {
        return error;
    }

    public String getErrorKey() {
        return String.format("%s%s", ERROR_KEYNAME_SUFFIX, error);
    }
}