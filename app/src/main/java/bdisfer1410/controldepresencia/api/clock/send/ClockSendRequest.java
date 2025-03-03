package bdisfer1410.controldepresencia.api.clock.send;

public class ClockSendRequest {
    private final float latitude;
    private final float longitude;

    public ClockSendRequest(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
