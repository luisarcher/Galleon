package pt.isec.lj.galleon.API;

import org.json.JSONObject;

public abstract class Request {
    int responseCode;
    boolean error;
    String message;
    JSONObject jsonResult;
    static final String baseUrl = "http://139.59.164.139/v1";

    public int getResponseCode(){
        return responseCode;
    }

    public String getMessage(){
        return message;
    }

    public JSONObject getJsonResult(){
        return jsonResult;
    }

    public boolean isError(){
        return error;
    }
}
