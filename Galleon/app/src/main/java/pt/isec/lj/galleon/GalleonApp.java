package pt.isec.lj.galleon;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by luism on 02/12/2016.
 */

public class GalleonApp extends Application{
    private ArrayList<Group> groups;
    private ArrayList<String> events;

    @Override
    public void onCreate() {
        super.onCreate();
        groups = new ArrayList<>();
        events = new ArrayList<>();
    }

    public void addGroup(Group g){
        groups.add(g);
    }

    public ArrayList<String> getEvents(){
        return events;
    }
}
