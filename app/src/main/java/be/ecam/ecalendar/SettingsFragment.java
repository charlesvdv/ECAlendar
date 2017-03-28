package be.ecam.ecalendar;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;


/**
 * Created by Antoi on 21/03/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_settings);

    }
}
