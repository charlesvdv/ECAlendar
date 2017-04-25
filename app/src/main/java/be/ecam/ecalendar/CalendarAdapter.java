package be.ecam.ecalendar;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by charles on 3/28/17.
 */

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = CalendarAdapter.class.getSimpleName();

    HashMap<String, ArrayList<Schedule>> calendars;
    // An array of all calendars sorted by date.
    ArrayList<Object> sortedSchedules;
    // Color of each calendar.
    HashMap<String, Integer> colorMap;
    int[] availableColor;

    private CalendarAdapterOnClickHandler clickHandler;

    public interface CalendarAdapterOnClickHandler{
        void onClick(Schedule schedule);
    }

    public CalendarAdapter(CalendarAdapterOnClickHandler clickHandler, int[] availableColor) {
        calendars = new HashMap<>();
        sortedSchedules = new ArrayList<>();
        colorMap = new HashMap<>();

        this.clickHandler = clickHandler;
        this.availableColor = availableColor;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        public TextView titleTextView;
        public TextView timeTextView;
        public TextView groupTextView;
        public LinearLayout titleLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
            timeTextView = (TextView) itemView.findViewById(R.id.time);
            groupTextView = (TextView) itemView.findViewById(R.id.group);
            titleLayout = (LinearLayout) itemView.findViewById(R.id.titleLayout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (sortedSchedules.get(pos) instanceof Schedule) {
                clickHandler.onClick((Schedule) sortedSchedules.get(pos));
            }
        }
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;

        public DateViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.day);
        }
    }

    public void setCalendarData(String name, ArrayList<Schedule> calendar) {
        if (calendar == null) {
            // Do nothing because we still don't have any data available.
            return;
        }
        calendars.put(name, calendar);

        ArrayList<Schedule> scheds = new ArrayList<>();
        for (ArrayList<Schedule> cal : calendars.values()) {
            scheds.addAll(cal);
        }

        // Handle color.
        if (! colorMap.containsKey(name)) {
            for (int color : availableColor) {
                if (! colorMap.containsValue(color)) {
                    colorMap.put(name, color);
                    break;
                }
            }
        }

        Collections.sort(scheds, new Comparator<Schedule>() {
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

        sortedSchedules.clear();

        SimpleDateFormat dt = new SimpleDateFormat("dd MMM", Locale.FRANCE);
        Date lastDate = new Date(1);
        for (Schedule sched : scheds) {
            if (! dt.format(lastDate).equals(dt.format(sched.getStartTime()))) {
                sortedSchedules.add(new DateItem(dt.format(sched.getStartTime())));
            }
            sortedSchedules.add(sched);
            lastDate = sched.getStartTime();
        }
    }

    public int getCalendarPosition() {
        long currentTime = new Date().getTime();
        for (int pos = 0; pos < sortedSchedules.size(); pos++) {
            Object item = sortedSchedules.get(pos);
            if (item instanceof Schedule) {
                if (currentTime < ((Schedule) item).getStartTime().getTime()) {
                    return pos;
                }
            }
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (sortedSchedules.get(position) instanceof Schedule) {
            return 0;
        } else if (sortedSchedules.get(position) instanceof DateItem) {
            return 1;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = null;
        switch (viewType) {
            case 0:
                v = inflater.inflate(R.layout.list_item, parent, false);
                return new ItemViewHolder(v);
            case 1:
                v = inflater.inflate(R.layout.list_day, parent, false);
                return new DateViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        Object item = sortedSchedules.get(pos);
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Schedule schedule = (Schedule) item;

            itemViewHolder.titleTextView.setText(schedule.getActivityName());
            Log.d(TAG, schedule.getCalendar());
            itemViewHolder.titleLayout.setBackgroundColor(colorMap.get(schedule.getCalendar()));
            itemViewHolder.timeTextView.setText(schedule.getStartTime().toString() + " - " +
                schedule.getEndTime().toString());
            itemViewHolder.groupTextView.setText(schedule.getGroup());
        } else if (holder instanceof DateViewHolder) {
            DateViewHolder dateViewHolder = (DateViewHolder) holder;
            DateItem date = (DateItem) item;
            dateViewHolder.dateTextView.setText(date.getDate());
        }
    }

    @Override
    public int getItemCount() {
        if (sortedSchedules == null) {
            return 0;
        }
        return sortedSchedules.size();
    }

    private class DateItem {
        private String date;

        public DateItem(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }
    }
}
