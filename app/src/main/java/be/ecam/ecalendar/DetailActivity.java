package be.ecam.ecalendar;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        Schedule schedule = (Schedule) intent.getParcelableExtra("schedule");

        TextView titleTextView = (TextView) this.findViewById(R.id.title);
        TextView timeTextView = (TextView) this.findViewById(R.id.time);
        TextView classroomTextView = (TextView) this.findViewById(R.id.classroom);
        TextView teacherTextView = (TextView) this.findViewById(R.id.teacher);
        TextView groupTextView = (TextView) this.findViewById(R.id.group);

        titleTextView.setText(schedule.getActivityName());
        SimpleDateFormat dt = new SimpleDateFormat("hh:mm dd/mmm/yy", Locale.FRANCE);
        timeTextView.setText(dt.format(schedule.getStartTime()) + " - " +
                dt.format(schedule.getEndTime()));
        classroomTextView.setText(schedule.getClassRoom());
        teacherTextView.setText(schedule.getTeacher());
        groupTextView.setText(schedule.getGroup());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}