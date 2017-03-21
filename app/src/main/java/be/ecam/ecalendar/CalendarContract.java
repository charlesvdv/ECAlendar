package be.ecam.ecalendar;

import android.provider.BaseColumns;

/**
 * Created by Sylvain on 21-03-17.
 */

public class CalendarContract {
    public static final class CalendarEntry implements BaseColumns {
        public static final String TABLE_NAME = "calendar";
        public static final String CALENDAR_ACTIVITY_ID = "activity_id";
        public static final String CALENDAR_ACTIVITY_NAME = "activity_name";
        public static final String CALENDAR_START_TIME = "start_time";
        public static final String CALENDAR_END_TIME = "end_time";
        public static final String CALENDAR_GROUP = "group";
        public static final String CALENDAR_TEACHER = "teacher";
        public static final String CALENDAR_CLASS_ROOM = "class_room";
        public static final String CALENDAR_NOTE = "note";
    }
}