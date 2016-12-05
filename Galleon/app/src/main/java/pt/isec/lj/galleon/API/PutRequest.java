package pt.isec.lj.galleon.API;

/**
 * Created by luism on 05/12/2016.
 */

public class PutRequest extends PostRequest {
    public PutRequest(String requestUrl, String queryParams){
        super("PUT",requestUrl,queryParams);
    }
}
