package be.ecam.ecalendar;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.component.VEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by charles on 3/21/17.
 */

public class CalendarLoader extends IntentService {
    static final String TAG = CalendarLoader.class.getSimpleName();

    public static final String BROADCAST_ACTION = "calendar_loaded";

    static final String BASE_URL = "calendar.ecam.be/list";
    static final String ICS_URL = "calendar.ecam.be/ics/";
    static final String TEACHERS_URL = BASE_URL + "/p";
    static final String CLASSROOMS_URL = BASE_URL + "/a";
    static final String GROUPS_URL = BASE_URL + "/h";
    static final String STUDENTS_URL = BASE_URL + "/e";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CalendarLoader(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent returnIntent = new Intent(BROADCAST_ACTION);
        switch(intent.getStringExtra("action")) {
            case "schedule":
                String name = intent.getStringExtra("name");
                ArrayList<Schedule> schedules = getSchedules(name);

                returnIntent.putExtra("action", "schedule");
                returnIntent.putExtra("schedules", schedules);
                returnIntent.putExtra("name", name);
                break;

            case "type":
                ArrayList<CalendarType> teachers = getCalendarType(TEACHERS_URL, "trigens", "npens");
                ArrayList<CalendarType> classrooms = getCalendarType(CLASSROOMS_URL, "abraud", "nomaud");
                ArrayList<CalendarType> groups = getCalendarType(GROUPS_URL, "abrser", "demisérie");
                // Could be handled when we have a proper API...
                // ArrayList<CalendarType> students = getCalendarType(STUDENTS_URL);

                returnIntent.putExtra("action", "type");
                returnIntent.putExtra("teachers", teachers);
                returnIntent.putExtra("classrooms", classrooms);
                returnIntent.putExtra("groups", groups);
                break;

            default:
                Log.d(TAG, "Could not match this action.");
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(returnIntent);
    }

    private ArrayList<CalendarType> getCalendarType(String url, String idKey, String descriptionKey) {
        // Synchronous request.
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(url, future, future);

        ArrayList<CalendarType> types = new ArrayList<>();
        try {
            JSONArray response = future.get();
            for (int i = 0; i < response.length(); i++) {
                JSONObject data = (JSONObject) response.get(i);
                types.add(new CalendarType(
                        data.getString(idKey),
                        data.getString(descriptionKey)
                ));
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        return types;
    }

    private ArrayList<Schedule> getSchedules(String name) {
        // Synchronous request.
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(ICS_URL + name, future, future);

        ArrayList<Schedule> schedules = new ArrayList<>();
        try {
            // Parse the ICS data.
            StringReader reader = new StringReader(future.get());
            Calendar calendar = new CalendarBuilder().build(reader);

            // Iterate over event data
            for (Object o : calendar.getComponents()) {
                Component component = (Component) o;

                // Pass if we don't have a calendar event
                if (! (component instanceof VEvent)) {
                    continue;
                }

                VEvent event = (VEvent) component;
                schedules.add(convertVEventToSchedule((event)));
            }
        } catch (InterruptedException | ExecutionException | ParserException | IOException e) {
            e.printStackTrace();
        }

        return schedules;
    }

    private Schedule convertVEventToSchedule(VEvent event) {
        String id = event.getSummary().getValue();
        String classRoom = event.getLocation().getValue();

        Date startDate = new Date();
        Date endDate = new Date();
        event.getConsumedTime(startDate, endDate);

        ParameterList parameters = event.getDescription().getParameters();
        String teacher = parameters.getParameter("Ens").getValue();
        String name = parameters.getParameter("Act").getValue();
        String group = parameters.getParameter("Groupe").getValue();

        return new Schedule(id, startDate, endDate, name, group, teacher, classRoom);
    }
}
