package pt.isec.lj.galleon.API;

/**
 * Created by luism on 05/12/2016
 */

public class PutRequest extends PostRequest {
    public PutRequest(String requestUrl, String queryParams, String api_key){
        super("PUT",requestUrl,queryParams,api_key);
    }
}
