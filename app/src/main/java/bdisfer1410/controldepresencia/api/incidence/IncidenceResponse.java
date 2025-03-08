package bdisfer1410.controldepresencia.api.incidence;

import com.google.gson.annotations.SerializedName;

import bdisfer1410.controldepresencia.models.Tokens;

public class IncidenceResponse {
    private final String message;

    public IncidenceResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}