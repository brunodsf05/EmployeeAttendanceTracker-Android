package brunodsf05.employee.attendance.tracker.api.clock.action;

import com.google.gson.annotations.SerializedName;

public class ClockActionErrorResponse {
    private static final String ERROR_KEYNAME_SUFFIX = "clock_error_actionservice_";
    private final String error;
    @SerializedName("msg")
    private final String tokenError;

    public ClockActionErrorResponse(String error, String tokenError) {
        this.error = error;
        this.tokenError = tokenError;
    }

    public boolean hasTokenError() {
        return tokenError != null && !tokenError.isEmpty();
    }

    public String getError() {
        return (hasTokenError())
                ? "app_error_accesstoken_invalid"
                : String.format("%s%s", ERROR_KEYNAME_SUFFIX, error);
    }

    public String getShortError() {
        return (hasTokenError())
                ? "app_error_accesstoken_invalid"
                : error;
    }
}
