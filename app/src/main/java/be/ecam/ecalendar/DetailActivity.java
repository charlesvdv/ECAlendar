package be.ecam.ecalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Schedule schedule=(Schedule) intent.getParcelableExtra("schedule");

        TextView titleTextView =(TextView) this.findViewById(R.id.title);
        TextView timeTextView =(TextView) this.findViewById(R.id.time);
        TextView classroomTextView =(TextView) this.findViewById(R.id.classroom);
        TextView teacherTextView =(TextView) this.findViewById(R.id.teacher);


        titleTextView.setText(schedule.getActivityName());
        timeTextView.setText(schedule.getStartTime().toString() + " - " + schedule.getEndTime().toString());
        classroomTextView.setText(schedule.getClassRoom());
        teacherTextView.setText(schedule.getTeacher());
    }
}
