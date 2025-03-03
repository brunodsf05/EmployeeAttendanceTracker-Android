package bdisfer1410.controldepresencia.tools;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Hour {
    public static final String FORMAT = "H:mm";
    public static final String INVALID = "...";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMAT);

    public static String format(LocalTime time) {
        return (time == null)
                ? INVALID
                : time.format(FORMATTER);
    }
}
