package com.example.ahmadrefaat.googlemaps;

public class NonImportant {


     /*
        --------------------------- google places API Getting Nearest Metro Stations -----------------
     */


//    private void findNearestStations(final LatLng aroundPlace, final String title){
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
//        url += "location="+ aroundPlace.latitude + "," + aroundPlace.longitude;
//        url += "&type=subway_station&rankby=distance&keyword=metro&";
//        url += "key=AIzaSyB-rd_CZNXHQm-807oBUQYOvF9opvOxxIY";
//
//        // prepare the Request
//        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>()
//                {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // display response
//
//                        System.out.println(response.toString());
//                        Log.d("Response", response.toString());
//
//
//                        ArrayList<LatLng> stations = new ArrayList<>();
//
//
//                        try {
//                            if("OK".equals(response.getString("status"))){
//                                JSONArray results = response.getJSONArray("results");
//
//                                double lat, lng;
//                                for(int i = 0; i <results.length(); i++){
//                                    lat = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
//                                    lng = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
//                                    stations.add(new LatLng(lat,lng));
//                                }
//
////                                addMarkersForTrains(stations,title, aroundPlace);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("Error.Response", error.toString());
//                    }
//                }
//        );
//
//        queue.add(getRequest);
//    }



//
//    private void addMarkersForTrains(ArrayList<LatLng> stations, String title,LatLng aroundPlace){
//
//
//        mMap.addMarker(new MarkerOptions().position(stations.get(0)).title("Metro Station").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//
//        // Creating a Route between Original Location And nearest Metro Station
//
//        Object[] dataTransfer = new Object[3];
//        url = getDirectionsUrl(aroundPlace,stations.get(0));
//        System.out.println("Directions Url" + url);
//        GetDirectionsData getDirectionsData = new GetDirectionsData(title,this);
//        dataTransfer[0] = mMap;
//        dataTransfer[1] = url;
//        dataTransfer[2] = stations.get(0);
//        getDirectionsData.execute(dataTransfer);
//
//    }


//    private void geoLocate(){
//        Log.d(TAG, "geoLocate: Locating Searched Place");
//        String entered = searchText.getText().toString();
//
//        Geocoder geocoder = new Geocoder(MapActivity.this);
//        List<Address> list = new ArrayList<>();
//
//        try {
//            list = geocoder.getFromLocationName(entered,1);
//        }catch (IOException e){
//            Log.d(TAG, "geoLocate: Caught an Exception: " + e.getMessage());
//        }
//
//        if(list.size() > 0){
//            Address address = list.get(0);
//            LatLng searchedLocation = new LatLng(address.getLatitude(),address.getLongitude());
//            moveCamera(searchedLocation,DEFAULT_ZOOM, true);
//            addMarker(searchedLocation,address.getAddressLine(0).toString());
//
//
//            Log.d(TAG, "geoLocate: Found Location: " + address.toString());
//
//        }
//
//    }

}
