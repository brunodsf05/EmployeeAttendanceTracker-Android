package brunodsf05.employee.attendance.tracker.api.incidence;

import com.google.gson.annotations.SerializedName;

public class IncidenceErrorResponse {
    private static final String ERROR_KEYNAME_SUFFIX = "incidence_error_service_";

    private final String error;
    @SerializedName("msg")
    private final String tokenError;

    public IncidenceErrorResponse(String error, String tokenError) {
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