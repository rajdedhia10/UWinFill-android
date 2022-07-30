package com.uwindsor.uwinfill;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

public class StatsFragment extends Fragment {

    private final String TAG = "hello";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");
        displayBarChart();
        displayPieChart();
        return inflater.inflate(R.layout.fragment_stats, container, false);

    }


    private void displayBarChart() {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "https://uw-fill.herokuapp.com/bar_chart";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "Bar Chart Response: " + response);
                    String base64Image = response.getString("image");
                    Log.d(TAG, "Base64 Image: " + base64Image);

                    byte[] decode = Base64.getDecoder().decode(base64Image);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                    //Log.d(TAG, "Binary Image: "+binaryStr);

                    ImageView imgView = getView().findViewById(R.id.barchart);
                    imgView.setImageBitmap(bitmap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Bar Chart Error: " + error);
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void displayPieChart() {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "https://uw-fill.herokuapp.com/pie_chart";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "Pie Chart Response: " + response);
                    String base64Image = response.getString("image");
                    //Log.d(TAG, "Base64 Image: "+base64Image);

                    byte[] decode = Base64.getDecoder().decode(base64Image);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                    //Log.d(TAG, "Binary Image: "+binaryStr);

                    ImageView imgView = getView().findViewById(R.id.piechart);
                    imgView.setImageBitmap(bitmap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Pie Chart Error: " + error);
            }
        });
        queue.add(jsonObjectRequest);
    }
}