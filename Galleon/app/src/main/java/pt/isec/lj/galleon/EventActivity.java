package pt.isec.lj.galleon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import pt.isec.lj.galleon.models.Event;

public class EventActivity extends Activity {

    private GalleonApp app;
    private Event actualEvent;

    TextView lblEventName;
    TextView lblEventDay;
    TextView lblEventTime;
    TextView lblEventDescription;
    TextView lblEventLocation;

    DataSender eventSender;

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

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eventSender != null)
            eventSender.terminate();
    }

    public void onEventShare(View v){
        eventSender = new DataSender(this);
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

            if (!app.isNetworkAvailable(context)){
                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
            }

            server();
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
                    String t = "Hello from Android!";
                    oos.writeObject(t);
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

        public String getLocalIpAddress() {
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
}
