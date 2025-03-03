package bdisfer1410.controldepresencia.api.clock;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import bdisfer1410.controldepresencia.models.ClockAction;
import bdisfer1410.controldepresencia.tools.Hour;

public class ClockResponse {
    private final String action;

    @SerializedName("hora_entrada")
    private String startHourString;

    @SerializedName("hora_salida")
    private String exitHourString;

    public ClockResponse(String action, String startHourString, String exitHourString) {
        this.action = action;
        this.startHourString = startHourString;
        this.exitHourString = exitHourString;
    }

    public String getActionString() {
        return action;
    }

    public String getStartHourString() {
        return startHourString;
    }

    public String getExitHourString() {
        return exitHourString;
    }

    public ClockAction getAction() {
        return ClockAction.fromString(action);
    }

    public LocalTime getStartHour() {
        return Hour.parse(startHourString);
    }

    public LocalTime getExitHour() {
        return Hour.parse(exitHourString);
    }
}
