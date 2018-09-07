package com.example.ahmadrefaat.googlemaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.util.MapUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.google.android.gms.maps.GoogleMap.*;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;



public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, OnConnectionFailedListener {

    // Constants
    private static final String TAG = "MapActivity";
    private static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final float EXTRA_ZOOM = 18f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(23, 25), new LatLng(30, 34));


    // Application Map States


    private static final int FINAL_DESTINATION_STATE = 0;
    private static final int INITIAL_LOCATION_STATE = 1;
    private static final int DISPLAY_INFO_STATE = 2;
    private static final int SHOW_ROUTE_TO_FIRST_METRO_STATE = 3;
    private static final int SHOW_METRO_MAP_STATE = 4;
    private static final int SHOW_ROUTE_TO_FINAL_DESTINATION_STATE = 5;
    private static final int TOTAL_STATES = 6;


    private int currentApplicationState;

    // Widgets

    private AutoCompleteTextView searchText;
    private ImageView currentLocationImage;
    private ImageView cancelSearch;
    private ImageView getDirections;
    private Button nextButton;
    private RelativeLayout foreground;
    private TextView displayText;
    private Button ConfirmMetroButton;
    private Button ConfirmCarButton;
    private ImageView imgNav;
    private RelativeLayout searchLayout;
    private RelativeLayout background;


    private LinearLayout draggableLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageView carImage;
    private ImageView trainImage;
    private int image_choice = -1;
    private TextView train_time, train_distance, car_time, car_distance;
    private Button confirm_trip;



    // Variables
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutoCompleteAdapter mPlaceAutoCompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    protected GeoDataClient mGeoDataClient;
    private PlaceInfo mPlace;
    private MarkerOptions destinationMarker;

    private Marker starterMarker;
    private Marker finalMarker;


    private LatLng destinationCoordinates = null;
    private LatLng originalLocationCoordinates = null;
    private LatLng firstMetroCoordinates = null;
    private LatLng LastMetroCoordinates = null;
    private int firstMetroId = -1;
    private int lastMetroId = -1;
    private String firstMetroName = null;
    private String lastMetroName = null;


    private NearbyStations stations;
    private ShortestDistanceStation shortestDistanceStation;
    private AfterFindingStations afterFindingStations;
    private ArrayList<Pair<LatLng,Pair<String,String>>> all_stations_list;
    private int current_station;
    private ArrayList<Marker> station_markers;


    private double old_angle = -1;

    ColorMatrix matrix = new ColorMatrix();
    ColorMatrixColorFilter filter;

    Object[] dataTransfer;
    String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("ad_font.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        );

        draggableLayout = findViewById(R.id.draggableLayout);
        bottomSheetBehavior = BottomSheetBehavior.from(draggableLayout);
        bottomSheetBehavior.setPeekHeight(0);


        confirm_trip = findViewById(R.id.trip_confirm);
        train_time = findViewById(R.id.metro_time);
        train_distance = findViewById(R.id.metro_distance);

        car_distance = findViewById(R.id.car_distance);
        car_time = findViewById(R.id.car_time);


        carImage = findViewById(R.id.car_image);
        trainImage = findViewById(R.id.train_image);


        matrix.setSaturation(0);
        filter = new ColorMatrixColorFilter(matrix);


        carImage.setColorFilter(filter);
        trainImage.setColorFilter(filter);

        carImage.setOnClickListener(this);
        trainImage.setOnClickListener(this);
        confirm_trip.setOnClickListener(this);

        // Connecting widgets
        searchText = (AutoCompleteTextView) findViewById(R.id.search_input);
        currentLocationImage = (ImageView) findViewById(R.id.ic_gps);
        cancelSearch = (ImageView) findViewById(R.id.cancel_search);
        getDirections = (ImageView) findViewById(R.id.ic_directions);
        nextButton = (Button) findViewById(R.id.next_button);
        imgNav = findViewById(R.id.img_nav);
        searchLayout = findViewById(R.id.relLayout1);
        background = findViewById(R.id.map_background);


        // FOR THE FOREGROUND ONLY
        foreground = findViewById(R.id.foreground);
        displayText = findViewById(R.id.directions_display);
        ConfirmMetroButton = findViewById(R.id.confirm_by_metro);
        ConfirmCarButton = findViewById(R.id.confirm_by_car);


        // On Click Listeners
        nextButton.setOnClickListener(MapActivity.this);
        imgNav.setOnClickListener(this);
        ConfirmMetroButton.setOnClickListener(MapActivity.this);
        currentLocationImage.setOnClickListener(this);
        cancelSearch.setOnClickListener(this);
        getDirections.setOnClickListener(this);


        // Setting Visibility for others to none
        foreground.setVisibility(View.GONE);


        // Getting Permission for Current Location, Otherwise, map not initialized
        getLocationPermissions();


        // Initializing Objects needed for calculating distances and other stuff
        stations = new NearbyStations(MapActivity.this);
        shortestDistanceStation = new ShortestDistanceStation(MapActivity.this);
        afterFindingStations = new AfterFindingStations();


        this.currentApplicationState = FINAL_DESTINATION_STATE;



    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void getLocationPermissions() {
        Log.d(TAG, "getLocationPermissions: Checking for location Permissions");


        // Checking if permission Already Granted, Otherwise request Permission
        String[] permissions = {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Permissions Results!");

        // On Checking that ALL permissions are granted, Map is initialized

        mLocationPermissionGranted = true;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
            break;
        }
    }


    private void initMap() {

        // Initializing the Google Map
        Log.d(TAG, "initMap: Initializing map!");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        // Callback function for when the map gets ready

        Log.d(TAG, "onMapReady: Map is now ready!");
        Toast.makeText(this, "Your Map is ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        initialize_final_location_state();

        if (mLocationPermissionGranted) {
            getDeviceLocation(new OnCompleteListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();
                        LatLng currentL = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

//                        originalLocationCoordinates = currentL;
                        System.out.println("Actual Current Location - Lat:  " + currentL.latitude + ", Long: " + currentL.longitude);

                        moveCamera(currentL, DEFAULT_ZOOM, false);

                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        mMap.getUiSettings().setMapToolbarEnabled(false);


                    }else{
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            init();
        }


        mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                addLocation(latLng,mPlace.getName());
            }
        });

        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
        mMap.setInfoWindowAdapter(customInfoWindow);

    }





    @SuppressLint("MissingPermission")
    public void initialize_final_location_state(){

        currentApplicationState = FINAL_DESTINATION_STATE;

        destinationCoordinates = null;
        originalLocationCoordinates = null;
        firstMetroCoordinates = null;
        LastMetroCoordinates = null;
        firstMetroId = -1;
        lastMetroId = -1;
        firstMetroName = null;
        lastMetroName = null;
        old_angle = -1;

        getDeviceLocation(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: found location!");
                    Location currentLocation = (Location) task.getResult();
                    LatLng currentL = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                    System.out.println("Actual Current Location - Lat:  " + currentL.latitude + ", Long: " + currentL.longitude);


                    mMap.clear();
                    boolean success = mMap.setMapStyle(new MapStyleOptions("[]"));


                    if(!success){
                        System.err.println("Can't Style Map in Initial State");
                    }

                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.getUiSettings().setMapToolbarEnabled(false);


                    searchText.setHint("Where to? Search or Press On The Map");
                    searchText.setText("");


                    CameraPosition cm = new CameraPosition.Builder().target(currentL).zoom(DEFAULT_ZOOM).tilt(0).bearing(0).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cm));
                    searchLayout.setVisibility(View.VISIBLE);
                    background.setVisibility(View.VISIBLE);
                    nextButton.setText("Next");


                }else{
                    Log.d(TAG, "onComplete: current location is null");
                    Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void initialize_initial_location_state(){

        currentApplicationState = INITIAL_LOCATION_STATE;

        background.setVisibility(View.VISIBLE);
        searchText.setText("");
        searchText.setHint("Now choose your pickup spot!");
        searchLayout.setVisibility(View.VISIBLE);


    }

    public void initialize_display_info_state(){

        currentApplicationState = DISPLAY_INFO_STATE;

        System.out.println("\n\n\n");
        System.out.println("In Get All: ");
        System.out.println(originalLocationCoordinates);
        System.out.println(destinationCoordinates);

        getAll();

    }

    @SuppressLint("MissingPermission")
    public void initialize_show_route_to_first_metro_state(){

        currentApplicationState = SHOW_ROUTE_TO_FIRST_METRO_STATE;

        boolean success = mMap.setMapStyle(new MapStyleOptions("[]"));

        if(!success){
            System.err.println("Can't Style Map in SHOW_ROUTE_TO_FIRST_METRO_STATE");
        }


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);


        createPath(originalLocationCoordinates, firstMetroCoordinates, "From Original Location to Metro Station", Color.BLUE, 7, new GetDirectionsData.DoneDrawing() {
            @Override
            public void done(String Duration, String distance) {


                addColoredMarker(originalLocationCoordinates,"Original Location", BitmapDescriptorFactory.HUE_RED, true);
                addColoredMarker(firstMetroCoordinates,firstMetroName, BitmapDescriptorFactory.HUE_BLUE, true);

                foreground.setVisibility(View.GONE);
                background.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.GONE);

                nextButton.setText("Ride Metro");
                nextButton.setVisibility(View.VISIBLE);

                moveCamera(originalLocationCoordinates, DEFAULT_ZOOM,true);

            }
        });



    }

    public void initialize_show_metro_map_state(){

        currentApplicationState = SHOW_METRO_MAP_STATE;

        Pair<String,ArrayList<Integer>> m = shortestDistanceStation.getPath(firstMetroId, lastMetroId);

        drawMetroMap(m.second);
        imgNav.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.GONE);

    }

    @SuppressLint("MissingPermission")
    public void initialize_show_route_to_final_destination_state(){

        currentApplicationState = SHOW_ROUTE_TO_FINAL_DESTINATION_STATE;

        mMap.clear();

        boolean success = mMap.setMapStyle(new MapStyleOptions("[]"));

        if(!success){
            System.err.println("Can't Style Map in SHOW_ROUTE_TO_FINAL_DESTINATION_STATE");
        }


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        createPath(LastMetroCoordinates, destinationCoordinates, "From Last Metro To Destination", Color.BLUE, 7, new GetDirectionsData.DoneDrawing() {
            @Override
            public void done(String Duration, String distance) {



                addColoredMarker(destinationCoordinates,"Final Destination", BitmapDescriptorFactory.HUE_RED, true);
                addColoredMarker(LastMetroCoordinates,lastMetroName, BitmapDescriptorFactory.HUE_BLUE, true);

                nextButton.setText("New Trip");
                nextButton.setVisibility(View.VISIBLE);
                imgNav.setVisibility(View.GONE);


                CameraPosition cm = new CameraPosition.Builder().target(LastMetroCoordinates).zoom(DEFAULT_ZOOM).tilt(0).bearing(0).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cm));

            }
        });

    }





    private void getDeviceLocation(OnCompleteListener listener){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(listener);
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }


    private void init(){
        Log.d(TAG, "init: Initializing");


        // Initializing the Autocomplete utility
        mGoogleApiClient = new GoogleApiClient.Builder(this) .addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).enableAutoManage(this, this).build();
        mPlaceAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null);

        // Setting the search Text functionality
        searchText.setOnItemClickListener(mAutocompleteClickListener);
        searchText.setAdapter(mPlaceAutoCompleteAdapter);


        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH ||actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    // Execute our actual search
                    hideKeyboard(MapActivity.this);
                    return true;
                }
                return false;
            }
        });



        hideKeyboard(MapActivity.this);

    }



    private class AfterFindingStations implements NearbyStations.MultipleStationsCallBack{
        @Override
        public void OnSuccess(LatLng fCoordinates, final int fStationId, String fName , final LatLng sCoordinates, final int sStationId, String sName) {

            firstMetroCoordinates = fCoordinates;
            LastMetroCoordinates = sCoordinates;
            firstMetroId = fStationId;
            lastMetroId = sStationId;
            firstMetroName = fName;
            lastMetroName = sName;

            GetTimeAndDistance td = new GetTimeAndDistance(new GetTimeAndDistance.GetTimeAndDistanceCallBack() {
                @Override
                public void onSuccess(float distance_car, int time_car, float stations_metro, int time_metro) {

                    int []arr_car = GetTimeAndDistance.splitToComponentTimes(time_car);
                    int []arr_metro = GetTimeAndDistance.splitToComponentTimes(time_metro);

                    train_time.setText(new StringBuilder().append(arr_metro[0]).append(" hrs ").append(arr_metro[1]).append(" mins").toString());
                    car_time.setText(new StringBuilder().append(arr_car[0]).append(" hrs ").append(arr_car[1]).append(" mins").toString());

                    train_distance.setText(((int)stations_metro) + " Stations");
                    car_distance.setText(distance_car + " KM");

                    bottomSheetBehavior.setPeekHeight(150);
                    bottomSheetBehavior.setHideable(false);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

//                    background.setVisibility(View.GONE);
//                    foreground.setVisibility(View.VISIBLE);
//                    displayText.setText(dt);

                }
            }, shortestDistanceStation, firstMetroId, lastMetroId);


            td.execute(GetDistanceUrl(originalLocationCoordinates,destinationCoordinates,fCoordinates,sCoordinates));


        }
    }

    public void getAll(){

        if(destinationCoordinates == null || originalLocationCoordinates == null)
            return;



        stations.getTwoStations(originalLocationCoordinates,destinationCoordinates,afterFindingStations);
    }



    /*
        --------------------------- Metro Map -----------------
     */



    public void drawMetroMap(ArrayList<Integer> arr){

        mMap.clear();


        this.current_station = 0;
        String map_style = shortestDistanceStation.loadJSONFromAsset(MapActivity.this,"map_style.json");
        station_markers = new ArrayList<>();

        boolean success = mMap.setMapStyle(new MapStyleOptions(map_style));
        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }


        mMap.clear();


