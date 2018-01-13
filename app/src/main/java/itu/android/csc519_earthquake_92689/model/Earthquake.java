package itu.android.csc519_earthquake_92689.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yee on 8/20/17.
 */

public class Earthquake implements Parcelable {
    private String mag;
    private String place;
    private String placeFull;
    //in miles
    private String depth;
    private String detailURL;
    private String webURL;
    private String distance;
    private String time;
    private String update;
    private String magType;
    private String sig;
    private double longitude;
    private double latitude;

    public Earthquake(String mag, String place, String placeFull, String depth, String detailURL, String webURL, String
            distance, String time, String update, String magType, String sig, double longitude, double latitude) {
        this.mag = mag;
        this.place = place;
        this.depth = depth;
        this.detailURL = detailURL;
        this.webURL = webURL;
        this.distance = distance;
        this.time = time;
        this.update = update;
        this.magType = magType;
        this.placeFull = placeFull;
        this.sig = sig;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    private Earthquake(Parcel in) {
        mag = in.readString();
        place = in.readString();
        depth = in.readString();
        detailURL = in.readString();
        webURL = in.readString();
        distance = in.readString();
        time = in.readString();
        update = in.readString();
        magType = in.readString();
        placeFull = in.readString();
        sig = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<Earthquake> CREATOR = new Creator<Earthquake>() {
        @Override
        public Earthquake createFromParcel(Parcel in) {
            return new Earthquake(in);
        }

        @Override
        public Earthquake[] newArray(int size) {
            return new Earthquake[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mag);
        dest.writeString(place);
        dest.writeString(depth);
        dest.writeString(detailURL);
        dest.writeString(webURL);
        dest.writeString(distance);
        dest.writeString(time);
        dest.writeString(update);
        dest.writeString(magType);
        dest.writeString(placeFull);
        dest.writeString(sig);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }

    public void setMag(String mag) {
        this.mag = mag;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setPlaceFull(String placeFull) {
        this.placeFull = placeFull;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public void setDetailURL(String detailURL) {
        this.detailURL = detailURL;
    }

    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public void setMagType(String magType) {
        this.magType = magType;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getMag() {
        return mag;
    }

    public String getPlace() {
        return place;
    }

    public String getPlaceFull() {
        return placeFull;
    }

    public String getDepth() {
        return depth;
    }

    public String getDetailURL() {
        return detailURL;
    }

    public String getWebURL() {
        return webURL;
    }

    public String getDistance() {
        return distance;
    }

    public String getTime() {
        return time;
    }

    public String getUpdate() {
        return update;
    }

    public String getMagType() {
        return magType;
    }

    public String getSig() {
        return sig;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
