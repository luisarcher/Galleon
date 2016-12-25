package pt.isec.lj.galleon.API;

import org.json.JSONObject;

public abstract class Request {

    static final String baseUrl = "http://139.59.164.139/v1";
    String api_key;

    int responseCode;
    boolean error;
    String message;
    JSONObject jsonResult;

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
