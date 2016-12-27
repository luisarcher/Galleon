package pt.isec.lj.galleon;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import pt.isec.lj.galleon.models.Event;

public class EventActivity extends Activity {

    private GalleonApp app;
    private Event actualEvent;

    TextView lblEventName;
    TextView lblEventDay;
    TextView lblEventTime;
    TextView lblEventDescription;
    TextView lblEventLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        app = (GalleonApp)getApplication();
        actualEvent = app.getCurrentEvent();

        lblEventName = (TextView) findViewById(R.id.lblEventName_act);
        lblEventDay = (TextView) findViewById(R.id.lblEventDay_act);
        lblEventTime = (TextView) findViewById(R.id.lblEventTime_act);
        lblEventDescription = (TextView) findViewById(R.id.lblEventDescription_act);
        lblEventLocation = (TextView) findViewById(R.id.lblEventLocation_act);

        lblEventName.setText(actualEvent.getName());
        lblEventDay.setText(actualEvent.getDate());
        lblEventTime.setText(actualEvent.getTime());
        lblEventDescription.setText(actualEvent.getDescription());
        lblEventLocation.setText(actualEvent.getLocation());
    }
}
