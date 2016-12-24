package pt.isec.lj.galleon;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import pt.isec.lj.galleon.models.User;

public class HomeActivity extends Activity {

    GalleonApp app;
    ListView eventList;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        app = (GalleonApp) getApplication();
        //eventList = (ListView) findViewById(R.id.lstEvents);
        currentUser = app.getCurrentUser();

        ((TextView)findViewById(R.id.lblName)).setText(currentUser.getUserName());
        ((TextView)findViewById(R.id.lblEmail)).setText(currentUser.getUserEmail());
    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    class EventListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }
}
