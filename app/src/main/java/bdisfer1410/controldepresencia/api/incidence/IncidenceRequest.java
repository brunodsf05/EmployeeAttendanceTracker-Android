package bdisfer1410.controldepresencia.api.incidence;

import java.util.Date;

public class IncidenceRequest {
    private final Date datetime;
    private final String description;

    public IncidenceRequest(Date datetime, String description) {
        this.datetime = datetime;
        this.description = description;
    }

    public Date getDatetime() {
        return datetime;
    }

    public String getDescription() {
        return description;
    }
}
