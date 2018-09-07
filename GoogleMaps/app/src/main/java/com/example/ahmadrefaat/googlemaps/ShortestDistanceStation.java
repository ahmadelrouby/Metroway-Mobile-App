package com.example.ahmadrefaat.googlemaps;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class ShortestDistanceStation {


    Context mContext;
    int start_id, target_id, starting_line;
    JSONObject stations, adj_matrix;

    HashMap<Integer,Station> stationHashMap;
    HashMap<Integer, ArrayList<Integer>> matrix;



    public boolean comparePairs(Pair<LatLng,LatLng> f, Pair<LatLng,LatLng> s){

        if(f.first.equals(s.first) || f.first.equals(s.second)){
            return f.second.equals(s.first) || f.second.equals(s.second);
        }
        return false;
    }


    public boolean checkExists(ArrayList<Pair<Pair<LatLng,LatLng>,Integer>> lines,Pair<LatLng,LatLng> newPair){
        for(int i = 0; i < lines.size(); i++){
            if(comparePairs(lines.get(i).first,newPair))
                return true;
        }

        return false;
    }


    public ArrayList<Pair<LatLng,Pair<String,String>>> getStationPoints(ArrayList<Integer> arr){

        ArrayList<Pair<LatLng,Pair<String,String>>> stations_list = new ArrayList<>();


        for(int i = 0; i < arr.size(); i++){

            String other = null;
            Station pt = stationHashMap.get(arr.get(i)),pt2 = null,pt3 = null, pt_prev =null;


            if(i == 0){
                int l = getCommonLine(pt,stationHashMap.get(arr.get(i+1)));
                other = "Ride The Metro Station At Line " + l;

            }

            if(i > 0 && i < arr.size()-1){

                pt_prev = stationHashMap.get(arr.get(i-1));
                pt2 = stationHashMap.get(arr.get(i+1));


                if(!sameLine(pt_prev.lines,pt2.lines))
                    other = "Get off the Metro Here and Switch to Line: " + pt2.lines.get(0);


            }

            if(i < arr.size() - 2){

                pt2 = stationHashMap.get(arr.get(i+2));
                if(!sameLine(pt.lines,pt2.lines))
                    if(other == null)
                        other = "Get Ready!Switching Lines Next Station";
                    else
                        other += "\nGet Ready!Switching Lines Next Station";

            }


            if(i == arr.size() - 1){
                other = "Last Station. Drop off Here!";
            }

            Pair<String,String> station_info = new Pair<>(pt.name + " Metro Station",other);
            stations_list.add(new Pair<LatLng, Pair<String,String>>(stationHashMap.get(arr.get(i)).coordinates,station_info));
        }

        return stations_list;

    }


    public ArrayList<Pair<Pair<LatLng,LatLng>,Integer>> getAllLines(){
        ArrayList<Pair<Pair<LatLng,LatLng>,Integer>> lines = new ArrayList<>();

        System.out.println(matrix);
        Iterator it = matrix.entrySet().iterator();

        while (it.hasNext()) {
            HashMap.Entry<Integer,ArrayList<Integer>> pair = (HashMap.Entry)it.next();

            int k = pair.getKey();


            ArrayList<Integer> values = pair.getValue();
            LatLng orgPnt = stationHashMap.get(k).coordinates;


            for(int i = 0; i < values.size(); i++){
                int common_line = getCommonLine(stationHashMap.get(k), stationHashMap.get(values.get(i)));
                Pair<LatLng, LatLng> p = new Pair<LatLng, LatLng>(stationHashMap.get(values.get(i)).coordinates,orgPnt);
                if(!checkExists(lines,p) && common_line != -1){
                    lines.add(new Pair<Pair<LatLng, LatLng>, Integer>(p,common_line));
                }
            }

        }

        return lines;
    }

    private int getCommonLine(Station x1, Station x2){
        for(int i = 0; i < x1.lines.size(); i++){
            for(int j = 0; j < x2.lines.size(); j++){
                if(x1.lines.get(i) == x2.lines.get(j))
                    return x1.lines.get(i);
            }
        }

        return -1;
    }
    public ShortestDistanceStation(Context m) {
        try {


            this.mContext = m;

            String s = loadJSONFromAsset(this.mContext,"stations.json");
            stations = new JSONObject(s);
            String mat = loadJSONFromAsset(this.mContext,"station_links.json");
            adj_matrix = new JSONObject(mat);

            stationHashMap = new HashMap<>();
            matrix = new HashMap<>();

            boolean []exists = new boolean[60];
            JSONArray lines = stations.getJSONArray("lines");
            for (int i = 0; i < lines.length(); i++) {
                int line_id = lines.getJSONObject(i).getInt("line_id");
                JSONArray stations = lines.getJSONObject(i).getJSONArray("stations");
                for (int j = 0; j < stations.length(); j++) {
                    String stationName = stations.getJSONObject(j).getString("station_name");
                    int station_id = stations.getJSONObject(j).getInt("id");

                    double lat = stations.getJSONObject(j).getDouble("lat");
                    double lng = stations.getJSONObject(j).getDouble("long");
                    LatLng pnt = new LatLng(lat,lng);

                    if (exists[station_id])
                    {
                        Station temp = stationHashMap.get(station_id);
                        temp.add_line(line_id);
                        stationHashMap.put(station_id,temp);
                        continue;
                    }
                    exists[station_id] = true;
                    stationHashMap.put(station_id, new Station(stationName, station_id, line_id,pnt));

                }
            }



            Iterator<?> keys = adj_matrix.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if (adj_matrix.get(key) instanceof JSONArray ) {
                    ArrayList<Integer> others = new ArrayList<>();
                    JSONArray arr = adj_matrix.getJSONArray(key);
                    for(int i = 0; i < arr.length(); i++){
                        others.add(arr.getInt(i));
                    }
                    matrix.put(Integer.parseInt(key),others);
                }
            }



            System.out.println("Stations HashMap: ");
            System.out.println(stationHashMap);

            System.out.println("Adjacency Matrix: ");
            System.out.println(matrix);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private boolean sameLine(ArrayList<Integer> f, ArrayList<Integer> s){

        for(int i = 0;i < f.size(); i++){
            for(int j = 0; j < s.size(); j++){
                if(f.get(i).equals(s.get(j)))
                    return true;
            }
        }

        return false;
    }


    private String show_path_info(StationDetails temp){

        StringBuilder path_string = new StringBuilder();
        ArrayList<Integer> path = temp.path;
        Station t,t1;

        ArrayList<Integer> current_line = stationHashMap.get(path.get(0)).lines;

        int start_line = (current_line.size() == 1)? current_line.get(0) : -1;
        if(start_line == -1){
            start_line = stationHashMap.get(path.get(1)).lines.get(0);
        }

        for(int i = 0; i < path.size(); i++){

            t = stationHashMap.get(path.get(i));

            if(i == 0){
                path_string.append("Start at station: ").append(t.name).append("at Line: ").append(start_line).append("\n");
                System.out.println("Start at station: " + t.name + "at Line: " + start_line);
            }
            else{
                path_string.append("then station: ").append(t.name).append("\n");
                System.out.println("then station: " + t.name);
            }


            if(i == path.size() -1)
                continue;

            t1 = stationHashMap.get(path.get(i+1));
            if(t1.lines.size() == 1 && t1.lines.get(0) != start_line){
                start_line = t1.lines.get(0);
                path_string.append("Change at this station to Line: ").append(start_line).append("\n");
                System.out.println("Change at this station to Line: " + start_line);
            }


        }


        return path_string.toString();

    }


    public Pair<String,ArrayList<Integer>> getPath(int start, int end){

        System.out.println("Matrix...");
        System.out.println(matrix);
        System.out.println("\n\n");

        System.out.println("Start: " + start + ", End: " + end);

        if(start == end){
            System.out.println("Start and End are the same");
            return new Pair<>("Start and End Stations are the same",null);
        }

        System.out.println("Starting at: " + start + " to " + end);

        PriorityQueue<StationDetails> stationDetails = new PriorityQueue<>();
        boolean [] closedList = new boolean[60];
        int [] details = new int[60];


        ArrayList<Integer> first_line, second_line;


        for(int i = 0; i < 60; i++){
            details[i] = -1;
            closedList[i] = false;
        }



        details[start] = 0;
        stationDetails.add(new StationDetails(start,0,new ArrayList<Integer>()));
        boolean found = false;

        StationDetails temp = null;
        ArrayList<Integer> paths;

        while (!stationDetails.isEmpty()){

            temp = stationDetails.remove();
            closedList[temp.id] = true;

            if(temp.id == end){
                found = true;
                break;
            }

            first_line = stationHashMap.get(temp.id).lines;

            System.out.println("Current Station: " + temp.id);
            paths = matrix.get(temp.id);

            for(int i = 0; i < paths.size(); i++){

                int new_id = paths.get(i);

                if(closedList[new_id])
                    continue;

                second_line = stationHashMap.get(new_id).lines;
                int new_f = temp.f + 1;

                if(!sameLine(first_line,second_line))
                    new_f+=2;

                if(new_f > details[new_id] && details[new_id] != -1)
                    continue;


                details[new_id] = new_f;
                stationDetails.add(new StationDetails(new_id, new_f, temp.path));

            }
        }

        if(found)
            return new Pair<String,ArrayList<Integer>>(show_path_info(temp),temp.path);


        System.out.println("Can't Find A Path...");
        return new Pair<>("N/A",null);

    }

    private class StationDetails implements Comparable<StationDetails>{
        int id;
        int f;
        ArrayList<Integer> path;

        public StationDetails(int id, int f, ArrayList<Integer> p) {
            this.id = id;
            this.f = f;
            this.path = new ArrayList<>(p);
            this.path.add(id);
        }

        @Override
        public String toString() {
            return "Station ID: " + id + ", F: " + f +  ", with current Path: " + path;
        }

        @Override
        public int compareTo(@NonNull StationDetails o) {
            return (int) (this.f-o.f);
        }
    }
    public String loadJSONFromAsset(Context context, String file_name) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(file_name);
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

    class Station{

        String name;
        int id;
        ArrayList<Integer> lines;
        LatLng coordinates;

        public Station(String name, int id, int line, LatLng cord) {
            this.name = name;
            this.id = id;
            this.lines = new ArrayList<>();
            this.lines.add(line);
            this.coordinates = cord;
        }

        public void add_line(int line){
            this.lines.add(line);
        }

        @Override
        public String toString() {
            return "Station name: " + name + ", id: " + id + ", lines: " + lines;
        }
    }
}
