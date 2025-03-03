package bdisfer1410.controldepresencia.tools;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Hour {
    public static final String FORMAT = "HH:mm";
    public static final String INVALID = "...";

    public static String parse(LocalTime time) {
        if (time == null) {
            return INVALID;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT);
        return time.format(formatter);
    }
}
