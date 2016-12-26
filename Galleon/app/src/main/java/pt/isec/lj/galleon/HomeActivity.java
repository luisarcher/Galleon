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

import org.json.JSONException;
import org.json.JSONObject;

import pt.isec.lj.galleon.API.GetRequest;
import pt.isec.lj.galleon.API.Request;
import pt.isec.lj.galleon.models.Group;
import pt.isec.lj.galleon.models.User;

public class HomeActivity extends Activity {

    GalleonApp app;
    ListView eventList;
    User currentUser;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        app = (GalleonApp) getApplication();
        if ((currentUser = app.getCurrentUser()) == null){
            app.unsetSharedPreferencesSess();
            startActivity(new Intent(this, LoginActivity.class));
        }

        ((TextView)findViewById(R.id.lblName)).setText(currentUser.getUserName());
        ((TextView)findViewById(R.id.lblEmail)).setText(currentUser.getUserEmail());

        eventList = ((ListView) findViewById(R.id.lstEvents));
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

    // É PRECISO CRIAR UM WORKER PARA IR BUSCAR OS MEUS EVENTOS

    class EventListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int i) {
            return "objecto evento";
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View layout = getLayoutInflater().inflate(R.layout.event_row, null);

            String eventName = "EventoX";

            LinearLayout ll = (LinearLayout) layout.findViewById(R.id.row);
            ll.setBackgroundColor(i%2==0 ? Color.WHITE:Color.rgb(248,248,248));

            ((TextView)layout.findViewById(R.id.lblEventName)).setText(eventName);

            return layout;
        }
    }

    class GetUserGroupTask extends AsyncTask<String, Void, String> {

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
}
