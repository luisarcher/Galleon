package pt.isec.lj.galleon.API;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by luism on 05/12/2016
 */

public class GetRequest extends Request {
    public GetRequest(String requestUrl, String api_key){
        this.api_key = api_key;

        sendRequest(requestUrl);
    }

    private void sendRequest(String requestUrl){
        StringBuilder resp = new StringBuilder();
        try {
            URL url = new URL(baseUrl + requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milisegundos */);
            conn.setConnectTimeout(15000 /* milisegundos */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            if (api_key != null && !api_key.equals("")){
                conn.setRequestProperty("Authorization", api_key);
            }

            conn.connect();

            InputStream is=conn.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String line;
            while ( (line = br.readLine()) != null )
                resp.append(line /*+ "\n"*/);

            responseCode = conn.getResponseCode();

            rawResponse = resp.toString();
            jsonResult = new JSONObject(rawResponse);

            error = true;
            error = jsonResult.getBoolean("error");

            message = "";
            message = jsonResult.getString("message");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
