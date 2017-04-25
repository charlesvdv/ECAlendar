package be.ecam.ecalendar;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.ArrayList;


/**
 * Created by Antoi on 21/03/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat
        implements CalendarDAO.CalendarDataUpdated {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_settings);

        CalendarDAO DAO = new CalendarDAO()
    }

    @Override
    public void notifySchedulesChange(String name, ArrayList<Schedule> schedules) {
        ListPreference listPreference = (ListPreference) findPreference("pref_section");

        CharSequence[] entries = { "English", "French"};
        CharSequence[] entryValues = {"1" , "2"};

        listPreference.setEntries(entries);
        listPreference.setEntryValues(entryValues);
    }
}
