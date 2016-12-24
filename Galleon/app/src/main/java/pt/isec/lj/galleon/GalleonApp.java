package pt.isec.lj.galleon;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import java.util.ArrayList;

import pt.isec.lj.galleon.models.Event;
import pt.isec.lj.galleon.models.Group;
import pt.isec.lj.galleon.models.User;

/**
 * Created by luism on 02/12/2016
 */

public class GalleonApp extends Application{
    private User currentUser;
    private ArrayList<Group> groups;
    private ArrayList<Event> events;

    @Override
    public void onCreate() {
        super.onCreate();
        groups = new ArrayList<>();
        events = new ArrayList<>();

        events.add(new Event("evento1"));
        events.add(new Event("evento2"));
        events.add(new Event("evento3"));
        events.add(new Event("evento4"));
        events.add(new Event("evento5"));
        events.add(new Event("evento6"));
    }

    public void addGroup(Group g){
        groups.add(g);
    }

    public ArrayList<Event> getEvents(){
        return events;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void setUser(User user){
        currentUser = user;

        SharedPreferences pref = getApplicationContext().getSharedPreferences("sess", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("userId", user.getUserId());
        editor.apply();
    }

    public User getCurrentUser(){
        return currentUser;
    }

    public boolean isSetSharedPreferencesSessId(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("sess", MODE_PRIVATE);
        currentUser = new User(pref.getInt("userId",0),"","","");
        return pref.contains("userId");
    }
}
