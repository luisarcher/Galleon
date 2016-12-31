package pt.isec.lj.galleon;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity implements OnMapReadyCallback {

    Double lat;
    Double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        lat = getIntent().getDoubleExtra("event_lat", 0);
        lon = getIntent().getDoubleExtra("event_lon", 0);
        Toast.makeText(this, " " + lat + " " + lon, Toast.LENGTH_LONG).show();

        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        LatLng eventLocation = new LatLng(lat,lon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 19);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                finish();
                return;
            }

        }
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        CameraPosition cp = new CameraPosition.Builder().target(eventLocation).zoom(17)
                .bearing(0).tilt(0).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
        map.addCircle(new CircleOptions().center(eventLocation).radius(25)
                .fillColor(Color.argb(128, 128, 128, 128))
                .strokeColor(Color.rgb(128, 0, 0)).strokeWidth(4));
        MarkerOptions mo = new MarkerOptions().position(eventLocation).title("Local");
        Marker local = map.addMarker(mo);
        local.showInfoWindow();
    }
}
