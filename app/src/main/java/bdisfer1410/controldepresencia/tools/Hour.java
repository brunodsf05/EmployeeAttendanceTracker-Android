package bdisfer1410.controldepresencia.tools;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Hour {
    public static final String INVALID = "...";

    public static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter STRING_FORMATTER = DateTimeFormatter.ofPattern("H:mm");

    public static String format(LocalTime time) {
        return (time == null)
                ? INVALID
                : time.format(STRING_FORMATTER);
    }

    public static LocalTime parse(String time) {
        return (time == null)
                ? null
                : LocalTime.parse(time, HOUR_FORMATTER);
    }
}
