package be.ecam.ecalendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import be.ecam.ecalendar.CalendarContract.CalendarTypeEntry;
import be.ecam.ecalendar.CalendarContract.ScheduleEntry;


/**
 * Created by Sylvain on 21-03-17.
 */

public class CalendarDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "calendar.db";
    private static final int DATABASE_VERSION = 1;

    public CalendarDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_SCHEDULE_TABLE =
                "CREATE TABLE " + ScheduleEntry.TABLE_NAME + " (" +
                        ScheduleEntry.SCHEDULE_ACTIVITY_ID + " TEXT NOT NULL, " +
                        ScheduleEntry.SCHEDULE_CALENDAR + " TEXT NOT NULL, " +
                        ScheduleEntry.SCHEDULE_ACTIVITY_NAME + " TEXT NOT NULL, " +
                        ScheduleEntry.SCHEDULE_START_TIME + " INTEGER NOT NULL, " +
                        ScheduleEntry.SCHEDULE_END_TIME + " INTEGER NOT NULL, " +
                        ScheduleEntry.SCHEDULE_GROUP + " TEXT NOT NULL, " +
                        ScheduleEntry.SCHEDULE_TEACHER + " TEXT NOT NULL, " +
                        ScheduleEntry.SCHEDULE_CLASS_ROOM + " TEXT NOT NULL, " +
                        "PRIMARY KEY (" + ScheduleEntry.SCHEDULE_ACTIVITY_ID + ", " +
                        ScheduleEntry.SCHEDULE_START_TIME + ", " +
                        ScheduleEntry.SCHEDULE_CALENDAR + ", " +
                        ScheduleEntry.SCHEDULE_END_TIME + ")); ";
        db.execSQL(SQL_CREATE_SCHEDULE_TABLE);

        final String SQL_CREATE_CALENDAR_TYPE_TABLE =
                "CREATE TABLE " + CalendarTypeEntry.TABLE_NAME + " (" +
                        CalendarTypeEntry.CALENDAR_TYPE_ID + " TEXT NOT NULL, " +
                        CalendarTypeEntry.CALENDAR_TYPE_DESCRIPTION + " TEXT NOT NULL, " +
                        CalendarTypeEntry.CALENDAR_TYPE_TYPE + " TEXT NOT NULL, " +
                        "PRIMARY KEY (" + CalendarTypeEntry.CALENDAR_TYPE_ID + ", " +
                        CalendarTypeEntry.CALENDAR_TYPE_TYPE  + ")); ";
        db.execSQL(SQL_CREATE_CALENDAR_TYPE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CalendarTypeEntry.TABLE_NAME);

        onCreate(db);
    }
}