//        Iterator it = shortestDistanceStation.stationHashMap.entrySet().iterator();
//
//        while (it.hasNext()) {
//            HashMap.Entry<Integer,ShortestDistanceStation.Station> pair = (HashMap.Entry)it.next();
//
//            int k = pair.getKey();
//            ShortestDistanceStation.Station station = pair.getValue();
//
//            LatLng stCoordinates = station.coordinates;
//            CircleOptions circleOptions = new CircleOptions()
//                    .center(stCoordinates)
//                    .radius(20).fillColor(Color.BLACK).strokeColor(Color.WHITE);// In meters
//
//            mMap.addCircle(circleOptions);
//
//        }


        ArrayList<Pair<Pair<LatLng,LatLng>,Integer>> subway_lines =  shortestDistanceStation.getAllLines();
        System.out.println("\n\n\n\nTotal Subway Lines: " + subway_lines.size() + "\n\n\n\n") ;

        for(int i = 0; i < subway_lines.size(); i++){
            int l = subway_lines.get(i).second;

            PolylineOptions options = new PolylineOptions();
            if(l == 3)
            options.color(Color.argb(255,33,114,15));
            else if(l == 2)
            options.color(Color.argb(255,174,108,23));
            else if (l == 1)
            options.color(Color.argb(255,32,179,164));
            options.width(15);
            options.add(subway_lines.get(i).first.first,subway_lines.get(i).first.second);
            mMap.addPolyline(options);
        }


        if(arr == null)
            return;

        this.all_stations_list = shortestDistanceStation.getStationPoints(arr);


        for(int i = 0; i < this.all_stations_list.size(); i++) {
            LatLng l1 = this.all_stations_list.get(i).first;
            String n1 = this.all_stations_list.get(i).second.first;
            String n2 = this.all_stations_list.get(i).second.second;

            MarkerOptions llm = new MarkerOptions().position(l1).title(n1).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).visible(true);

            if(n2 != null){
                llm.snippet(n2);
            }

            Marker m = mMap.addMarker(llm);
            station_markers.add(m);

        }

        moveCameraWithStation();

    }


    private void moveCameraWithStation(){

        System.out.println("In Move Camera With Station");



        if(this.current_station >= this.all_stations_list.size()){
            initialize_show_route_to_final_destination_state();
            return;
        }


        System.out.println("In Move Camera With Station: Checkpoint 1");

        if(this.current_station == this.all_stations_list.size() -1){
            System.out.println("In Move Camera With Station: before last");
            mMap.animateCamera(CameraUpdateFactory.newLatLng(this.all_stations_list.get(this.current_station).first));

            this.station_markers.get(this.current_station).showInfoWindow();

            this.current_station++;
            return;
        }

        System.out.println("In Move Camera With Station: Checkpoint 2");

        LatLng l1, l2;
        l1 = this.all_stations_list.get(this.current_station).first;
        l2 = this.all_stations_list.get(this.current_station+1).first;



        final double ang = computeHeading(l1,l2);


        CameraPosition cm;
        if(old_angle == -1){
            cm = new CameraPosition.Builder().target(this.all_stations_list.get(this.current_station).first).zoom(EXTRA_ZOOM).tilt(45).bearing((float)ang).build();
        }else{
            cm = new CameraPosition.Builder().target(this.all_stations_list.get(this.current_station).first).zoom(EXTRA_ZOOM).bearing((float)old_angle).tilt(45).build();
        }
        final int curr = current_station;

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cm), 1500, new GoogleMap.CancelableCallback(){

            @Override
            public void onFinish() {
                System.out.println("Finished Animating the first One!");
                CameraPosition cm = new CameraPosition.Builder().target(all_stations_list.get(curr).first).zoom(EXTRA_ZOOM).tilt(45).bearing((float)ang).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cm), 500,null);

                }

            @Override
            public void onCancel() {

            }
        } );




        this.station_markers.get(this.current_station).showInfoWindow();

        current_station++;
        old_angle = ang;

    }





    // Pnt is the point to save
    // Is_destination refers that this point is the destination if var is true, otherwise this point is the original Location
    private void addLocation(LatLng pnt, String title_for_marker){

        if(currentApplicationState == FINAL_DESTINATION_STATE){


            if(finalMarker != null)
                finalMarker.remove();

            finalMarker = mMap.addMarker(new MarkerOptions().position(pnt).title(title_for_marker).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

            moveCamera(pnt,DEFAULT_ZOOM, true);
            destinationCoordinates = pnt;
            nextButton.setVisibility(View.VISIBLE);

            System.out.println("\n\n\n\nIN FINAL_DESTINATION_STATE, The Coordinates for both are, original : " + originalLocationCoordinates + ", final: " + destinationCoordinates+"\n\n\n\n");

        }else if(currentApplicationState == INITIAL_LOCATION_STATE){



            if(starterMarker != null)
                starterMarker.remove();

            starterMarker = mMap.addMarker(new MarkerOptions().position(pnt).title(title_for_marker).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


            moveCamera(pnt,DEFAULT_ZOOM, true);
//            addColoredMarker(pnt,title_for_marker,BitmapDescriptorFactory.HUE_RED,true);
            originalLocationCoordinates = pnt;

            nextButton.setVisibility(View.VISIBLE);


            System.out.println("\n\n\n\nIN INITIAL_LOCATION_STATE, The Coordinates for both are, original : " + originalLocationCoordinates + ", final: " + destinationCoordinates+"\n\n\n\n");
        }

    }
    /*
        --------------------------- google places API autocomplete suggestions -----------------
     */


    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideKeyboard(MapActivity.this);

            final AutocompletePrediction item = mPlaceAutoCompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try{
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setId(place.getId());
                mPlace.setLatlng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                mPlace.setWebsiteUri(place.getWebsiteUri());
            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage() );
            }

            LatLng destination = new LatLng(place.getViewport().getCenter().latitude,place.getViewport().getCenter().longitude);
            addLocation(destination,mPlace.getName());

            places.release();

        }
    };




    /*
        --------------------------- General Map Utility Functions -----------------
     */


    public void addColoredMarker(LatLng pnt, String tile,float color, boolean show){

        Marker m = mMap.addMarker(new MarkerOptions().position(pnt).title(tile).icon(BitmapDescriptorFactory.defaultMarker(color)));
        if(show) m.showInfoWindow();
    }

    private void addMarker(LatLng latLng, String title, boolean clearAll){

        if(clearAll)
            mMap.clear();
        destinationMarker = new MarkerOptions();
        destinationMarker.position(latLng).title(title);
        mMap.addMarker(destinationMarker);

        Log.d(TAG, "addMarker: Adding Market at: lat: " + latLng.latitude + ", lng: " + latLng.longitude );

    }

    private void moveCamera(LatLng latLng, float zoom, boolean animate){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );

        System.out.println("Current State is: " + currentApplicationState);
        if(!animate){
            System.out.println("Animating Moving Camera");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
        else{
            System.out.println("NOM Animating Moving Camera");
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }


        hideKeyboard(MapActivity.this);
    }


    /*
        --------------------------- Map Path Creation Utility -----------------
     */


    private String getDirectionsUrl(LatLng src, LatLng dest)
    {

        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+src.latitude+","+src.longitude);
        googleDirectionsUrl.append("&destination="+dest.latitude+","+dest.longitude);
        googleDirectionsUrl.append("&key="+"AIzaSyB-rd_CZNXHQm-807oBUQYOvF9opvOxxIY");

        return googleDirectionsUrl.toString();
    }

    private String GetDistanceUrl(LatLng src, LatLng dest, LatLng station1, LatLng station2){


        StringBuilder googleDistanceUrl = new StringBuilder();
        googleDistanceUrl.append("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=");
        googleDistanceUrl.append(src.latitude).append(",").append(src.longitude).append("|");
        googleDistanceUrl.append(station2.latitude).append(",").append(station2.longitude).append("&destinations=");
        googleDistanceUrl.append(station1.latitude).append(",").append(station1.longitude).append("|");
        googleDistanceUrl.append(dest.latitude).append(",").append(dest.longitude).append("&key=AIzaSyB-rd_CZNXHQm-807oBUQYOvF9opvOxxIY");

        return googleDistanceUrl.toString();

    }

    public void createPath(LatLng from, LatLng to, String title, int color, int width, GetDirectionsData.DoneDrawing d){
        Object[] dataTransfer = new Object[3];
        url = getDirectionsUrl(from,to);
        System.out.println("Directions Url" + url);
        GetDirectionsData getDirectionsData = new GetDirectionsData(title,this,color,width, d);
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = to;
        getDirectionsData.execute(dataTransfer);
    }


    /*
        --------------------------- General Views Utility Functions -----------------
     */


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == currentLocationImage.getId()){
            getDeviceLocation(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();
                        LatLng currentL = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

//                        originalLocationCoordinates = currentL;
                        System.out.println("Actual Current Location - Lat:  " + currentL.latitude + ", Long: " + currentL.longitude);

                        moveCamera(currentL, DEFAULT_ZOOM, true);

                    }else{
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if(cancelSearch.getId() == v.getId()){
            searchText.setText("");
        }else if(getDirections.getId() == v.getId() && destinationMarker != null){
            System.out.println("Destination Location - Lat:  " + destinationCoordinates.latitude + ", Long: " + destinationCoordinates.longitude);
            moveCamera(destinationCoordinates,DEFAULT_ZOOM,true);
        }else if(nextButton.getId() == v.getId()){
            nextButton.setVisibility(View.GONE);

            if(currentApplicationState == FINAL_DESTINATION_STATE)
                initialize_initial_location_state();
            else if(currentApplicationState == INITIAL_LOCATION_STATE){
                initialize_display_info_state();
            }else if(currentApplicationState == SHOW_ROUTE_TO_FIRST_METRO_STATE){
                initialize_show_metro_map_state();
            }else if(currentApplicationState == SHOW_ROUTE_TO_FINAL_DESTINATION_STATE){
                initialize_final_location_state();
            }

        }else if(ConfirmMetroButton.getId() == v.getId()){
            System.out.println("Clicked Confirm Metro");
            initialize_show_route_to_first_metro_state();

        }else if(imgNav.getId() == v.getId()){
            moveCameraWithStation();
        }else if(v.getId() == trainImage.getId()){

            trainImage.clearColorFilter();

            train_distance.setTextColor(Color.parseColor("#000000"));
            train_time.setTextColor(Color.parseColor("#000000"));


            if(image_choice != -1) {
                carImage.setColorFilter(filter);
                car_distance.setTextColor(Color.parseColor("#808080"));
                car_time.setTextColor(Color.parseColor("#808080"));
            }
            image_choice = 1;

        }else if(v.getId() == carImage.getId()){

            carImage.clearColorFilter();
            car_distance.setTextColor(Color.parseColor("#000000"));
            car_time.setTextColor(Color.parseColor("#000000"));


            if(image_choice != -1) {
                trainImage.setColorFilter(filter);
                train_distance.setTextColor(Color.parseColor("#808080"));
                train_time.setTextColor(Color.parseColor("#808080"));
            }
            image_choice = 2;
        }else if(confirm_trip.getId() == v.getId()){

            initialize_show_route_to_first_metro_state();
            bottomSheetBehavior.setPeekHeight(0);

            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    private double angleFromCoordinate(double lat1, double long1, double lat2,
                                       double long2) {

        double dLon = (long2 - long1);

        double y = sin(dLon) * cos(lat2);
        double x = cos(lat1) * sin(lat2) - sin(lat1)
                * cos(lat2) * cos(dLon);

        double brng = atan2(y, x);

        brng = toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise

        return brng;
    }


    public static double computeHeading(LatLng from, LatLng to) {
        // http://williams.best.vwh.net/avform.htm#Crs
        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double toLat = toRadians(to.latitude);
        double toLng = toRadians(to.longitude);
        double dLng = toLng - fromLng;
        double heading = atan2(
                sin(dLng) * cos(toLat),
                cos(fromLat) * sin(toLat) - sin(fromLat) * cos(toLat) * cos(dLng));
        return wrap(toDegrees(heading), -180, 180);
    }

    static double wrap(double n, double min, double max) {
        return (n >= min && n < max) ? n : (mod(n - min, max - min) + min);
    }


    static double mod(double x, double m) {
        return ((x % m) + m) % m;
    }

}
