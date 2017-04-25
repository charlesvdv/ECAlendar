package be.ecam.ecalendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.CalendarAdapterOnClickHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.resultView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        CalendarAdapter adapter = new CalendarAdapter(this);
        CalendarDAO dao = CalendarDAO.createSingleton(this, adapter);
        adapter.setCalendarData("serie_4EI5A", dao.getCalendar("serie_4EI5A"));
        adapter.setCalendarData("serie_4EM2A", dao.getCalendar("serie_4EM2A"));
        dao.getCalendarTypes();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.calendar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        if (itemThatWasClicked == R.id.settings) {
            Context context = this;
            Class destinationClass = SettingsActivity.class;
            Intent intent = new Intent(context, destinationClass);
            startActivity(intent);

            return true;
        }

        //Place here others menu items with an if

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Schedule schedule) {
        Intent intent = new Intent (this,DetailActivity.class);
        intent.putExtra("schedule", schedule);
        startActivity(intent);
    }
}
