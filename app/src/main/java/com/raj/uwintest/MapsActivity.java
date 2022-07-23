package com.raj.uwintest;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.raj.uwintest.databinding.ActivityMapsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private String TAG = "hello";
    private GoogleMap googleMap;
    private ActivityMapsBinding binding;
    private static double[] currentLocation;


    @SuppressLint({"WrongThread", "StaticFieldLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // THE IMPLEMENTATION IS VERY JUGAADU. PLEASE FID A BETTER WAY IF YOU CAN.

        GpsTracker gpsTracker = new GpsTracker(MapsActivity.this);
        Log.d(TAG, "trying to check location");
        if (!gpsTracker.canGetLocation()){

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            this.startActivity(intent);
            SystemClock.sleep(5000);

//            Log.d(TAG, "log "+gpsTracker.canGetLocation());
//            gpsTracker.showSettingsAlert();
//            Log.d(TAG, "setting alert shown");
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        //initialize current location
        currentLocation= getLocation();
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
        //double[] returned = getLocation();


        LatLng current = new LatLng(currentLocation[0], currentLocation[1]);
        googleMap.addMarker(new MarkerOptions()
                .position(current)
                .title("Current Location: "+currentLocation[0]+currentLocation[1])
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_action_name))
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(current));

        float zoomLevel = 16.0f; //This goes up to 21
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));

        plotAllLocations(googleMap);
        try {
            plotNearestCoordinates(googleMap);

        } catch (JSONException | InterruptedException e) {
        }

    }

    private void plotAllLocations(GoogleMap googleMap) {
        RequestQueue queue= Volley.newRequestQueue(this);
        String url= "https://uw-fill.herokuapp.com/flocation";
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Get All Response: "+response);

                JSONArray locations = null;
                try {
                    locations= (JSONArray) response.get("fountains");

                    Log.d(TAG, "All Locations: "+ locations.length());
                    for(int i=0; i<locations.length();i++){
//                        Log.d(TAG, "IN LOOP: "+i+" "+locations.getJSONObject(i).get("lat") +" "+ locations.getJSONObject(i).get("long"));
                        JSONObject loc= locations.getJSONObject(i);
                        Log.d(TAG, "IN LOOP: "+i+ loc);
                        LatLng location= new LatLng((double) loc.get("lat"), (double) loc.get("long"));
                        googleMap.addMarker(new MarkerOptions()
                                .position(location)
                                .title("Location "+(i+1)));
                   }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "get All Error: "+error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void plotNearestCoordinates(GoogleMap googleMap) throws JSONException, InterruptedException {
        RequestQueue queue= Volley.newRequestQueue(this);
        String url="https://uw-fill.herokuapp.com/nearest";
        JSONObject jsonObject= new JSONObject();
        jsonObject.put("lat", currentLocation[0]);
        jsonObject.put("long", currentLocation[1]);
        Log.d(TAG, "Current Coordinates: "+jsonObject.names()+jsonObject.get("lat")+jsonObject.get("long"));

        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(
                Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                double[] nearestLocation= new double[2];
                Log.d(TAG, " Get Nearest Response: "+response);

                try {
                    nearestLocation[0]= (double) response.get("lat");
                    nearestLocation[1]= (double) response.get("long");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("hello", "nearest: "+nearestLocation[0]+" "+nearestLocation[1]);

                LatLng loc4 = new LatLng(nearestLocation[0], nearestLocation[1]);
                Log.d(TAG, "location 4: "+loc4);
                googleMap.addMarker(new MarkerOptions()
                        .position(loc4)
                        .title("Nearest Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Get Nearest Error: "+error);
            }
        });
        queue.add(jsonObjectRequest);
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap.createScaledBitmap(bitmap, 100, 100, false));
    }
}

