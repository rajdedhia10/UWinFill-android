package com.raj.uwintest;

// TEST FILE DO NOT USE THIS ANYWHERE


import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class GetCoords extends AsyncTask<Void,Void,Void> {
    String result;
    private GoogleMap googleMap;

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            Log.i("gETcOORDS", "TRYING TO CONNECT");
            URL url = new URL("https://pastebin.com/raw/vDYs5ehu");
            Log.i("gETcOORDS", "CONNECTED TO URL");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            Log.i("gETcOORDS", "CONNECTED TO NETWORK");
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
//                result = result + line;
                List<String> coordList = Arrays.asList(line.split(","));
                String latitude = coordList.get(0);
                String longitude = coordList.get(1);
                double str1 = Double.parseDouble(latitude);
                double str2 = Double.parseDouble(longitude);
                LatLng current = new LatLng(str1, str2);
                googleMap.addMarker(new MarkerOptions()
                        .position(current));
//                        .title("Current Location"));
            }
        } catch (MalformedURLException e) {
//            Log.i("myApp", "CATCH MALFORMED URL");
            e.printStackTrace();
        } catch (IOException e) {
//            Log.i("myApp", "IO EXCEPTION");
            e.printStackTrace();
        }
        return null;
    }
}
