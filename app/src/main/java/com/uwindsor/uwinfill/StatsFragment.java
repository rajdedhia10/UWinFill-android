package com.uwindsor.uwinfill;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Base64;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String TAG= "hello";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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


    private void displayBarChart(){
        RequestQueue queue= Volley.newRequestQueue(getActivity().getApplicationContext());
        String url="https://uw-fill.herokuapp.com/bar_chart";

        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "Bar Chart Response: "+response);
                    String base64Image= response.getString("image");
                    Log.d(TAG, "Base64 Image: "+base64Image);

                    byte[] decode = Base64.getDecoder().decode(base64Image);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decode , 0, decode.length);
                    //Log.d(TAG, "Binary Image: "+binaryStr);

                    ImageView imgView= (ImageView) getView().findViewById(R.id.barchart);
                    imgView.setImageBitmap(bitmap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Bar Chart Error: "+error);
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void displayPieChart(){
        RequestQueue queue= Volley.newRequestQueue(getActivity().getApplicationContext());
        String url="https://uw-fill.herokuapp.com/pie_chart";

        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "Pie Chart Response: "+response);
                    String base64Image= response.getString("image");
                    //Log.d(TAG, "Base64 Image: "+base64Image);

                    byte[] decode = Base64.getDecoder().decode(base64Image);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decode , 0, decode.length);
                    //Log.d(TAG, "Binary Image: "+binaryStr);

                    ImageView imgView= (ImageView) getView().findViewById(R.id.piechart);
                    imgView.setImageBitmap(bitmap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Pie Chart Error: "+error);
            }
        });
        queue.add(jsonObjectRequest);
    }
}