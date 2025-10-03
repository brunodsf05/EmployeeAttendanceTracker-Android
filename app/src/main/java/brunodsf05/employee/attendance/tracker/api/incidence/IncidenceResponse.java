package brunodsf05.employee.attendance.tracker.api.incidence;

public class IncidenceResponse {
    private final String message;

    public IncidenceResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}