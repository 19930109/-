package jp.javadrive.openweatherapifragmentsqlite;

import android.os.Parcel;
import android.os.Parcelable;

public class Weatherday implements Parcelable {


    public String cityname;
    public String day;
    public String pop;
    public String temp;
    public String weather;
    public String icon;

    public Weatherday(String day,String pop,String temp,String weather,String icon,String cityname) {

        this.cityname = cityname;
        this.day = day;
        this.pop = pop;
        this.temp = temp;
        this.weather = weather;
        this.icon = icon;

    }

    public Weatherday(Parcel in){


        this.cityname = in.readString();
        this.day = in.readString();
        this.pop = in.readString();
        this.temp = in.readString();
        this.weather = in.readString();
        this.icon = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){

        dest.writeString(cityname);
        dest.writeString(day);
        dest.writeString(pop);
        dest.writeString(temp);
        dest.writeString(weather);
        dest.writeString(icon);
    }

    @Override
    public int describeContents() {
        return 0;
    }//FileDescriptor未使用の場合は0

    // 今は定型文という認識で
    public static final Creator<Weatherday> CREATOR = new Creator<Weatherday>() {
        public Weatherday createFromParcel(Parcel in) {
            return new Weatherday(in);
        }

        public Weatherday[] newArray(int size) {
            return new Weatherday[size];
        }
    };

}


