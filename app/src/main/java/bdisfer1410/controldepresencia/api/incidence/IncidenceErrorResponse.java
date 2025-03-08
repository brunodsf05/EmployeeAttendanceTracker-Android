package bdisfer1410.controldepresencia.api.incidence;

public class IncidenceErrorResponse {
    private static final String ERROR_KEYNAME_SUFFIX = "incidence_error_service_";

    private final String error;

    public IncidenceErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return String.format("%s%s", ERROR_KEYNAME_SUFFIX, error);
    }

    public String getShortError() {
        return error;
    }
}