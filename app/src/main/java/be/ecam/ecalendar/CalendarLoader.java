package be.ecam.ecalendar;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by charles on 3/21/17.
 */

public class CalendarLoader extends IntentService {
    static final String TAG = CalendarLoader.class.getSimpleName();

    private static final int TIMEOUT = 20;

    static final String BASE_URL = "http://calendar.ecam.be/list";
    static final String ICS_URL = "http://calendar.ecam.be/ics/";
    static final String TEACHERS_URL = BASE_URL + "/p";
    static final String CLASSROOMS_URL = BASE_URL + "/a";
    static final String GROUPS_URL = BASE_URL + "/h";
    static final String STUDENTS_URL = BASE_URL + "/e";

    RequestQueue requestQueue;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CalendarLoader(String name) {
        super(name);
    }

    public CalendarLoader() {
        this(CalendarLoader.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        requestQueue = Volley.newRequestQueue(this);

        Intent returnIntent = new Intent(this, CalendarLoaderReceiver.class);
        try {
            switch(intent.getStringExtra("action")) {
                case "schedule":
                    String name = intent.getStringExtra("name");
                    ArrayList<Schedule> schedules = getSchedules(name);

                    returnIntent.putExtra("action", "schedule");
                    returnIntent.putExtra("name", name);

                    returnIntent.putParcelableArrayListExtra("schedules", schedules);
                    break;

                case "type":
                    ArrayList<CalendarType> teachers = getCalendarType(TEACHERS_URL, "trigens", "npens");
                    ArrayList<CalendarType> classrooms = getCalendarType(CLASSROOMS_URL, "abraud", "nomaud");
                    ArrayList<CalendarType> groups = getCalendarType(GROUPS_URL, "abrser", "demisérie");
                    // Could be handled when we have a proper API...
                    // ArrayList<CalendarType> students = getCalendarType(STUDENTS_URL);

                    returnIntent.putExtra("action", "type");
                    returnIntent.putParcelableArrayListExtra("teachers", teachers);
                    returnIntent.putParcelableArrayListExtra("classrooms", classrooms);
                    returnIntent.putParcelableArrayListExtra("groups", groups);
                    break;

                default:
                    Log.d(TAG, "Could not match this action.");
            }
        } catch (NetworkException | ICSParsingException e) {
            returnIntent.putExtra("action", "error");
            returnIntent.putExtra("message", e.getMessage());
            Log.d(TAG, "Error send");
        }
        //LocalBroadcastManager.getInstance(this).sendBroadcast(returnIntent);
        sendBroadcast(returnIntent);
    }

    private ArrayList<CalendarType> getCalendarType(String url, String idKey, String descriptionKey) throws NetworkException {
        // Synchronous request.
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(url, future, future);
        requestQueue.add(request);

        ArrayList<CalendarType> types = new ArrayList<>();
        try {
            JSONArray response = future.get(TIMEOUT, TimeUnit.SECONDS);
            for (int i = 0; i < response.length(); i++) {
                JSONObject data = (JSONObject) response.get(i);
                types.add(new CalendarType(
                        data.getString(idKey),
                        data.getString(descriptionKey)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NetworkException(e.getCause());
        }

        return types;
    }

    private ArrayList<Schedule> getSchedules(String name) throws ICSParsingException, NetworkException {
        // Synchronous request.
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(ICS_URL + name, future, future);
        requestQueue.add(request);

        ArrayList<Schedule> schedules = new ArrayList<>();
        try {
            // Parse the ICS data.
            StringReader reader = new StringReader(future.get(TIMEOUT, TimeUnit.SECONDS));
            Calendar calendar = new CalendarBuilder().build(reader);
            // Iterate over event data
            for (Object o : calendar.getComponents()) {
                Component component = (Component) o;

                // Pass if we don't have a calendar event
                if (! (component instanceof VEvent)) {
                    continue;
                }

                VEvent event = (VEvent) component;
                try {
                    schedules.add(convertVEventToSchedule(name, event));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ICSParsingException(e.getCause());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NetworkException(e.getCause());
        }
        return schedules;
    }

    private Schedule convertVEventToSchedule(String calendarName, VEvent event) {
        String id = event.getSummary().getValue();
        String classRoom = "";
        if (event.getLocation() != null) {
            classRoom = event.getLocation().getValue();
        }

        Date startDate = event.getStartDate().getDate();
        Date endDate = event.getEndDate().getDate();

        String description = event.getDescription().getValue();
        String[] descriptionValue = description.split("(Act:)|(\nGroupe:)|(\nEns:)");

        String name = descriptionValue[1];
        String group = descriptionValue[2];
        String teacher = "";
        if (descriptionValue.length == 4) {
            teacher = descriptionValue[3];
        }
        Log.d(TAG, classRoom+ " " + group);
        return new Schedule(id, calendarName, startDate, endDate, name, group, teacher, classRoom);
    }

    // Define custom exceptions.
    class NetworkException extends Exception {
        public NetworkException(Throwable throwable) {
            super("Could not load data", throwable);
        }
    }

    class ICSParsingException extends Exception {
        public ICSParsingException(Throwable throwable) {
            super("Error while parsing ICS", throwable);
        }
    }
}
