package pt.isec.lj.galleon.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by luism on 24/12/2016
 */

public class Event implements Serializable{

    static final long serialVersionUID = 1010L;

    private int eventId;
    private String name;
    private String description;
    private String location;
    private String date;
    private String time;
    private int groupid;
    private String createdAt;
    private Double latitude;
    private Double longitude;
    private int isPrivate;
    private int sharingAllowed;

    public Event(int id, String n, String desc, String loc, String dt, String t, int gid, String ca, Double lat, Double lon, int priv, int shar){
        this.eventId = id;
        this.name = n;
        this.description = desc;
        this.location = loc;
        this.date = dt;
        this.time = t;
        this.groupid = gid;
        this.createdAt = ca;
        this.latitude = lat;
        this.longitude = lon;
        this.isPrivate = priv;
        this.sharingAllowed = shar;
    }

    public Event(JSONObject event) {
        try
        {
            this.eventId = event.getInt("id");
            this.name = event.getString("name");
            this.description = event.getString("description");
            this.location = event.getString("location");
            this.date = event.getString("eventdate");
            this.time = event.getString("eventtime");
            this.groupid = event.getInt("groupid");
            this.createdAt = event.getString("createdat");
            this.latitude = event.getDouble("latitude");
            this.longitude = event.getDouble("longitude");
            this.isPrivate = event.getInt("isprivate");
            this.sharingAllowed = event.getInt("isshared");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getEventId(){
        return eventId;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime(){
        return time;
    }

    public String getLocation() {
        return location;
    }

    public int getGroupid() {
        return groupid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public boolean isPrivate(){
        return (isPrivate != 0);
    }

    public boolean sharingAllowed(){
        return (sharingAllowed != 0);
    }

    @Override
    public String toString(){
        return name;
    }
}
