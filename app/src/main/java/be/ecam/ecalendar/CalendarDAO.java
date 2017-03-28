package be.ecam.ecalendar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.BroadcastReceiver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import be.ecam.ecalendar.CalendarContract.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by charles on 3/28/17.
 */

// TODO:charlesvdv should implement OnUpdateListener for types and calendar.
public class CalendarDAO extends BroadcastReceiver {
    private static final String LAST_SAVED_PREF_FILE_KEY = "last_saved_query";
    private static final String LAST_SAVED_TYPES_ID = "types";
    private static final String LAST_SAVED_CALENDAR_ID = "calendar-";
    private static final int TIME_BEFORE_RELOAD = 60 * 60 * 24 * 7;

    private Context context;
    private SharedPreferences lastTimePref;

    private CalendarDBHelper dbHelper;
    private SQLiteDatabase db;

    private HashMap<String, ArrayList<Schedule>> calendars;
    private HashMap<String, ArrayList<CalendarType>> types;

    private long lastSavedTimeTypes;

    public CalendarDAO(Context context) {
        this.context = context;

        dbHelper = new CalendarDBHelper(context);
        db = dbHelper.getWritableDatabase();

        calendars = new HashMap<>();
        types = new HashMap<>();
        loadTypesFromDB();

        lastTimePref = context.getSharedPreferences(LAST_SAVED_PREF_FILE_KEY,
                Context.MODE_PRIVATE);
        lastSavedTimeTypes = lastTimePref.getLong(LAST_SAVED_TYPES_ID, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getStringExtra("action")) {
            case "schedule":
                String name = intent.getStringExtra("name");
                ArrayList<Schedule> schedules = intent.getParcelableArrayListExtra("schedules");

                calendars.put(name, schedules);
                saveCalendar(name, schedules);
                break;
            case "type":
                for (String key : new String[]{"teachers", "classrooms", "groups"}) {
                    types.put(key, intent.<CalendarType>getParcelableArrayListExtra(key));
                }

                saveCalendarType();
                break;
        }
    }

    public HashMap<String, ArrayList<CalendarType>> getCalendarTypes() {
        long current = new Date().getTime();
        if (types.isEmpty() || current - lastSavedTimeTypes > TIME_BEFORE_RELOAD) {
            downloadTypesData();
        }
        return types;
    }

    public ArrayList<Schedule> getCalendar(String name) {
        long lastSaved = lastTimePref.getLong(LAST_SAVED_CALENDAR_ID + name, 0);
        long current = new Date().getTime();

        if (calendars.get(name).isEmpty() || current - lastSaved > TIME_BEFORE_RELOAD) {
            downloadCalendar(name);
        } else {
            loadCalendarFromDB(name);
        }
        return calendars.get(name);
    }

    private void downloadTypesData() {
        Intent intent = new Intent(context, CalendarLoader.class);
        intent.putExtra("action", "type");
        context.startService(intent);
    }

    private void loadTypesFromDB() {
        for (ArrayList<CalendarType> type : types.values()) {
            type.clear();
        }
        // Get everythings from CalendarType table.
        Cursor cursor = db.query(CalendarTypeEntry.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.isAfterLast()) {
            String type = cursor.getString(cursor.getColumnIndex(CalendarTypeEntry.CALENDAR_TYPE_TYPE));
            String id = cursor.getString(cursor.getColumnIndex(CalendarTypeEntry.CALENDAR_TYPE_ID));
            String desc = cursor.getString(cursor.getColumnIndex(CalendarTypeEntry.CALENDAR_TYPE_DESCRIPTION));
            types.get(type).add(new CalendarType(id, desc));

            cursor.moveToNext();
        }
    }

    private void downloadCalendar(String name) {
        Intent intent = new Intent(context, CalendarLoader.class);
        intent.putExtra("action", "schedule");
        intent.putExtra("name", name);
        context.startService(intent);
    }

    private void loadCalendarFromDB(String name) {
        calendars.get(name).clear();

        Cursor cursor = db.query(ScheduleEntry.TABLE_NAME, null,
                ScheduleEntry.SCHEDULE_ACTIVITY_ID + "=", new String[] {name}, null, null, null);
        while (cursor.isAfterLast()) {
            calendars.get(name).add(new Schedule(
                    name,
                    new Date(cursor.getLong(cursor.getColumnIndex(ScheduleEntry.SCHEDULE_START_TIME))),
                    new Date(cursor.getLong(cursor.getColumnIndex(ScheduleEntry.SCHEDULE_END_TIME))),
                    cursor.getString(cursor.getColumnIndex(ScheduleEntry.SCHEDULE_ACTIVITY_NAME)),
                    cursor.getString(cursor.getColumnIndex(ScheduleEntry.SCHEDULE_GROUP)),
                    cursor.getString(cursor.getColumnIndex(ScheduleEntry.SCHEDULE_TEACHER)),
                    cursor.getString(cursor.getColumnIndex(ScheduleEntry.SCHEDULE_CLASS_ROOM))
            ));

            cursor.moveToNext();
        }
    }

    private void saveLastQueryTime(String id) {
        SharedPreferences.Editor editor = lastTimePref.edit();
        editor.putLong(id, new Date().getTime());
        editor.commit();
    }

    private void saveCalendarType() {
        // Delete all data associated to calendar type.
        db.delete(CalendarTypeEntry.TABLE_NAME, null, null);

        for (Map.Entry<String, ArrayList<CalendarType>> entry : types.entrySet()) {
            String type = entry.getKey();

            for (CalendarType typeData : entry.getValue()) {
                ContentValues values = new ContentValues();
                values.put(CalendarTypeEntry.CALENDAR_TYPE_ID, typeData.getId());
                values.put(CalendarTypeEntry.CALENDAR_TYPE_DESCRIPTION, typeData.getDescription());
                values.put(CalendarTypeEntry.CALENDAR_TYPE_TYPE, type);

                db.insert(CalendarTypeEntry.TABLE_NAME, null, values);
            }
        }

        saveLastQueryTime(LAST_SAVED_TYPES_ID);
        lastSavedTimeTypes = new Date().getTime();
    }

    private void saveCalendar(String name, ArrayList<Schedule> schedules) {
        // Remove previously saved calendar data with the name.
        db.delete(ScheduleEntry.TABLE_NAME,
                ScheduleEntry.SCHEDULE_ACTIVITY_ID + "=?", new String[] { name });

        for (Schedule sched : schedules) {
            ContentValues values = new ContentValues();
            values.put(ScheduleEntry.SCHEDULE_ACTIVITY_ID, name);
            values.put(ScheduleEntry.SCHEDULE_ACTIVITY_NAME, sched.getActivityName());
            values.put(ScheduleEntry.SCHEDULE_START_TIME, sched.getStartTime().getTime());
            values.put(ScheduleEntry.SCHEDULE_END_TIME, sched.getStartTime().getTime());
            values.put(ScheduleEntry.SCHEDULE_GROUP, sched.getGroup());
            values.put(ScheduleEntry.SCHEDULE_CLASS_ROOM, sched.getGroup());
            values.put(ScheduleEntry.SCHEDULE_TEACHER, sched.getTeacher());

            db.insert(ScheduleEntry.TABLE_NAME, null, values);
        }

        saveLastQueryTime(LAST_SAVED_CALENDAR_ID + name);
    }
}
