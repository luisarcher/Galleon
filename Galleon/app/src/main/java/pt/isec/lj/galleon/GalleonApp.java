package pt.isec.lj.galleon;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by luism on 02/12/2016.
 */

public class GalleonApp extends Application{
    protected ArrayList<Group> groups;

    @Override
    public void onCreate() {
        super.onCreate();
        groups = new ArrayList<Group>();
    }

    public void addGroup(Group g)
    {
        groups.add(g);
    }
}
