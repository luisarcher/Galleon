package pt.isec.lj.galleon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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

        if (((GalleonApp)getApplication()).isNetworkAvailable(this))
            new ExploreTask(this).execute("/usergroup", "68ad8d6469245160e510443308551b16");
        else
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
    }

    private class ExploreTask extends AsyncTask<String, Void, String>{
        private final Context context;
        ExploreTask(Context c){
            this.context = c;
        }

        @Override
        protected void onPreExecute(){
            progress = new ProgressDialog(this.context);
            progress.setMessage(getResources().getString(R.string.loading));
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Request getReq =  new GetRequest(strings[0], "");

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
