package bdisfer1410.controldepresencia.api.clock.action;

import bdisfer1410.controldepresencia.models.ClockAction;

public class ClockActionResponse {
    private final String action;

    public ClockActionResponse(String action) {
        this.action = action;
    }

    public String getActionString() {
        return action;
    }

    public ClockAction getAction() {
        return ClockAction.fromString(action);
    }
}
