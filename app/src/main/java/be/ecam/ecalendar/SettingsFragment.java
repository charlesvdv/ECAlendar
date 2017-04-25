package be.ecam.ecalendar;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Antoi on 21/03/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat
        implements CalendarDAO.CalendarDataUpdated {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_settings);

        CalendarDAO dao = CalendarDAO.createSingleton(getActivity(), this);
        dao.getCalendarTypes();
    }

    @Override
    public void notifySchedulesChange(String name, ArrayList<Schedule> schedules) {
        //Nothing to do
    }

    @Override
    public void notifyCalendarTypesChanges(HashMap<String, ArrayList<CalendarType>> types) {
        Log.d("grgtgtrg", "czlledd!!");
        ArrayList<CalendarType> entries = new ArrayList<>();
        for (Map.Entry<String, ArrayList<CalendarType>> typeList : types.entrySet()) {
            entries.addAll(typeList.getValue());
        }

        CharSequence[] calendarType = entries.toArray(new CharSequence[entries.size()]);

        ListPreference listPreference = (ListPreference) findPreference("pref_section");

        CharSequence[] test = { "English", "French"};

        listPreference.setEntries(test);
        listPreference.setEntryValues(test);
    }
}
