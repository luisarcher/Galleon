package pt.isec.lj.galleon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import pt.isec.lj.galleon.models.Group;

public class ExploreActivity extends Activity {

    private EditText txtSearch;
    private ListView groupList;

    private ProgressDialog progress;
    private GalleonApp app;
    private ArrayList<Group> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        txtSearch = (EditText) findViewById(R.id.txtSearch);
        groupList = (ListView) findViewById(R.id.lstGroupList);

        app = (GalleonApp) getApplication();
        groups = new ArrayList<>();

        /*txtSearch.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (!event.isShiftPressed()) {
                                search();
                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                });*/

        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                app.setGroup((Group) groupList.getItemAtPosition(position));
                showGroupActivity();
            }
        });
    }

    private void showGroupActivity(){
        startActivity(new Intent(this, GroupPageActivity.class));
    }

    public void onSearch(View v){
        search();
    }

    public void search(){
        if (((GalleonApp)getApplication()).isNetworkAvailable(this))
            new ExploreTask(this).execute("/search/" + txtSearch.getText().toString(), app.getCurrentUser().getApiKey());
        else
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
    }

    private class ExploreTask extends AsyncTask<String, Void, String>{
        private final Context context;
        private GetRequest getRequest;

        private ExploreTask(Context c){
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
            getRequest =  new GetRequest(strings[0], strings[1]);

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
        protected void onPostExecute(String result){
            groupList.setAdapter(new GroupListAdapter());
            progress.dismiss();
        }

        private void saveData(JSONObject json){
            try {
                groups.clear();
                JSONArray json_groups = json.getJSONArray("groups");
                for (int i = 0, size =  json_groups.length(); i < size; i++){
                    JSONObject group = json_groups.getJSONObject(i);
                    groups.add(new Group(group));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GroupListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return groups.size();
        }

        @Override
        public Object getItem(int i) {
            return groups.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View layout = getLayoutInflater().inflate(R.layout.group_row,null);

            LinearLayout ll = (LinearLayout) layout.findViewById(R.id.row);
            ll.setBackgroundColor(i%2==0 ? Color.WHITE:Color.rgb(248,248,248));

            ((TextView) layout.findViewById(R.id.lblGroupName_row)).setText(groups.get(i).getGroupName());
            ((TextView) layout.findViewById(R.id.lblGroupCategory_row)).setText(groups.get(i).getGroupCat());

            return layout;
        }
    }
}
