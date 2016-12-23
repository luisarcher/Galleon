package pt.isec.lj.galleon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import pt.isec.lj.galleon.API.GetRequest;
import pt.isec.lj.galleon.API.Request;

public class ExploreActivity extends Activity {

    TextView tvGrp;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        tvGrp = (TextView) findViewById(R.id.txtGrpList);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new ExploreTask(this).execute("/allgrp");
        } else {
            //Vai buscar os grupos da BD local?
            tvGrp.setText("No network connection available.");
        }
    }

    private class ExploreTask extends AsyncTask<String, Void, String>{
        private final Context context;
        public ExploreTask(Context c){
            this.context = c;
        }

        @Override
        protected void onPreExecute(){
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Request getReq =  new GetRequest(strings[0]);

            /* COMPOR OS GRUPOS ATRAVÉS DO OBJECTO JSON */
            return getReq.getJsonResult().toString();
            /* SIM, TEM DE SER AQUI */
        }

        @Override
        protected void onPostExecute(String result){
            progress.dismiss();
            tvGrp.setText(result);
        }
    }
}
