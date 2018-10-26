package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public final static String SEND_GEOCODE = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    public final static String SAMPLE_GET =
            "https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyC_hHyAnm9pa_YIeoOiGoMCSp4R-I0FrR8";
    public final static String API_KEY = "AIzaSyC_hHyAnm9pa_YIeoOiGoMCSp4R-I0FrR8";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText =(EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        String address = message.replace(" ", "+");
        String query = SEND_GEOCODE + address;
        query = query + "&key=" + API_KEY; // final URL to collect info from
        String send_message = "";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, SAMPLE_GET, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            JSONArray allData = response.getJSONArray("results");
                            JSONObject geometry = allData.getJSONObject(0).getJSONObject("geometry");
                            Log.d("message", geometry.toString());
                        } catch (JSONException e) {
                            Log.d("Debug","Fuck");
                            e.printStackTrace();
                        }
                        Log.d("Response", response.toString());
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

        intent.putExtra(EXTRA_MESSAGE, send_message);
        startActivity(intent);
    }
}
