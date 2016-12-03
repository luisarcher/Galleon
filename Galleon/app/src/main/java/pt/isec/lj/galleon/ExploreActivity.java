package pt.isec.lj.galleon;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExploreActivity extends Activity {

    TextView tvGrp;
    //GalleonApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        tvGrp = (TextView) findViewById(R.id.txtGrpList);

        getAllGroups();
        //app = (GalleonApp)getApplication();
    }

    public void getAllGroups(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            ArrayList<Group> groups = new ArrayList<Group>();
            @Override
            protected String doInBackground(Void... params) {
                String resultado = "(Erro)";
                try {
                    APICaller apiCaller = new APICaller();
                    resultado = apiCaller.getAllGroups();
                    JSONObject obj = new JSONObject(resultado);

                    if (obj.getString("error") == "false"){ //não existem erros

                        //resultado = obj.toString(4);
                        //resultado = obj.getString("error");

                        JSONArray recs = obj.getJSONArray("groups");
                        //JSONObject d2 = array.getJSONObject(1);
                        //double pressure = d2.getDouble("pressure");
                        //resultado = "Pressão: " +pressure+"\n"+d2.toString(4);
                        //resultado = array.toString();

                        // Adiciona no máximo 20 grupos ao array temporário
                        for (int i = 0;(i < recs.length() && i<20) ; ++i) {
                            JSONObject rec = recs.getJSONObject(i);
                            groups.add(
                                    new Group(
                                            rec.getInt("id"),
                                            rec.getInt("userid"),
                                            rec.getString("groupname"),
                                            rec.getString("createdat")
                                    )
                            );
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return resultado;
            }

            @Override
            protected void onPostExecute(String resultado) {
                tvGrp.setText(resultado);
            }
        };

        if (networkInfo != null && networkInfo.isConnected()) {
            task.execute();
        } else {
            //Vai buscar os grupos da BD local
            tvGrp.setText("No network connection available.");
        }
    }
}
