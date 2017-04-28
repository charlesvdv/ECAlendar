package be.ecam.ecalendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Arrays;

public class SearchActivity extends AppCompatActivity implements CalendarDAO.CalendarDataUpdated{

    ArrayAdapter<String> adapter;
    CalendarDAO dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dao = CalendarDAO.getSingleton(this);
        dao.getCalendarTypes();

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.searchMenu);

        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void notifySchedulesChange(String name, ArrayList<Schedule> schedules) {
        //nothing to do
    }

    @Override
    public void notifyCalendarTypesChanges(HashMap<String, ArrayList<CalendarType>> types) {
        ArrayList<String> entries = new ArrayList<>();
        for (Map.Entry<String, ArrayList<CalendarType>> typeList : types.entrySet()) {
            for (CalendarType ct : typeList.getValue()) {
                entries.add(ct.getId());
            }
        }

        ListView lv = (ListView)findViewById(R.id.listViewType);

        adapter = new ArrayAdapter<>(SearchActivity.this,
                android.R.layout.simple_list_item_1,
                entries);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String calendarId = ((TextView)view).getText().toString();
                String item = "Vous avez selectionné : ";
                item += calendarId;

                SharedPreferences sharedPreferences = getSharedPreferences("other_section",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("id", calendarId);

                editor.commit();

                dao.getCalendar(calendarId);

                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO : Usefull in the futur ?
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
    */
}
