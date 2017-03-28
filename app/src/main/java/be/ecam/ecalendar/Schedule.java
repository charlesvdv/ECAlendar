package be.ecam.ecalendar;

import java.util.Date;

/**
 * Created by Antoi on 21/03/2017.
 */

public class Schedule {
    private String activityId;
    private Date startTime;
    private Date endTime;
    private String activityName;
    private String group;
    private String teacher;
    private String classRoom;

    Schedule(String id, Date start, Date end, String name, String group,
             String teacher, String note) {
        this.activityId = id;
        this.startTime = start;
        this.endTime = end;
        this.activityName = name;
        this.group = group;
        this.teacher = teacher;
        this.classRoom = note;
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
}