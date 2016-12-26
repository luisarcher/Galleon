package pt.isec.lj.galleon.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by luism on 02/12/2016
 */

public class Group {
    private int groupId;
    private int userId;
    private String groupName;
    private String groupCat;
    private String createdAt;

    private ArrayList<Event> events;

    public Group(int id, int uid, String n, String cat, String ca){
        groupId = id;
        userId = uid;
        groupName = n;
        groupCat = cat;
        createdAt = ca;

        events = new ArrayList<>();
    }

    public Group(JSONObject group){
        try {
            userId = -1;
            groupId = group.getInt("id");
            groupName = group.getString("groupname");
            groupCat = group.getString("groupnat");
            createdAt = group.getString("createdat");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        events = new ArrayList<>();
    }

    public int getId(){
        return groupId;
    }

    public String getGroupName(){
        return groupName;
    }

    public String getGroupCat(){
        return groupCat;
    }

    public ArrayList<Event> getEvents(){
        return events;
    }

    public void addEvent(Event e){
        events.add(e);
    }

    public int getNumEvents(){
        return events.size();
    }

}
