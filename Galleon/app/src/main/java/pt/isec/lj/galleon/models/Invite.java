package pt.isec.lj.galleon.models;

import java.io.Serializable;

/**
 * Created by luism on 29/12/2016
 */

public class Invite implements Serializable{

    static final long serialVersionUID = 1010L;

    private Event event;
    private String server_api;

    public Invite(Event e, String api){
        event = e;
        server_api = api;
    }

    public Event getEvent(){
        return event;
    }

    public String getApi(){
        return server_api;
    }
}
