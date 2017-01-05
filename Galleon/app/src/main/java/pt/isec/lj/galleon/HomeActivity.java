package pt.isec.lj.galleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import pt.isec.lj.galleon.API.GetRequest;
import pt.isec.lj.galleon.API.Request;
import pt.isec.lj.galleon.models.Event;
import pt.isec.lj.galleon.models.Group;
import pt.isec.lj.galleon.models.Invite;
import pt.isec.lj.galleon.models.User;

public class HomeActivity extends Activity {

    GalleonApp app;
    ListView eventList;
    User currentUser;
    ProgressDialog progress;

    ArrayList<Event> myEvents;

    // Cliente TCP para receber um evento privado pela socket
    DataReceiver eventReceiver;

    static final String baseProfImgUrl = "http://139.59.164.139/images/user/";
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        app = (GalleonApp) getApplication();
        myEvents = new ArrayList<>();

        if ((currentUser = app.getCurrentUser()) == null){
            app.unsetSharedPreferencesSess();
            startActivity(new Intent(this, LoginActivity.class));
        }

        profileImage = (ImageView) findViewById(R.id.profile_image);
        ((TextView)findViewById(R.id.lblName)).setText(currentUser.getUserName());
        ((TextView)findViewById(R.id.lblEmail)).setText(currentUser.getUserEmail());

