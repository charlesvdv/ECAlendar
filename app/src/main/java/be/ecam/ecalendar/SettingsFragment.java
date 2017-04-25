package be.ecam.ecalendar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

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

        CalendarDAO dao = CalendarDAO.getSingleton(this);
        dao.getCalendarTypes();


        ListPreference pref = (ListPreference) findPreference("pref_section");

        pref.setSummary(pref.getValue());

        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object val) {
                ListPreference pref = (ListPreference) findPreference("pref_section");
                pref.setSummary(val.toString());
                String chose = "Votre horaire principal est : " + val.toString();
                Toast.makeText(getActivity(), chose, Toast.LENGTH_LONG).show();

                return true;
            }
        });
    }

    @Override
    public void notifySchedulesChange(String name, ArrayList<Schedule> schedules) {
        //Nothing to do
    }

    @Override
    public void notifyCalendarTypesChanges(HashMap<String, ArrayList<CalendarType>> types) {
        ArrayList<String> entries = new ArrayList<>();
        for (Map.Entry<String, ArrayList<CalendarType>> typeList : types.entrySet()) {
            for (CalendarType ct : typeList.getValue()) {
                entries.add(ct.getId());
            }
        }

        CharSequence[] calendarChose = entries.toArray(new CharSequence[entries.size()]);

        ListPreference listPreference = (ListPreference) findPreference("pref_section");

        listPreference.setEntries(calendarChose);
        listPreference.setEntryValues(calendarChose);
    }

}
