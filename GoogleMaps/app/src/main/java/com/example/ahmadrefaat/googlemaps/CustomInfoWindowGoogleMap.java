package com.example.ahmadrefaat.googlemaps;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahmadrefaat.googlemaps.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.info_window, null);

        TextView title = view.findViewById(R.id.window_title);
        TextView snipp = view.findViewById(R.id.window_snippet);


        title.setText(marker.getTitle());

        System.out.println(marker.getTitle());

        if(marker.getSnippet() == null){
            snipp.setVisibility(View.GONE);
        }else{
            snipp.setText(marker.getSnippet());
        }
//        System.out.println(marker.getSnippet());



//        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

//        int imageId = context.getResources().getIdentifier(infoWindowData.getImage().toLowerCase(),
//                "drawable", context.getPackageName());
//        img.setImageResource(imageId);

//        hotel_tv.setText(infoWindowData.getHotel());
//        food_tv.setText(infoWindowData.getFood());
//        transport_tv.setText(infoWindowData.getTransport());

        return view;
    }
}
 