        eventList = ((ListView) findViewById(R.id.lstMyEvents));
        //new GetMyEventsTask(this).execute("/userevents", currentUser.getApiKey());

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                app.setCurrentEvent((Event)eventList.getItemAtPosition(position));
                showEventActivity();
            }
        });

        new DownloadImageTask().execute(baseProfImgUrl + currentUser.getUserId() + ".png");
    }

    public void showEventActivity(){
        startActivity(new Intent(this, EventActivity.class));
    }

    public void showInvite(String server_api_key){
        //startActivity(new Intent(this, EventActivity.class));
        Intent i = new Intent(this, EventActivity.class);
        i.putExtra("SERVER_API_KEY", server_api_key);
        startActivity(i);
    }

    @Override
    protected void onResume(){
        super.onResume();
        new GetMyEventsTask(this).execute("/userevents", currentUser.getApiKey());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eventReceiver != null)
            eventReceiver.terminate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuCreateGroup:
                new GetUserGroupTask(this).execute("/usergroup",currentUser.getApiKey());
                return true;
            case R.id.menuSearch:
                startActivity(new Intent(this, ExploreActivity.class));
                return true;
            case R.id.menuReceiveEvent:
                eventReceiver = new DataReceiver(this);
                return true;
            case R.id.menuSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetMyEventsTask extends AsyncTask<String, Void, String> {

        Context context;
        Request getRequest;

        GetMyEventsTask (Context c){
            this.context = c;
        }

        @Override
        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage(getResources().getString(R.string.loading));
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            getRequest = new GetRequest(strings[0],strings[1]);

            if (getRequest.isError()){
                String msg = getRequest.getMessage();
                return (msg.isEmpty()) ? getResources().getString(R.string.conn_error) : ("" + getRequest.getResponseCode() + " " + msg);
            }else {
                try {
                    saveData(new JSONObject(getRequest.getRaw()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "";
            }
        }

        @Override
        protected void onPostExecute(String msg) {
            progress.dismiss();
            if (!msg.equals("")){
                Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show();
            }
            eventList.setAdapter(new EventListAdapter());
        }

        private void saveData(JSONObject json){
            try {
                JSONArray events = json.getJSONArray("events");
                myEvents.clear();
                for (int i = 0, size =  events.length(); i < size; i++){
                    JSONObject event = events.getJSONObject(i);
                    myEvents.add(new Event(
                            event.getInt("id"),
                            event.getString("name"),
                            event.getString("description"),
                            event.getString("location"),
                            event.getString("eventdate"),
                            event.getString("eventtime"),
                            event.getInt("groupid"),
                            event.getString("createdat"),
                            event.getDouble("latitude"),
                            event.getDouble("longitude"),
                            event.getInt("isprivate"),
                            event.getInt("isshared")
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetUserGroupTask extends AsyncTask<String, Void, String> {

        Context context;
        Request getRequest;
        private boolean hasGroup;

        GetUserGroupTask (Context c){
            this.context = c;
            hasGroup = false;
        }

        @Override
        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage(getResources().getString(R.string.loading));
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            getRequest = new GetRequest(strings[0], strings[1]);
            if (getRequest.isError()){
                String msg = getRequest.getMessage();
                return (msg.isEmpty()) ? getResources().getString(R.string.conn_error) : ("" + getRequest.getResponseCode() + " " + msg);
            } else {
                hasGroup = true;
                saveData(getRequest.getJsonResult());
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            progress.dismiss();

            if (hasGroup){
                startActivity(new Intent(this.context, GroupPageActivity.class));
            } else {
                startActivity(new Intent(this.context, CreateGroupPageActivity.class));
            }
        }

        private void saveData(JSONObject json){
            try {
                app.setGroup(new Group(
                        json.getInt("groupid"),
                        json.getInt("userid"),
                        json.getString("groupname"),
                        json.getString("groupcat"),
                        json.getString("createdat")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class EventListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return myEvents.size();
        }

        @Override
        public Object getItem(int i) {
            return myEvents.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View layout = getLayoutInflater().inflate(R.layout.event_row, null);

            LinearLayout ll = (LinearLayout) layout.findViewById(R.id.row);
            ll.setBackgroundColor(i%2==0 ? Color.WHITE:Color.rgb(248,248,248));


            ((TextView) layout.findViewById(R.id.lblEventDay)).setText(myEvents.get(i).getDate());
            ((TextView) layout.findViewById(R.id.lblEventHour)).setText(myEvents.get(i).getTime());

            ((TextView) layout.findViewById(R.id.lblEventName)).setText(myEvents.get(i).getName());
            ((TextView) layout.findViewById(R.id.lblEventDescription)).setText(myEvents.get(i).getDescription());

            return layout;
        }
    }

    private class DataReceiver {

        private static final int PORT = 8899;

        Context context;
        Handler procMsg;
        Socket socket;

        ObjectInputStream ois;

        Invite invite;

        DataReceiver(Context c){
            this.context = c;
            this.procMsg = new Handler();
            this.socket = null;
            ois = null;

            if (!app.isNetworkAvailable(context)){
                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
            } else
                clientDlg();
        }

        private void clientDlg(){
            final EditText edtIP = new EditText(context);
            edtIP.setText("192.168.0.1");
            AlertDialog ad = new AlertDialog.Builder(context).setTitle(R.string.dlg_title_event_receive)
                    .setMessage("Server IP").setView(edtIP)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            client(edtIP.getText().toString(), PORT); // to test with emulators: PORTaux);
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    }).create();
            ad.show();
        }

        void client(final String strIP, final int Port) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("EventComm", "Connecting to the server  " + strIP);
                        socket = new Socket(strIP, Port);
                    } catch (Exception e) {
                        socket = null;
                    }
                    commThread.start();
                }
            });
            t.start();
        }

        Thread commThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ois = new ObjectInputStream(socket.getInputStream());
                    invite = (Invite) ois.readObject();
                    app.setCurrentEvent(invite.getEvent());
                    showInvite(invite.getApi());

                    Log.d("RPS", "Received: " + invite.toString());
                    procMsg.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), (R.string.you_were_invited_to + " " + invite.toString()) , Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    procMsg.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.failed_to_send, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        public void terminate(){
            try {
                commThread.interrupt();
                if (socket != null)
                    socket.close();
                if (ois != null)
                    ois.close();
            } catch (Exception e) {
            }
            ois = null;
            socket = null;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private boolean error = false;

        protected Bitmap doInBackground(String... strings) {
            String urldisplay = strings[0];
            Bitmap prof_img = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                prof_img = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                error = true;
            }
            return prof_img;
        }

        protected void onPostExecute(Bitmap result) {
            if (error){
                profileImage.setImageDrawable(getResources().getDrawable(R.drawable.me));
            } else {
                profileImage.setImageBitmap(result);
            }
        }
    }
}
