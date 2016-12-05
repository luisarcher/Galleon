package pt.isec.lj.galleon.API;

/**
 * Created by luism on 05/12/2016.
 */

public class DeleteRequest extends PostRequest {
    public DeleteRequest(String requestUrl, String queryParams){
        super("DELETE",requestUrl,queryParams);
    }
}
