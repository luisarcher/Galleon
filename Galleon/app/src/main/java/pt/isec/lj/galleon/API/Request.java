package pt.isec.lj.galleon.API;

import org.json.JSONObject;

public abstract class Request {
    protected int responseCode;
    protected String message;
    protected JSONObject jsonResult;
    protected String baseUrl = "http://139.59.164.139/v1";

    public int getResponseCode(){
        return responseCode;
    }

    public String getMessage(){
        return message;
    }

    public JSONObject getJsonResult(){
        return jsonResult;
    }
}
