package pt.isec.lj.galleon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pt.isec.lj.galleon.API.PostRequest;

public class CreateGroupPageActivity extends Activity {

    private GalleonApp app;
    private ProgressDialog progress;

    private String groupName;
    private String groupCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_page);

        app = (GalleonApp) getApplication();
    }

    public void onGroupCreate(View v){
        groupName = ((TextView) findViewById(R.id.txtGroupName)).getText().toString();
        groupCat = ((TextView) findViewById(R.id.txtGroupCat)).getText().toString();

        if (!app.isNetworkAvailable(this)) {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        if (groupName.equals("") || groupCat.equals("")){
            Toast.makeText(this, getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("groupName", groupName)
                .appendQueryParameter("groupCat", groupCat);
        String query = builder.build().getEncodedQuery();

        new CreateGroupPageTask(this).execute("/groups", query);
    }

    private class CreateGroupPageTask extends AsyncTask<String, Void, String> {
        private final Context context;
        PostRequest postReq;

        CreateGroupPageTask(Context c){
            this.context = c;
        }

        @Override
        protected void onPreExecute(){
            progress = new ProgressDialog(this.context);
            progress.setMessage(getResources().getString(R.string.group_progress_msg));
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String msg = "";
            postReq = new PostRequest(strings[0],strings[1], app.getCurrentUser().getApiKey());
            msg = postReq.getMessage();

            if (postReq.isError()){
                return (msg.isEmpty()) ? getResources().getString(R.string.conn_error) : ("" + msg + " (" + postReq.getResponseCode() + ")");
            }else {
                return (msg.isEmpty()) ? getResources().getString(R.string.group_created_ok) : (msg);
            }
        }

        @Override
        protected void onPostExecute(String message){
            progress.dismiss();
            Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();

            // Pode por exemplo ir para a página do grupo depois de o criar
            finish();
        }
    }
}
