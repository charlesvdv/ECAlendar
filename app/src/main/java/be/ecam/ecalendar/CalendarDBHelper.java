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
        final String SQL_CREATE_CALENDAR_TABLE =
                "CREATE TABLE " + CalendarEntry.TABLE_NAME + " (" +
                        CalendarEntry.CALENDAR_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        CalendarEntry.CALENDAR_ACTIVITY_NAME + " TEXT NOT NULL," +
                        CalendarEntry.CALENDAR_START_TIME + " TEXT NOT NULL," +
                        CalendarEntry.CALENDAR_END_TIME + " TEXT NOT NULL," +
                        CalendarEntry.CALENDAR_GROUP + " TEXT NOT NULL" +
                        CalendarEntry.CALENDAR_TEACHER + " TEXT NOT NULL" +
                        CalendarEntry.CALENDAR_CLASS_ROOM + " TEXT NOT NULL" +
                        CalendarEntry.CALENDAR_NOTE + " TEXT NOT NULL" +
                        "); ";
        db.execSQL(SQL_CREATE_CALENDAR_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + CalendarEntry.TABLE_NAME);

        onCreate(db);
    }
}
