package com.raj.uwintest;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.raj.uwintest.databinding.ActivityMapsBinding;

import java.text.DecimalFormat;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private ActivityMapsBinding binding;

    @SuppressLint({"WrongThread", "StaticFieldLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }


//        new Thread(new GetCoords(){
//            public void run() {
//                GetCoords process = new GetCoords();
//                process.doInBackground();
//            }
//        }).start();

//        new GetCoords().execute(doInBackground());


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public double[] getLocation() {
        GpsTracker gpsTracker = new GpsTracker(MapsActivity.this);
        double[] myList = new double[0];
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            myList = new double[2];
            DecimalFormat df = new DecimalFormat("0.0000000");
            myList[0] = Double.parseDouble(df.format(latitude));
            myList[1] = Double.parseDouble(df.format(longitude));
            Toast.makeText(this, String.valueOf(myList[0]), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, String.valueOf(myList[1]), Toast.LENGTH_SHORT).show();
        } else {
            gpsTracker.showSettingsAlert();
        }
        return myList;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
//        LatLng sydney = new LatLng(-33.852, 151.211);
        double[] returned = getLocation();
        LatLng current = new LatLng(returned[0], returned[1]);
        googleMap.addMarker(new MarkerOptions()
                .position(current)
                .title("Current Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(current));

        float zoomLevel = 16.0f; //This goes up to 21
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));

        LatLng loc1 = new LatLng(42.3076721,-83.0683973);
        googleMap.addMarker(new MarkerOptions()
                .position(loc1)
                .title("Location 1"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(current));


        LatLng loc2 = new LatLng(42.3078383,-83.0677691);
        googleMap.addMarker(new MarkerOptions()
                .position(loc2)
                .title("Location 2"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(current));
//        GetCoords process = new GetCoords();
//        process.doInBackground();
//        googleMap.addMarker(new MarkerOptions()
//                .position(sydney)
//                .title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}