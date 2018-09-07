package com.example.ahmadrefaat.googlemaps;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.List;


public class GetDirectionsData extends AsyncTask<Object,String,String> {


    private static final String TAG = "GetDirectionsData";
    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    String duration, distance;
    LatLng latLng;
    String name;
    Context mContext;
    int color;
    int width;
    DoneDrawing d;




    public interface DoneDrawing{
        public void done(String Duration, String distance);
    }

    GetDirectionsData(String n, Context mc, int color, int width, DoneDrawing dor){
        this.name = n;
        this.mContext = mc;
        this.color = color;
        this.width = width;
        this.d = dor;
    }

    @Override
    protected String doInBackground(Object... objects) {


        Log.d(TAG, "doInBackground: Downloading objects...");
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        latLng = (LatLng)objects[2];


        System.out.println("URL: ");
        System.out.println(url);


        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleDirectionsData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {

        Log.d(TAG, "onPostExecute: Finished Downloading Directions");
        Log.d(TAG, "onPostExecute: Here's the final Output:" + s);
        
        String[] directionsList;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(s);
        displayDirection(directionsList);
        JSONObject jsonObject = null;

        HashMap<String,String> stuff = null;
        try {
            stuff = parser.getDuration(s);

//            Toast.makeText(mContext, "Distance and Duration for " + this.name + stuff.toString(), Toast.LENGTH_SHORT);
//            System.out.println("Distance and Duration for " + this.name );
//            System.out.println(stuff.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.d.done(stuff.get("duration"),stuff.get("distance"));

    }

    public void getDistanceAndDuration(JSONArray arr){

    }

    public void displayDirection(String[] directionsList)
    {

        Log.d(TAG, "displayDirection: Displaying Directions on Map Now...");
        int count = directionsList.length;
        for(int i = 0;i<count;i++)
        {
            System.out.println(directionsList[i]);
            PolylineOptions options = new PolylineOptions();
            options.color(this.color);
            options.width(this.width);
            options.clickable(true);
            options.addAll(PolyUtil.decode(directionsList[i]));


            mMap.addPolyline(options);

        }
    }
}