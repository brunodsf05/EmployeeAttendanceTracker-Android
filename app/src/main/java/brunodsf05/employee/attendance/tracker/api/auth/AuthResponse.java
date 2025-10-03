package brunodsf05.employee.attendance.tracker.api.auth;

import com.google.gson.annotations.SerializedName;

import brunodsf05.employee.attendance.tracker.models.Tokens;

public class AuthResponse {
    @SerializedName("access_token")
    private final String accessToken;

    @SerializedName("refresh_token")
    private final String refreshToken;

    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Tokens getTokens() {
        return new Tokens(accessToken, refreshToken);
    }
}