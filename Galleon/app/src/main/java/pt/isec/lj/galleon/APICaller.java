package pt.isec.lj.galleon;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by luism on 02/12/2016.
 */

public class APICaller {
    protected String baseURL = "http://139.59.164.139/v1"; // Digital Ocean

    public String getAllGroups(){
        return getData("GET", baseURL + "/allgrp");
    }

    protected String getData(String requestMethod, String requestUrl)
    {
        StringBuilder resp = new StringBuilder();
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milisegundos */);
            conn.setConnectTimeout(15000 /* milisegundos */);
            conn.setRequestMethod(requestMethod);
            conn.setDoInput(true);
            conn.connect();
            int codigo = conn.getResponseCode();
            if (codigo == 200) {
                InputStream is=conn.getInputStream();
                BufferedReader br=new BufferedReader(new InputStreamReader(is));
                String line;
                while ( (line = br.readLine()) != null )
                    resp.append(line + "\n");
            } else {
                resp.append("Erro a aceder à página");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp.toString();
    }
}
