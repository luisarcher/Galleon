package pt.isec.lj.galleon;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class EventMapActivity extends Activity implements LocationListener, OnMapReadyCallback{

    TextView tvLat, tvLon;
    GoogleMap googleMap;
    GalleonApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        app = (GalleonApp) getApplication();

        tvLat = (TextView) findViewById(R.id.tvLat);
        tvLon = (TextView) findViewById(R.id.tvLon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 19);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //return;
                finish();
            } else {

                SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
                List<Sensor> lst = sm.getSensorList(Sensor.TYPE_ALL);

                if (lst == null || lst.size() == 0) {
                    finish();
                    return;
                }

                LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);
                ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Double lat = location.getLatitude();
        Double lon = location.getLongitude();
        tvLat.setText("Lat: " + lat);
        tvLon.setText("Lon: " + lon);

        app.setCurrentLocation(new LatLng(lat,lon));
        updateCameraPosition(googleMap,app.getCurrentLocation(), "Local");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(GoogleMap map) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 19);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);

        googleMap = map;
    }

    public void updateCameraPosition(GoogleMap map, LatLng localizacao, String title){
        CameraPosition cp = new CameraPosition.Builder().target(localizacao).zoom(17)
                .bearing(0).tilt(0).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
        map.addCircle(new CircleOptions().center(localizacao).radius(100)
                .fillColor(Color.argb(128, 128, 128, 128))
                .strokeColor(Color.rgb(128, 0, 0)).strokeWidth(4));
        MarkerOptions mo = new MarkerOptions().position(localizacao).title(title);
        Marker locActual = map.addMarker(mo);
        locActual.showInfoWindow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_editor_gps,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAcceptGps:
                app.flag_creating_event_with_gps = true;
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
