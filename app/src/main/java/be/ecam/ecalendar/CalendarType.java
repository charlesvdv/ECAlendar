package be.ecam.ecalendar;

/**
 * Created by Antoi on 21/03/2017.
 * //id, type description
 */

public class CalendarType {
    private String id = null;
    private String description = null;

    CalendarType(String id, String description) {
        this.description = description;
        this.id = id;
    }

    public String getId() { return id;}
    public String getDescription() { return description;}

}
