package pt.isec.lj.galleon;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
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

        eventList = ((ListView) findViewById(R.id.lstEvents));
    }

    @Override
    protected void onResume(){
        super.onResume();
        eventList.setAdapter(new EventListAdapter());
    }

    class EventListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return app.getEvents().size();
        }

        @Override
        public Object getItem(int i) {
            return app.getEvents().get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View layout = getLayoutInflater().inflate(R.layout.event_row, null);

            String eventName = app.getEvents().get(i).getName();

            LinearLayout ll = (LinearLayout) layout.findViewById(R.id.row);
            ll.setBackgroundColor(i%2==0 ? Color.WHITE:Color.rgb(248,248,248));

            ((TextView)layout.findViewById(R.id.lblEventName)).setText(eventName);

            return layout;
        }
    }
}
