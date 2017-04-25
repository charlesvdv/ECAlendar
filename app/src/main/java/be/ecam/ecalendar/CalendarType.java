package be.ecam.ecalendar;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Antoi on 21/03/2017.
 * //id, type description
 */

public class CalendarType implements Parcelable {
    private String id = null;
    private String description = null;

    CalendarType(String id, String description) {
        this.description = description;
        this.id = id;
    }

    protected CalendarType(Parcel in) {
        id = in.readString();
        description = in.readString();
    }

    public String getId() { return id;}
    public String getDescription() { return description;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
    }

    public static final Creator<CalendarType> CREATOR = new Creator<CalendarType>() {
        @Override
        public CalendarType createFromParcel(Parcel in) {
            return new CalendarType(in);
        }

        @Override
        public CalendarType[] newArray(int size) {
            return new CalendarType[size];
        }
    };
}
