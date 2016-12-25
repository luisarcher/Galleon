package pt.isec.lj.galleon.API;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pt.isec.lj.galleon.GalleonApp;

public class PostRequest extends Request {
    private String requestMethod;

    public PostRequest(String requestUrl, String queryParams, String api_key){
        this.api_key = api_key;
        requestMethod = "POST";
        sendRequest(requestUrl, queryParams);
    }

    public PostRequest(String requestMethod, String requestUrl, String queryParams, String api_key){
        this.api_key = api_key;
        this.requestMethod = requestMethod;
        sendRequest(requestUrl, queryParams);
    }

    private void sendRequest(String requestUrl, String queryParams){
        try {

            URL url = new URL(baseUrl + requestUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod(requestMethod);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            if (api_key != null && api_key.equals("")){
                connection.setRequestProperty("Authorization", api_key);
            }

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(queryParams);
            writer.flush();
            writer.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder responseOutput = new StringBuilder();
            while((line = br.readLine()) != null ) {
                responseOutput.append(line);
            }
            br.close();

            jsonResult = new JSONObject(responseOutput.toString());
            responseCode = connection.getResponseCode();
            message = jsonResult.getString("message");
            error = jsonResult.getBoolean("error");

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

/* Compor uma queryParams:
Uri.Builder builder = new Uri.Builder()
    .appendQueryParameter("name", "luisFromAppTest2")
    .appendQueryParameter("email", "luismcjordao@hotmail.com")
    .appendQueryParameter("password", "myPassword");
String query = builder.build().getEncodedQuery();
 */
