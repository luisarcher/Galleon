package pt.isec.lj.galleon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import pt.isec.lj.galleon.API.GetRequest;
import pt.isec.lj.galleon.API.Request;
import pt.isec.lj.galleon.models.CalendarManager;
import pt.isec.lj.galleon.models.Event;
import pt.isec.lj.galleon.models.Group;
import pt.isec.lj.galleon.models.Invite;

public class EventActivity extends Activity {

    private GalleonApp app;
    private Event actualEvent;

    TextView lblEventName;
    TextView lblEventDay;
    TextView lblEventTime;
    TextView lblEventDescription;
    TextView lblEventLocation;

    DataSender eventSender;

    Double eventLat;
    Double eventLon;

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

        eventLat = actualEvent.getLatitude();
        eventLon = actualEvent.getLongitude();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eventSender != null)
            eventSender.terminate();
    }

    public void onEventShare(View v){
        if (actualEvent.sharingAllowed())
            eventSender = new DataSender(this);
        else
            Toast.makeText(this, R.string.event_non_shared, Toast.LENGTH_LONG).show();
    }

    public void onEventSave(View v){
        new EventSubscribeTask(this).execute("/subevent/" + actualEvent.getEventId(),
                app.getCurrentUser().getApiKey());
    }

    public void saveThisEventOnCalendar(){
        CalendarManager cm = new CalendarManager(this);
        cm.addEvent(actualEvent);
    }

    public void onViewMap(View v){
        if (eventLat.intValue() != 0 || eventLon.intValue() != 0){
            Intent i = new Intent(this, MapActivity.class);
            i.putExtra("event_lat", eventLat);
            i.putExtra("event_lon", eventLon);
            startActivity(i);
        } else
            Toast.makeText(this, R.string.gps_coordinates_not_available , Toast.LENGTH_SHORT).show();
    }

    public void onViewGroup(View v){
        new GetEventGroupTask(this).execute("/groups/" + actualEvent.getGroupid() ,app.getCurrentUser().getApiKey());
    }

    private class DataSender {

        private static final int PORT = 8899;

        Context context;
        ProgressDialog progressDlg;
        ServerSocket serverSocket;
        Socket socketToClient;

        ObjectOutputStream oos;
        Handler procMsg;

        DataSender(Context c){
            this.context = c;
            procMsg = new Handler();
            progressDlg = null;
            serverSocket = null;
            socketToClient = null;
            oos = null;

            if (!app.isNetworkAvailable(context)){
                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
            } else server();
        }

        private void server(){
            String ipAddr = getLocalIpAddress();
            progressDlg = new ProgressDialog(context);
            progressDlg.setMessage(getResources().getString(R.string.serverdlg_msg) + "\n(IP: " + ipAddr + ")");
            progressDlg.setTitle(R.string.serverdlg_title);
            progressDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                if (serverSocket!=null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {}
                    serverSocket=null;
                }
                }
            });
            progressDlg.show();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                try {
                    serverSocket = new ServerSocket(PORT);
                    socketToClient = serverSocket.accept();
                    serverSocket.close();
                    serverSocket=null;
                    commThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    socketToClient = null;
                }
                procMsg.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDlg.dismiss();
                    }
                });
                }
            });
            t.start();
        }

        Thread commThread = new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                oos = new ObjectOutputStream(socketToClient.getOutputStream());
                Invite invite = new Invite(actualEvent, app.getCurrentUser().getApiKey());
                oos.writeObject(invite);
                oos.flush();

                procMsg.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.msg_event_sent, Toast.LENGTH_LONG).show();
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

        String getLocalIpAddress() {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface
                        .getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf
                            .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            } catch (SocketException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        public void terminate(){
            try {
                commThread.interrupt();
                if (socketToClient != null)
                    socketToClient.close();
                if (oos != null)
                    oos.close();
            } catch (Exception e) {
            }
            oos = null;
            socketToClient = null;
        }
    }

    private class EventSubscribeTask extends AsyncTask<String, Void, String> {

        private final Context context;
        GetRequest getRequest;
        ProgressDialog progress;

        EventSubscribeTask(Context c) {
            this.context = c;
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage(getResources().getString(R.string.loading));
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            getRequest = new GetRequest(strings[0], strings[1]);
            if (getRequest.isError()) {
                return getResources().getString(R.string.msg_already_subscribed);
            } else {
                saveThisEventOnCalendar();
                return getResources().getString(R.string.msg_subscribed);
            }
        }

        @Override
        protected void onPostExecute(String msg) {
            progress.dismiss();
            Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private class GetEventGroupTask extends AsyncTask<String, Void, String> {

        Context context;
        Request getRequest;
        ProgressDialog progress;

        GetEventGroupTask (Context c){
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
            getRequest = new GetRequest(strings[0], strings[1]);
            if (getRequest.isError()){
                String msg = getRequest.getMessage();
                return (msg.isEmpty()) ? getResources().getString(R.string.conn_error) : ("" + getRequest.getResponseCode() + " " + msg);
            } else {
                try {
                    saveData(new JSONObject(getRequest.getRaw()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return getRequest.getRaw();
        }

        @Override
        protected void onPostExecute(String s) {
            progress.dismiss();
            if (!getRequest.isError())
                startActivity(new Intent(context, GroupPageActivity.class));
        }

        private void saveData(JSONObject json){
            app.setGroup(new Group(json));
        }
    }
}
