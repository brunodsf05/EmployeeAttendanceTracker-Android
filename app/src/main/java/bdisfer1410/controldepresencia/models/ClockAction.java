package bdisfer1410.controldepresencia.models;

import bdisfer1410.controldepresencia.R;

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

    public int getButtonText() {
        switch (this) {
            case FREEDAY:
            case WAIT:
            case START:
                return R.string.clock_btn_clock_in;
            case WORK:
            case EXIT:
            case RECOVER:
                return R.string.clock_btn_clock_out;
            default:
                return R.string.clock_btn_unknown;
        }
    }

    // Debug helpers
    public ClockAction getNext() {
        switch (this) {
            case WAIT: return FREEDAY;
            case START: return WAIT;
            case WORK: return START;
            case EXIT: return WORK;
            case RECOVER: return EXIT;
            case NOTIFY_AUSENCE: return RECOVER;
            case TOBEIN_WORK: return NOTIFY_AUSENCE;
            case FREEDAY: default: return TOBEIN_WORK;
        }
    }
}
