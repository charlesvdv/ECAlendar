package be.ecam.ecalendar;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by charles on 3/28/17.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private static final String TAG = CalendarAdapter.class.getSimpleName();

    HashMap<String, ArrayList<Schedule>> calendars;
    // An array of all calendars sorted by date.
    ArrayList<Schedule> sortedSchedules;

    private CalendarAdapterOnClickHandler clickHandler;

    public interface CalendarAdapterOnClickHandler{
        void onClick(Schedule schedule);
    }

    public CalendarAdapter(CalendarAdapterOnClickHandler clickHandler) {
        calendars = new HashMap<>();
        sortedSchedules = new ArrayList<>();
        this.clickHandler = clickHandler;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView titleTextView;
        public TextView timeTextView;
        public TextView groupTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
            timeTextView = (TextView) itemView.findViewById(R.id.time);
            groupTextView = (TextView) itemView.findViewById(R.id.group);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            clickHandler.onClick(sortedSchedules.get(pos));
        }
    }

    public void setCalendarData(String name, ArrayList<Schedule> calendar) {
        if (calendar == null) {
            // Do nothing because we still don't have any data available.
            return;
        }
        calendars.put(name, calendar);

        sortedSchedules.clear();
        for (ArrayList<Schedule> cal : calendars.values()) {
            sortedSchedules.addAll(cal);
        }

        Collections.sort(sortedSchedules, new Comparator<Schedule>() {
            @Override
            public int compare(Schedule o1, Schedule o2) {
                long o1starttime = o1.getStartTime().getTime();
                long o1endtime = o1.getEndTime().getTime();
                long o2starttime = o2.getStartTime().getTime();
                long o2endtime = o2.getEndTime().getTime();

                if (o1starttime < o2starttime) {
                    return -1;
                } else if (o1starttime > o2starttime) {
                    return 1;
                } else {
                    if (o1endtime < o2endtime) {
                        return -1;
                    }
                    return 1;
                }
            }
        });
        notifyDataSetChanged();
    }

    public int getCalendarPosition() {
        long currentTime = new Date().getTime();
        for (int pos = 0; pos < sortedSchedules.size(); pos++) {
            if (currentTime < sortedSchedules.get(pos).getStartTime().getTime()) {
                return pos;
            }
        }
        return 0;
    }
    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CalendarAdapter.ViewHolder holder, int pos) {
        Schedule schedule = sortedSchedules.get(pos);

        holder.titleTextView.setText(schedule.getActivityName());
        holder.timeTextView.setText(schedule.getStartTime().toString() + " - " +
            schedule.getEndTime().toString());
        holder.groupTextView.setText(schedule.getGroup());
    }

    @Override
    public int getItemCount() {
        if (sortedSchedules == null) {
            return 0;
        }
        return sortedSchedules.size();
    }
}
