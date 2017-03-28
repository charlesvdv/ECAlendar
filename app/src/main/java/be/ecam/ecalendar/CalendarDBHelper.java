package be.ecam.ecalendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import be.ecam.ecalendar.CalendarContract.*;


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
                        ScheduleEntry.SCHEDULE_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        ScheduleEntry.SCHEDULE_ACTIVITY_NAME + " TEXT NOT NULL," +
                        ScheduleEntry.SCHEDULE_START_TIME + " TEXT NOT NULL," +
                        ScheduleEntry.SCHEDULE_END_TIME + " TEXT NOT NULL," +
                        ScheduleEntry.SCHEDULE_GROUP + " TEXT NOT NULL" +
                        ScheduleEntry.SCHEDULE_TEACHER + " TEXT NOT NULL" +
                        ScheduleEntry.SCHEDULE_CLASS_ROOM + " TEXT NOT NULL" +
                        "); ";
        db.execSQL(SQL_CREATE_SCHEDULE_TABLE);

        final String SQL_CREATE_CALENDAR_TYPE_TABLE =
                "CREATE TABLE " + CalendarTypeEntry.TABLE_NAME + " (" +
                        CalendarTypeEntry.CALENDAR_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        CalendarTypeEntry.CALENDAR_TYPE_DESCRIPTION + " TEXT NOT NULL," +
                        "); ";
        db.execSQL(SQL_CREATE_CALENDAR_TYPE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CalendarTypeEntry.TABLE_NAME);

        onCreate(db);
    }
}
