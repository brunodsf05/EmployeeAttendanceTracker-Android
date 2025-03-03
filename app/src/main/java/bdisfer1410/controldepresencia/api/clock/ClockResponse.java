package bdisfer1410.controldepresencia.api.clock;

import bdisfer1410.controldepresencia.models.ClockAction;

public class ClockResponse {
    private final String action;

    public ClockResponse(String action) {
        this.action = action;
    }

    public String getActionString() {
        return action;
    }

    public ClockAction getAction() {
        return ClockAction.fromString(action);
    }
}
