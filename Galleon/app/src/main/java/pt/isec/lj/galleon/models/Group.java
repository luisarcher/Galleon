package pt.isec.lj.galleon.models;

/**
 * Created by luism on 02/12/2016.
 */

public class Group {
    private int id;
    private int userId;
    private String groupName;
    private String createdAt;

    public Group(int id, int uid, String n, String ca){
        this.id = id;
        userId = uid;
        groupName = n;
        createdAt = ca;
    }
}
