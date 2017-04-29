package be.ecam.ecalendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.emory.mathcs.backport.java.util.Arrays;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements CalendarDAO.CalendarDataUpdated, CalendarAdapter.CalendarAdapterOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();

    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    CalendarAdapter adapter;

    SharedPreferences prefs;

    CalendarDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "activity created!");

        recyclerView = (RecyclerView) findViewById(R.id.resultView);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        // recyclerView.setHasFixedSize(true);

        adapter = new CalendarAdapter(this, getResources().getIntArray(R.array.titleColors));
        recyclerView.setAdapter(adapter);

        dao = CalendarDAO.createSingleton(this, this);
        // Preload calendar types.
        dao.getCalendarTypes();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCalendar();
        Log.d(TAG, "resumed!!!");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "destroying activity");

        SharedPreferences other_pref = getSharedPreferences("other_section", Context.MODE_PRIVATE);
        other_pref.edit().remove("id").apply();
        super.onDestroy();
    }

    public void updateCalendar() {
        Log.d(TAG, "Updating Calendar!");

        if (prefs.getString("pref_section", null) != null) {
            adapter.clearCalendar();
            dao.getCalendar(prefs.getString("pref_section", ""));
        } else {
            Toast.makeText(this, "Please set a default calendar in your preference!", Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, "Other section: " + prefs.getString("other_section", "none"));
        SharedPreferences other_pref = getSharedPreferences("other_section", Context.MODE_PRIVATE);
        if (other_pref.getString("id", null) != null) {
            dao.getCalendar(other_pref.getString("id", ""));
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int itemThatWasClicked = item.getItemId();
        Context context = this;
        if (itemThatWasClicked == R.id.settings) {
            Class destinationClass = SettingsActivity.class;
            Intent intent = new Intent(context, destinationClass);
            startActivity(intent);

            return true;
        }
        if (itemThatWasClicked == R.id.search) {
            Class destinationClass = SearchActivity.class;
            Intent intent = new Intent(context, destinationClass);
            startActivity(intent);

            return true;
        }

        //Place here others menu items with an if
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Schedule schedule) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("schedule", schedule);
        startActivity(intent);
    }

    @Override
    public void notifySchedulesChange(String name, ArrayList<Schedule> schedules) {
        Log.d(TAG, "notified!!");
        adapter.setCalendarData(name, schedules);
        layoutManager.scrollToPosition(adapter.getCalendarPosition());
    }

    @Override
    public void notifyCalendarTypesChanges(HashMap<String, ArrayList<CalendarType>> types) {
        // Do nothings.
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adapter.clearCalendar();
        updateCalendar();
    }
}
