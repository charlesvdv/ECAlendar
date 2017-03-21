package be.ecam.ecalendar;

/**
 * Created by Antoi on 21/03/2017.
 */

public class Schedule {
    private String activityId;
    private String startTime;
    private String endTime;
    private String activityName;
    private String group;
    private String teacher;
    private String note;

    Schedule(String id, String start, String end, String name, String group,
                String teacher, String note) {
        this.activityId = id;
        this.startTime = start;
        this.endTime = end;
        this.activityName = name;
        this.group = group;
        this.teacher = teacher;
        this.note = note;
    }

    public String getActivityId() {
        return activityId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
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

    public String getNote() {
        return note;
    }
}
