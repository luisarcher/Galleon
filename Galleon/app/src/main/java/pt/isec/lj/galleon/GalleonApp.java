package pt.isec.lj.galleon;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

import pt.isec.lj.galleon.models.Group;

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

    public void saveLoginData(String login){
        return;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
