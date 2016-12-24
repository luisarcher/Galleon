package pt.isec.lj.galleon.models;

/**
 * Created by luism on 23/12/2016
 */

public class User {
    private int id;
    private String name;
    private String email;
    private String api_key;

    public User (int id, String n, String e, String api){
        this.id = id;
        this.name = n;
        this.email = e;
        this.api_key = api;
    }

    public int getUserId(){
        return id;
    }

    public String getUserName(){
        return name;
    }

    public String getUserEmail(){
        return email;
    }
}
