package pt.isec.lj.galleon.API;

import org.json.JSONObject;

public abstract class Request {

    static final String baseUrl = "http://139.59.164.139/v1";
    String api_key;

    String rawResponse;

    int responseCode;
    JSONObject jsonResult;
    boolean error;
    String message;

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

    public int getLength(){
        return jsonResult.length();
    }

    public String getRaw(){
        return rawResponse;
    }
}
