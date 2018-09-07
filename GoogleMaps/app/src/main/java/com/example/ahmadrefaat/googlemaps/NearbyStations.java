package com.example.ahmadrefaat.googlemaps;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class NearbyStations {

    private static final String TAG = "NearbyStations";
    Context mContext;
    JSONObject stations;


    NearbyStations(Context m){
        Log.d(TAG, "NearbyStations: Reading Stations From File...");

        this.mContext = m;
        try {
            stations = new JSONObject(loadJSONFromAsset(this.mContext));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void getTwoStations(LatLng fCoordinates, final LatLng sCoordinates, final MultipleStationsCallBack multipleStationsCallBack){

        try {
            GetNearestStation(fCoordinates, new NearestStationsCallBack() {
                @Override
                public void OnSuccess(final LatLng nearestStation1, final int stationId1, final String name1) {
                    try {
                        GetNearestStation(sCoordinates, new NearestStationsCallBack() {
                            @Override
                            public void OnSuccess(LatLng nearestStation2, int stationId2, String name2) {
                                multipleStationsCallBack.OnSuccess(nearestStation1, stationId1, name1, nearestStation2, stationId2, name2);
                            }

                            @Override
                            public void OnError(Error err) {

                            }
                        }, "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void OnError(Error err) {
                    throw err;
                }
            }, "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void GetNearestStation(LatLng lt, NearestStationsCallBack cb, String title) throws JSONException {


        ArrayList<MetroStationDistance> stationDistances = new ArrayList<>();
        boolean []exists = new boolean[60];

        JSONArray lines = stations.getJSONArray("lines");
        for (int i = 0; i < lines.length(); i++){
            int line_id = lines.getJSONObject(i).getInt("line_id");
            JSONArray stations = lines.getJSONObject(i).getJSONArray("stations");
            for(int j = 0; j < stations.length(); j++){
                String stationName = stations.getJSONObject(j).getString("station_name");
                double lat = stations.getJSONObject(j).getDouble("lat");
                double lon = stations.getJSONObject(j).getDouble("long");
                int station_id = stations.getJSONObject(j).getInt("id");

                if(exists[station_id])
                    continue;

                exists[station_id] = true;
                stationDistances.add(new MetroStationDistance(station_id,line_id,stationName,new LatLng(lat,lon),lt));
            }
        }


        Collections.sort(stationDistances);

        System.out.println(title);
        System.out.println(stationDistances.get(0));
        System.out.println(stationDistances.get(1));
        System.out.println(stationDistances.get(2));
        System.out.println(stationDistances.get(3));

        cb.OnSuccess(stationDistances.get(0).stationCoordinates, stationDistances.get(0).id, stationDistances.get(0).name);


    }
    private String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("stations.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public interface NearestStationsCallBack{
        public void OnSuccess(LatLng nearestStation, int stationId, String name);
        public void OnError(Error err);
    }

    public interface MultipleStationsCallBack{
        public void OnSuccess(LatLng fCoordinates, int fStationId,String fName, LatLng sCoordinates, int sStationId,String sName);
    }

    private class MetroStationDistance implements Comparable<MetroStationDistance>{
        int id;
        int lineId;
        String name;
        float distance;
        LatLng stationCoordinates;

        public MetroStationDistance(int id, int lineId, String name, LatLng station, LatLng location) {

            this.id = id;
            this.lineId = lineId;
            this.name = name;
            this.stationCoordinates = station;
            this.distance = distance(station.latitude, station.longitude, location.latitude, location.longitude);

        }

        public float distance (double lat_a, double lng_a, double lat_b, double lng_b )
        {
            double earthRadius = 3958.75;
            double latDiff = Math.toRadians(lat_b-lat_a);
            double lngDiff = Math.toRadians(lng_b-lng_a);
            double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                    Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                            Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double distance = earthRadius * c;
            int meterConversion = 1609;

            return new Float(distance * meterConversion).floatValue();
        }

        @Override
        public String toString() {
            return "Station Name: " + this.name + ",id: "+ this.id +", Line:  " + this.lineId;
        }

        @Override
        public int compareTo(@NonNull MetroStationDistance d) {
            return (int) (this.distance-d.distance);
        }
    }

}
