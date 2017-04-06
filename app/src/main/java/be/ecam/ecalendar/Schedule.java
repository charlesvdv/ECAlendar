package be.ecam.ecalendar;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Antoi on 21/03/2017.
 */

public class Schedule implements Parcelable {
    private String calendar;
    private String activityId;
    private Date startTime;
    private Date endTime;
    private String activityName;
    private String group;
    private String teacher;
    private String classRoom;

    Schedule(String id, String calendar, Date start, Date end, String name, String group,
             String teacher, String classRoom) {
        this.activityId = id;
        this.calendar = calendar;
        this.startTime = start;
        this.endTime = end;
        this.activityName = name;
        this.group = group;
        this.teacher = teacher;
        this.classRoom = classRoom;
    }

    protected Schedule(Parcel in) {
        activityId = in.readString();
        calendar = in.readString();
        startTime = new Date(in.readLong());
        endTime = new Date(in.readLong());
        activityName = in.readString();
        group = in.readString();
        teacher = in.readString();
        classRoom = in.readString();
    }

    public String getActivityId() {
        return activityId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getGroup() {
        return group;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public String getCalendar() {
        return calendar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(activityId);
        dest.writeString(calendar);
        dest.writeLong(startTime.getTime());
        dest.writeLong(endTime.getTime());
        dest.writeString(activityName);
        dest.writeString(group);
        dest.writeString(teacher);
        dest.writeString(classRoom);
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };
}