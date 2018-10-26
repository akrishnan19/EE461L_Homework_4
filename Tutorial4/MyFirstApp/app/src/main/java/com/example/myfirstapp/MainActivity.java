package com.example.myfirstapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    // public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public final static String SEND_GEOCODE = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    public final static String SAMPLE_GET =
            "https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyC_hHyAnm9pa_YIeoOiGoMCSp4R-I0FrR8";
    public final static String API_KEY = "AIzaSyC_hHyAnm9pa_YIeoOiGoMCSp4R-I0FrR8";
    MapFragment mapFragment;

    double lat, lng;
    String county = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapFragment = ((MapFragment) this.getFragmentManager().findFragmentById(R.id.map));
    }

    public void sendMessage(View view){
        EditText editText =(EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        final String address = message.replace(" ", "+");
        String query = SEND_GEOCODE + address;
        query = query + "&key=" + API_KEY; // final URL to collect info from
        // String send_message = "";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, query, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            JSONArray allData = response.getJSONArray("results");
                            JSONArray address_components = allData.getJSONObject(0).getJSONArray("address_components");
                            JSONObject geometry = allData.getJSONObject(0).getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");

                            double lat = location.getDouble("lat");
                            double lng = location.getDouble("lng");
                            String county = "";
                            for(int iii = 0; iii < address_components.length(); iii++) {
                                JSONArray types = address_components.getJSONObject(iii).getJSONArray("types");
                                if(types.getString(0).equals("administrative_area_level_2")) {
                                    county = address_components.getJSONObject(iii).getString("long_name");
                                }
                            }
                            Log.d("GetCoordinates", "lat: " + lat + " lng: " + lng);
                            updateMap(lat, lng, county);

                            Log.d("message", address_components.toString());
                        } catch (JSONException e) {
                            Log.d("Debug","Fuck");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Debug","Fuck");
                        Log.d("Error.Response", "");
                    }
                }
        );
        queue.add(getRequest);
    }

    public void updateMap(double lat, double lng, String county) {
        Log.d("UpdateCoordinates", "We are updating idiot");
        this.lat = lat;
        this.lng = lng;
        this.county = county;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("DebugMap", "Map shall do shit");
        Log.d("MapCoordinates", "lat: " + lat + " lng: " + lng);
        LatLng coords = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coords, 15);
        googleMap.animateCamera(cameraUpdate);
        googleMap.addMarker(new MarkerOptions().position(coords));
        EditText countyField = findViewById(R.id.county);
        countyField.setText(county);
    }
}
