package brunodsf05.employee.attendance.tracker.api.clock.send;

public class ClockSendRequest {
    private final double latitude;
    private final double longitude;

    public ClockSendRequest(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
