package com.example.ahmadrefaat.googlemaps;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class GetTimeAndDistance extends AsyncTask<String, String, String> {

    GetTimeAndDistanceCallBack cb;
    ShortestDistanceStation sd;
    int start, end;

    public interface GetTimeAndDistanceCallBack{
        public void onSuccess(float distance_car, int time_car, float stations_metro, int time_metro);
    }

    public GetTimeAndDistance(GetTimeAndDistanceCallBack cb, ShortestDistanceStation s, int st, int en) {

        this.cb = cb;
        this.sd = s;
        this.start = st;
        this.end = en;

    }

    @Override
    protected String doInBackground(String... urls) {


        String googleDirectionsData = null;

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleDirectionsData = downloadUrl.readUrl(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return googleDirectionsData;
    }


    public static int[] splitToComponentTimes(int biggy)
    {
        long longVal = biggy;
        int hours = (int) longVal / 3600;
        int remainder = (int) longVal - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        int[] ints = {hours , mins , secs};
        return ints;
    }
    @Override
    protected void onPostExecute(String s) {

        JSONObject full_route, init_metro, metro_final;

        try {
            JSONObject full_obj = new JSONObject(s);

            full_route  = full_obj.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(1);
            init_metro  = full_obj.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
            metro_final = full_obj.getJSONArray("rows").getJSONObject(1).getJSONArray("elements").getJSONObject(1);

            int full_route_time = full_route.getJSONObject("duration").getInt("value");
            int full_route_distance= full_route.getJSONObject("distance").getInt("value");

            int init_metro_time= init_metro.getJSONObject("duration").getInt("value");
            int init_metro_distance= init_metro.getJSONObject("distance").getInt("value");

            int metro_final_time= metro_final.getJSONObject("duration").getInt("value");
            int metro_final_distance= metro_final.getJSONObject("distance").getInt("value");


            float distance_needed_car = full_route_distance/1000;
            float distance_needed_metro = (init_metro_distance + metro_final_distance)/1000;

            int []arr = splitToComponentTimes(full_route_time);
            int []arr2 = splitToComponentTimes(init_metro_time+metro_final_time); // Need to add actual metro time

            int total_Stations = this.sd.getPath(this.start,this.end).second.size();


            String x = "Trip By Car\nTime\n" + arr[0] + " hrs " + arr[1] + " mins";
            x += "\nDistance\n"+distance_needed_car+" km";
            x += "\nTrip By Metro\nTime\n" + arr2[0] + " hrs " + arr2[1] + " mins";
            x += "\nDistance\n"+distance_needed_metro+" km";


            this.cb.onSuccess(distance_needed_car, full_route_time, total_Stations, total_Stations*6*60);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
