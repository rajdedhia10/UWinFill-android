package com.uwindsor.uwinfill;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MapsFragment extends Fragment {

    private static double[] currentLocation;
    private final String TAG = "hello";
    SupportMapFragment mapFragment;

    @SuppressLint({"WrongThread", "StaticFieldLeak"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        GpsTracker gpsTracker = new GpsTracker(getActivity());
        Log.d(TAG, "trying to check location");
        if (!gpsTracker.canGetLocation()) {

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

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    LatLng current = new LatLng(currentLocation[0], currentLocation[1]);
                    googleMap.addMarker(new MarkerOptions()
                            .position(current)
                            .title("Current Location: " + currentLocation[0] + currentLocation[1])
                            .icon(BitmapFromVector(getActivity().getApplicationContext(), R.drawable.ic_action_name))
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
            });
        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        //initialize current location
        currentLocation = getLocation();
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        FloatingActionButton checkIn = (FloatingActionButton) view.findViewById(R.id.checkIn);
        checkIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                try {
                    checkIn();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.d(TAG, "onStart");
//        FloatingActionButton checkIn = (FloatingActionButton) getActivity().findViewById(R.id.checkIn);
//        checkIn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Log.d(TAG, "onClick");
//                try {
//                    checkIn();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    //API call on checkIn
    private void checkIn() throws JSONException {
        Log.d(TAG, "checkIn");
        RequestQueue queue= Volley.newRequestQueue(getActivity().getApplicationContext());
        String url= "https://uw-fill.herokuapp.com/check_in";
        JSONObject jsonObject= new JSONObject();
        jsonObject.put("lat", currentLocation[0]);
        jsonObject.put("long", currentLocation[1]);
        Log.d(TAG, "Check-In Current Coordinates: " + jsonObject.names() + jsonObject.get("lat") + jsonObject.get("long"));
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(
                Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Check-In Response: "+ response);
                try {
                    String result= response.getString("update");
                    if(result.compareTo("inbound")==0){
                        Toast.makeText(getActivity(), String.valueOf("Check-in Successful"), Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getActivity(), String.valueOf("Check-in Failed"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Check-in Error: "+error);
            }
        });
        queue.add(jsonObjectRequest);
    }

    public double[] getLocation() {
        GpsTracker gpsTracker = new GpsTracker(getActivity());
        double[] myList = new double[0];
        if (gpsTracker.canGetLocation()) {
            double latitude = 42.3047308;
            double longitude = -83.0666243;
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            myList = new double[2];
            DecimalFormat df = new DecimalFormat("0.000000");
            myList[0] = Double.parseDouble(df.format(latitude));
            myList[1] = Double.parseDouble(df.format(longitude));
//            Toast.makeText(getActivity(), String.valueOf(myList[0]), Toast.LENGTH_SHORT).show();
//            Toast.makeText(getActivity(), String.valueOf(myList[1]), Toast.LENGTH_SHORT).show();
        } else {
            gpsTracker.showSettingsAlert();
        }
        return myList;

    }

    private void plotAllLocations(GoogleMap googleMap) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://uw-fill.herokuapp.com/flocation";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Get All Response: " + response);

                JSONArray locations = null;
                try {
                    locations = (JSONArray) response.get("fountains");

                    Log.d(TAG, "All Locations: " + locations.length());
                    for (int i = 0; i < locations.length(); i++) {
//                        Log.d(TAG, "IN LOOP: "+i+" "+locations.getJSONObject(i).get("lat") +" "+ locations.getJSONObject(i).get("long"));
                        JSONObject loc = locations.getJSONObject(i);
                        Log.d(TAG, "IN LOOP: " + i + loc);
                        LatLng location = new LatLng((double) loc.get("lat"), (double) loc.get("long"));
                        googleMap.addMarker(new MarkerOptions()
                                .position(location)
                                .title("Location " + (i + 1)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "get All Error: " + error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void plotNearestCoordinates(GoogleMap googleMap) throws JSONException, InterruptedException {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://uw-fill.herokuapp.com/nearest";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lat", currentLocation[0]);
        jsonObject.put("long", currentLocation[1]);
        Log.d(TAG, "Current Coordinates: " + jsonObject.names() + jsonObject.get("lat") + jsonObject.get("long"));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                double[] nearestLocation = new double[2];
                Log.d(TAG, " Get Nearest Response: " + response);

                try {
                    nearestLocation[0] = (double) response.get("lat");
                    nearestLocation[1] = (double) response.get("long");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("hello", "nearest: " + nearestLocation[0] + " " + nearestLocation[1]);

                LatLng loc4 = new LatLng(nearestLocation[0], nearestLocation[1]);
                Log.d(TAG, "location 4: " + loc4);
                googleMap.addMarker(new MarkerOptions()
                        .position(loc4)
                        .title("Nearest Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Get Nearest Error: " + error);
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
        return BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));
    }
}