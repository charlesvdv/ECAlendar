package be.ecam.ecalendar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import be.ecam.ecalendar.CalendarContract.CalendarTypeEntry;
import be.ecam.ecalendar.CalendarContract.ScheduleEntry;

/**
 * Created by charles on 3/28/17.
 */

public class CalendarDAO {
    private static final String TAG = CalendarDAO.class.getSimpleName();
    private static final String LAST_SAVED_PREF_FILE_KEY = "last_saved_query";
    private static final String LAST_SAVED_TYPES_ID = "types";
    private static final String LAST_SAVED_CALENDAR_ID = "calendar-";

    // In milliseconds.
    private static final int TIME_BEFORE_RELOAD = 1000 * 60 * 60 * 24 * 7;

    private static CalendarDAO singleton;

    private Context context;
    private ArrayList<CalendarDataUpdated> notifiers;

    private CalendarDBHelper dbHelper;
    private SQLiteDatabase db;

    private HashMap<String, ArrayList<Schedule>> calendars;
    private HashMap<String, ArrayList<CalendarType>> types;

    private long lastSavedTimeTypes;
    private SharedPreferences lastTimePref;

    private CalendarDAO(Context context, CalendarDataUpdated notifier) {
        this.context = context;

        this.notifiers = new ArrayList<>();
        this.notifiers.add(notifier);

        dbHelper = new CalendarDBHelper(context);
        db = dbHelper.getWritableDatabase();

        calendars = new HashMap<>();
        types = new HashMap<>();
        loadTypesFromDB();

        lastTimePref = context.getSharedPreferences(LAST_SAVED_PREF_FILE_KEY,
                Context.MODE_PRIVATE);
    }

    public static CalendarDAO createSingleton(Context context, CalendarDataUpdated notifier) {
        if (singleton != null) {
            return singleton;
        }
        singleton = new CalendarDAO(context, notifier);
        return singleton;
    }

    public static CalendarDAO getSingleton() {
        return singleton;
    }

    public static CalendarDAO getSingleton(CalendarDataUpdated notifier) {
        singleton.addNotifier(notifier);
        return singleton;
    }

    public interface CalendarDataUpdated {
        void notifySchedulesChange(String name, ArrayList<Schedule> schedules);
        void notifyCalendarTypesChanges(HashMap<String, ArrayList<CalendarType>> types);
    }

    public void addNotifier(CalendarDataUpdated notifier) {
        notifiers.add(notifier);
    }

    public HashMap<String, ArrayList<CalendarType>> getCalendarTypes() {
        long current = new Date().getTime();
        long lastSaved = lastTimePref.getLong(LAST_SAVED_TYPES_ID, 0);

        if (types.isEmpty()) {
            if (current - lastSaved > TIME_BEFORE_RELOAD) {
                downloadTypesData();
            } else {
                loadTypesFromDB();
            }
        }

        for (CalendarDataUpdated noti : notifiers) {
            noti.notifyCalendarTypesChanges(types);
        }
        return types;
    }

    public ArrayList<Schedule> getCalendar(String name) {
        long lastSaved = lastTimePref.getLong(LAST_SAVED_CALENDAR_ID + name, 0);
        long current = new Date().getTime();

        if (calendars.get(name) == null) {
            calendars.put(name, new ArrayList<Schedule>());
        }
        if (calendars.get(name).isEmpty()) {
            if (current - lastSaved > TIME_BEFORE_RELOAD) {
                downloadCalendar(name);
            } else {
                loadCalendarFromDB(name);
            }
        }

        for (CalendarDataUpdated noti : notifiers) {
            noti.notifySchedulesChange(name, calendars.get(name));
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
        while (cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndex(CalendarTypeEntry.CALENDAR_TYPE_TYPE));
            String id = cursor.getString(cursor.getColumnIndex(CalendarTypeEntry.CALENDAR_TYPE_ID));
            String desc = cursor.getString(cursor.getColumnIndex(CalendarTypeEntry.CALENDAR_TYPE_DESCRIPTION));

            if (types.get(type) == null) {
                types.put(type, new ArrayList<CalendarType>());
            }
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
                ScheduleEntry.SCHEDULE_CALENDAR + "=?", new String[] {name}, null, null, null);
        while (cursor.moveToNext()) {
            calendars.get(name).add(new Schedule(
                    cursor.getString(cursor.getColumnIndex(ScheduleEntry.SCHEDULE_CALENDAR)),
                    name,
                    new Date(cursor.getLong(cursor.getColumnIndex(ScheduleEntry.SCHEDULE_START_TIME))),
                    new Date(cursor.getLong(cursor.getColumnIndex(ScheduleEntry.SCHEDULE_END_TIME))),
                    cursor.getString(cursor.getColumnIndex(ScheduleEntry.SCHEDULE_ACTIVITY_NAME)),
                    cursor.getString(cursor.getColumnIndex(ScheduleEntry.SCHEDULE_GROUP)),
                    cursor.getString(cursor.getColumnIndex(ScheduleEntry.SCHEDULE_TEACHER)),
                    cursor.getString(cursor.getColumnIndex(ScheduleEntry.SCHEDULE_CLASS_ROOM))
            ));
        }
    }

    private void saveLastQueryTime(String id) {
        SharedPreferences.Editor editor = lastTimePref.edit();
        editor.putLong(id, new Date().getTime());
        editor.commit();
    }

    public void updateCalendarType(HashMap<String, ArrayList<CalendarType>> types) {
        // Update in-memory representation.
        this.types = types;
        // Delete all data associated to calendar type.
        db.delete(CalendarTypeEntry.TABLE_NAME, null, null);

        for (Map.Entry<String, ArrayList<CalendarType>> entry : types.entrySet()) {
            String type = entry.getKey();

            for (CalendarType typeData : removeDuplicate(entry.getValue())) {
                ContentValues values = new ContentValues();
                values.put(CalendarTypeEntry.CALENDAR_TYPE_ID, typeData.getId());
                values.put(CalendarTypeEntry.CALENDAR_TYPE_DESCRIPTION, typeData.getDescription());
                values.put(CalendarTypeEntry.CALENDAR_TYPE_TYPE, type);

                db.insert(CalendarTypeEntry.TABLE_NAME, null, values);
            }
        }

        saveLastQueryTime(LAST_SAVED_TYPES_ID);
    }

    private ArrayList<CalendarType> removeDuplicate(ArrayList<CalendarType> types) {
        ArrayList<CalendarType> newData = new ArrayList<>();
        for (CalendarType type : types) {
            boolean found = false;
            for (CalendarType data : newData) {
                if (type.getId().equals(data.getId())) {
                    found = true;
                }
            }
            if (! found) {
                newData.add(type);
            }
        }

        return newData;
    }

    public void updateCalendar(String name, ArrayList<Schedule> schedules) {
        // Update in-memory data
        calendars.put(name, schedules);

        // Remove previously saved calendar data with the name.
        db.delete(ScheduleEntry.TABLE_NAME,
                ScheduleEntry.SCHEDULE_CALENDAR + "=?", new String[] { name });

        for (Schedule sched : schedules) {
            ContentValues values = new ContentValues();
            values.put(ScheduleEntry.SCHEDULE_ACTIVITY_ID, sched.getActivityId());
            values.put(ScheduleEntry.SCHEDULE_CALENDAR, name);
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