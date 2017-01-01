package pt.isec.lj.galleon;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import pt.isec.lj.galleon.models.Event;

/**
 * Created by luism on 01/01/2017
 */

public class CalendarManager {

    Context context;
    public CalendarManager(Context c){
        context = c;
    }

    public void addEvent(Event event){
        /*
        private int eventId;
    private String name;
    private String description;
    private String location;
    private String date;
    private String time;
    private int groupid;
    private String createdAt;
    private Double latitude;
    private Double longitude;
    private int isPrivate;
    private int sharingAllowed;*/

        SimpleDateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");

        String eventDate = "" + event.getDate() + " " + event.getTime();
        Toast.makeText(context, eventDate , Toast.LENGTH_LONG).show();
        Date date;

        try {
            date = originalDateFormat.parse(eventDate);
            eventDate = targetDateFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String dateData[] = eventDate.split(",");

        int e_year = Integer.parseInt(dateData[0]);
        int e_month = Integer.parseInt(dateData[1])-1;
        int e_day = Integer.parseInt(dateData[2]);
        int e_hour = Integer.parseInt(dateData[3]);
        int e_min = Integer.parseInt(dateData[4]);

        GregorianCalendar gCalendar = new GregorianCalendar();
        gCalendar.set(e_year,e_month,e_day, e_hour,e_min);

        long startMilis = gCalendar.getTimeInMillis();

        gCalendar.add(Calendar.DATE, 1);
        long endMilis = gCalendar.getTimeInMillis();

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, startMilis);
        values.put(CalendarContract.Events.DTEND, startMilis);
        values.put(CalendarContract.Events.TITLE, event.getName());
        values.put(CalendarContract.Events.DESCRIPTION, event.getDescription());
        values.put(CalendarContract.Events.EVENT_LOCATION, event.getLocation());
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
        values.put(CalendarContract.Events.ALL_DAY, 1);
        //values.put(CalendarContract.Events.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions((EventActivity) context, new String[]{
                    Manifest.permission.WRITE_CALENDAR}, 20);
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
        long eventId = Long.parseLong(uri.getLastPathSegment());
        Log.d("Novo evento ", String.valueOf(eventId));
        Toast.makeText(context, "Saved: " + eventId, Toast.LENGTH_LONG).show();
    }
}
