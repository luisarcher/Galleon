package pt.isec.lj.galleon;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import pt.isec.lj.galleon.models.Event;
import pt.isec.lj.galleon.models.Group;
import pt.isec.lj.galleon.models.User;

/**
 * Created by luism on 02/12/2016
 */

public class GalleonApp extends Application{
    private User currentUser;
    private Group currentGroup;
    private Event currentEvent;
    //private ArrayList<Group> groups;


    @Override
    public void onCreate() {
        super.onCreate();


        /*events.add(new Event("evento1"));
        events.add(new Event("evento2"));
        events.add(new Event("evento3"));
        events.add(new Event("evento4"));
        events.add(new Event("evento5"));
        events.add(new Event("evento6"));*/
    }

    /*public void addGroup(Group g){
        groups.add(g);
    }*/

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public User getCurrentUser(){
        return currentUser;
    }

    public void setUser(User user){
        currentUser = user;
    }

    public boolean isSetUser(){
        return (currentUser != null);
    }

    public SharedPreferences getSharedPreferencesSess(){
        return getApplicationContext().getSharedPreferences("sess", MODE_PRIVATE);
    }

    public void setSharedPreferencesSess(int id, String email, String passwd){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("sess", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("userId", id);
        editor.putString("email", email);
        editor.putString("passwd", passwd);
        editor.apply();
    }
    public boolean isSetSharedPreferencesSessId(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("sess", MODE_PRIVATE);
        return (pref.contains("userId") && pref.getInt("userId",0) != 0);
    }

    public void unsetSharedPreferencesSess(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("sess", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("userId");
        editor.remove("email");
        editor.remove("passwd");
        editor.apply();
    }

    public void setGroup(Group g){
        currentGroup = g;
    }

    public Group getGroup(){
        return currentGroup;
    }

    public void setCurrentEvent(Event e){
        currentEvent = e;
    }

    public Event getCurrentEvent(){
        return currentEvent;
    }
}
