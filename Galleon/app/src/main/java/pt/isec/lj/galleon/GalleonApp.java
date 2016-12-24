package pt.isec.lj.galleon;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import java.util.ArrayList;

import pt.isec.lj.galleon.models.Group;
import pt.isec.lj.galleon.models.User;

/**
 * Created by luism on 02/12/2016
 */

public class GalleonApp extends Application{
    private User currentUser;
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
}
