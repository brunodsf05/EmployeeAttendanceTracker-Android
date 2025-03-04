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
    FREEDAY,
    ERROR_CONNECTION;

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

    public int getFeedbackTitle() {
        switch (this) {
            case WAIT: return R.string.clock_fdb_wait;
            case START: return R.string.clock_fdb_start;
            case WORK: return R.string.clock_fdb_work;
            case EXIT: return R.string.clock_fdb_exit;
            case RECOVER: return R.string.clock_fdb_recover;
            case NOTIFY_AUSENCE: return R.string.clock_fdb_notify_ausence;
            case TOBEIN_WORK: return R.string.clock_fdb_tobein_work;
            case FREEDAY: return R.string.clock_fdb_freeday;
        }

        return R.string.clock_fdb_error;
    }

    public int getFeedbackDescription() {
        switch (this) {
            case WAIT: return R.string.clock_lbl_wait;
            case START: return R.string.clock_lbl_start;
            case WORK: return R.string.clock_lbl_work;
            case EXIT: return R.string.clock_lbl_exit;
            case RECOVER: return R.string.clock_lbl_recover;
            case NOTIFY_AUSENCE: return R.string.clock_lbl_notify_ausence;
            case TOBEIN_WORK: return R.string.clock_lbl_tobein_work;
            case FREEDAY: return R.string.clock_lbl_freeday;
            case ERROR_CONNECTION: return R.string.clock_lbl_error_connection;
        }

        return R.string.clock_lbl_error;
    }

    public boolean doesLocationMatter() {
        switch (this) {
            case WAIT:
            case WORK:
            case RECOVER:
            case NOTIFY_AUSENCE:
            case FREEDAY:
                return false;
            case START:
            case EXIT:
            case TOBEIN_WORK:
                return true;
        }

        return false;
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
