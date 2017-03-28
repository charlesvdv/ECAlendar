package be.ecam.ecalendar;

import android.provider.BaseColumns;

/**
 * Created by Sylvain on 21-03-17.
 */

 public class CalendarContract {
    public static final class ScheduleEntry implements BaseColumns {
        public static final String TABLE_NAME = "schedule";
        public static final String SCHEDULE_ACTIVITY_ID = "activity_id";
        public static final String SCHEDULE_ACTIVITY_NAME = "activity_name";
        public static final String SCHEDULE_START_TIME = "start_time";
        public static final String SCHEDULE_END_TIME = "end_time";
        public static final String SCHEDULE_GROUP = "group";
        public static final String SCHEDULE_TEACHER = "teacher";
        public static final String SCHEDULE_CLASS_ROOM = "class_room";
    }

    public static final class CalendarTypeEntry implements BaseColumns{
        public static final String TABLE_NAME = "calendar_type";
        public static final String CALENDAR_TYPE_ID = "id";
        public static final String CALENDAR_TYPE_DESCRIPTION = "description";
        public static final String CALENDAR_TYPE_TYPE = "type";
    }
}


