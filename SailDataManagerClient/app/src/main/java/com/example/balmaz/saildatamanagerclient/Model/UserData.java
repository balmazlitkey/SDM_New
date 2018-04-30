package com.example.balmaz.saildatamanagerclient.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by balmaz on 2018. 04. 15..
 */

public class UserData implements Parcelable{

    private String instrumentID;
    private String timestamp;
    private double speed;
    private int temp;
    private double lat;
    private double lon;
    private double windSpeed;
    private int windDirection;
    private int heading;
    private int batteryLevel;

    public UserData(String instrumentID, String timestamp, double speed, int temp, double lat, double lon, double windSpeed, int windDirection, int heading, int batteryLevel) {
        this.instrumentID = instrumentID;
        this.timestamp = timestamp;
        this.speed = speed;
        this.temp = temp;
        this.lat = lat;
        this.lon = lon;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.heading = heading;
        this.batteryLevel = batteryLevel;
    }

    public UserData() {
        //required empty constructor
    }

    protected UserData(Parcel in) {
        instrumentID = in.readString();
        timestamp = in.readString();
        speed = in.readDouble();
        temp = in.readInt();
        lat = in.readDouble();
        lon = in.readDouble();
        windSpeed = in.readDouble();
        windDirection = in.readInt();
        heading = in.readInt();
        batteryLevel = in.readInt();
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    public String getInstrumentID() {
        return instrumentID;
    }

    public void setInstrumentID(String instrumentID) {
        this.instrumentID = instrumentID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "instrumentID='" + instrumentID + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", speed=" + speed +
                ", temp=" + temp +
                ", lat=" + lat +
                ", lon=" + lon +
                ", windSpeed=" + windSpeed +
                ", windDirection=" + windDirection +
                ", heading=" + heading +
                ", batteryLevel=" + batteryLevel +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(instrumentID);
        parcel.writeString(timestamp);
        parcel.writeDouble(speed);
        parcel.writeInt(temp);
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);
        parcel.writeDouble(windSpeed);
        parcel.writeInt(windDirection);
        parcel.writeInt(heading);
        parcel.writeInt(batteryLevel);
    }
}
