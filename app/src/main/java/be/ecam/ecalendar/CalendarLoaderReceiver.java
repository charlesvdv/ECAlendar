package be.ecam.ecalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by charles on 4/2/17.
 */

/**
 * Receive data from CalendarLoader intent service and dispatch it to the DAO.
 */
public class CalendarLoaderReceiver extends BroadcastReceiver {
    private static final String TAG = CalendarLoaderReceiver.class.getSimpleName();
    private CalendarDAO singleton;

    public CalendarLoaderReceiver() {
        singleton = CalendarDAO.getSingleton();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Intent received: "+ intent.getStringExtra("action"));
        switch (intent.getStringExtra("action")) {
            case "schedule":
                String name = intent.getStringExtra("name");
                ArrayList<Schedule> schedules = intent.getParcelableArrayListExtra("schedules");
                singleton.updateCalendar(name, schedules);
                break;
            case "type":
                HashMap<String, ArrayList<CalendarType>> types = new HashMap<>();
                for (String key : new String[]{"teachers", "classrooms", "groups"}) {
                    types.put(key, intent.<CalendarType>getParcelableArrayListExtra(key));
                }
                singleton.updateCalendarType(types);
                break;
            case "error":
                Toast.makeText(context, intent.getStringExtra("message"), Toast.LENGTH_LONG).show();
                break;
        }
    }
}