package bdisfer1410.controldepresencia.models;

public enum ClockAction {
    WAIT,
    START,
    WORK,
    EXIT,
    RECOVER,
    NOTIFY_AUSENCE,
    TOBEIN_WORK,
    FREEDAY;

    public static ClockAction fromString(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        try {
            return ClockAction.valueOf(text.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean canClock() {
        switch (this) {
            case START:
            case EXIT:
                return true;
            default:
                return false;
        }
    }
}
