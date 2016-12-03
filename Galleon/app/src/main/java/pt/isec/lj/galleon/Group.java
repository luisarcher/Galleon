package pt.isec.lj.galleon;

/**
 * Created by luism on 02/12/2016.
 */

public class Group {
    int id;
    int userId;
    String groupName;
    String createdAt;

    public Group(int id, int uid, String n, String ca){
        this.id = id;
        userId = uid;
        groupName = n;
        createdAt = ca;
    }
}
