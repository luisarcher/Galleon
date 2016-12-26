package pt.isec.lj.galleon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.isec.lj.galleon.API.GetRequest;
import pt.isec.lj.galleon.API.Request;
import pt.isec.lj.galleon.models.Event;
import pt.isec.lj.galleon.models.Group;
import pt.isec.lj.galleon.models.User;

public class HomeActivity extends Activity {

    GalleonApp app;
    ListView eventList;
    User currentUser;
    ProgressDialog progress;

    ArrayList<Event> myEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        app = (GalleonApp) getApplication();
        myEvents = new ArrayList<Event>();

        if ((currentUser = app.getCurrentUser()) == null){
            app.unsetSharedPreferencesSess();
            startActivity(new Intent(this, LoginActivity.class));
        }

        ((TextView)findViewById(R.id.lblName)).setText(currentUser.getUserName());
        ((TextView)findViewById(R.id.lblEmail)).setText(currentUser.getUserEmail());

        eventList = ((ListView) findViewById(R.id.lstMyEvents));
        new GetMyEventsTask(this).execute("/userevents", currentUser.getApiKey());
    }

    @Override
    protected void onResume(){
        super.onResume();
        eventList.setAdapter(new EventListAdapter());
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
            case R.id.menuFindGroups:
                startActivity(new Intent(this, ExploreActivity.class));
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
                            event.getDouble("longitude")
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
                        currentUser.getUserId(),
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
}
