package pt.isec.lj.galleon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import pt.isec.lj.galleon.API.PostRequest;

public class EventEditorActivity extends Activity {
    private GalleonApp app;

    private EditText txtEventName;
    private EditText txtEventDescription;
    private EditText txtEventLocation;
    private EditText txtEventDate;
    private EditText txtEventTime;
    private CheckBox chkEventPrivate;
    private CheckBox chkEventNoShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);

        app = (GalleonApp) getApplication();

        txtEventName = (EditText) findViewById(R.id.txtEventName);
        txtEventDescription = (EditText) findViewById(R.id.txtEventDesc);
        txtEventLocation = (EditText) findViewById(R.id.txtEventLocation);
        txtEventDate = (EditText) findViewById(R.id.txtEventDate);
        txtEventTime = (EditText) findViewById(R.id.txtEventTime);
        chkEventPrivate = (CheckBox) findViewById(R.id.chkEventPrivate);
        chkEventNoShare = (CheckBox) findViewById(R.id.chkEventShare);
    }

    public void onCreateEvent(View v){
        if (!((GalleonApp) getApplication()).isNetworkAvailable(this)) {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!checkAllRequiredFields()) {
            Toast.makeText(this, getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        Integer privateEvent = (chkEventPrivate.isChecked()) ? 1 : 0;
        Integer nonSharedEvent = (chkEventNoShare.isChecked()) ? 0 : 1;

        // Existe uma verificação do lado do servidor se este grupo pertence de facto ao utilizador devido a pedidos forjados.
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("eventname", txtEventName.getText().toString())
                .appendQueryParameter("description", txtEventDescription.getText().toString())
                .appendQueryParameter("location", txtEventLocation.getText().toString())
                .appendQueryParameter("groupid", ((Integer)app.getGroup().getId()).toString())
                .appendQueryParameter("eventdate", txtEventDate.getText().toString())
                .appendQueryParameter("eventtime", txtEventTime.getText().toString())
                .appendQueryParameter("isprivate", privateEvent.toString())
                .appendQueryParameter("isshared", nonSharedEvent.toString());
        String query = builder.build().getEncodedQuery();

        new CreateNewEventTask(this).execute("/event", query, app.getCurrentUser().getApiKey());
    }

    private boolean checkAllRequiredFields() {
        return !(isEmpty(txtEventName)
                || isEmpty(txtEventDescription)
                || isEmpty(txtEventLocation)
                || isEmpty(txtEventDate));
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    private class CreateNewEventTask extends AsyncTask<String, Void, String> {
        private final Context context;
        private ProgressDialog progress;

        CreateNewEventTask(Context c){
            this.context = c;
        }

        @Override
        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            PostRequest postReq = new PostRequest(strings[0],strings[1], strings[2]);

            if (postReq.isError()){
                String msg = postReq.getMessage();
                return (msg.isEmpty()) ? getResources().getString(R.string.conn_error) : ("" + postReq.getResponseCode() + " " + msg);
            }else {
                return getResources().getString(R.string.register_success);
            }
        }

        @Override
        protected void onPostExecute(String message) {
            progress.dismiss();
            Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
