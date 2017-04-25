package be.ecam.ecalendar;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ViewFlipper;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.emory.mathcs.backport.java.util.Arrays;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements CalendarDAO.CalendarDataUpdated, CalendarAdapter.CalendarAdapterOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();

    RecyclerView.LayoutManager layoutManager;
    CalendarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.resultView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CalendarAdapter(this);
        CalendarDAO dao = CalendarDAO.createSingleton(this, this);
        dao.getCalendar("serie_4EI5A");
        dao.getCalendar("serie_4EM2A");
        dao.getCalendarTypes();
        recyclerView.setAdapter(adapter);
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
        adapter.setCalendarData(name, schedules);
        layoutManager.scrollToPosition(adapter.getCalendarPosition());

    }

    @Override
    public void notifyCalendarTypesChanges(HashMap<String, ArrayList<CalendarType>> types) {
        // Do nothings.
    }
}
