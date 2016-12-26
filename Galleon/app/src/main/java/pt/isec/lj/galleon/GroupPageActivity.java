package pt.isec.lj.galleon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

import pt.isec.lj.galleon.API.GetRequest;
import pt.isec.lj.galleon.models.Event;
import pt.isec.lj.galleon.models.Group;

public class GroupPageActivity extends Activity {

    GalleonApp app;
    Group currentGroup;
    ListView eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_page);

        app = (GalleonApp) getApplication();

        if ((currentGroup = app.getGroup()) == null || !app.isSetUser())
            finish();

        ((TextView)findViewById(R.id.lblGroupName)).setText(currentGroup.getGroupName());
        ((TextView)findViewById(R.id.lblGroupCat)).setText(currentGroup.getGroupCat());

        eventList = ((ListView) findViewById(R.id.lstGroupEvents));

        if (((GalleonApp)getApplication()).isNetworkAvailable(this))
            new GroupPageEventsTask(this).execute("/events/" + currentGroup.getId(), app.getCurrentUser().getApiKey());
        else
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
    }

    /*@Override
    protected void onResume(){
        super.onResume();

        eventList.setAdapter(new GroupEventListAdapter());
    }*/

    private class GroupPageEventsTask extends AsyncTask<String, Void, String> {

        private final Context context;
        GetRequest getRequest;
        ProgressDialog progress;

        GroupPageEventsTask (Context c){
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
            eventList.setAdapter(new GroupEventListAdapter());
        }

        private void saveData(JSONObject json){
            try {
                JSONArray events = json.getJSONArray("events");
                for (int i = 0, size =  events.length(); i < size; i++){
                    JSONObject event = events.getJSONObject(i);
                    // O modelo não deveria de conhecer os "json names"
                    //currentGroup.addEvent(new Event(event));
                    // por isso
                    currentGroup.addEvent(new Event(
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

    private class GroupEventListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return currentGroup.getEvents().size();
        }

        @Override
        public Object getItem(int i) {
            return currentGroup.getEvents().get(i);
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


            ((TextView) layout.findViewById(R.id.lblEventDay)).setText(currentGroup.getEvents().get(i).getDate());
            ((TextView) layout.findViewById(R.id.lblEventHour)).setText(currentGroup.getEvents().get(i).getTime());

            ((TextView) layout.findViewById(R.id.lblEventName)).setText(currentGroup.getEvents().get(i).getName());
            ((TextView) layout.findViewById(R.id.lblEventDescription)).setText(currentGroup.getEvents().get(i).getDescription());

            return layout;
        }
    }
}
