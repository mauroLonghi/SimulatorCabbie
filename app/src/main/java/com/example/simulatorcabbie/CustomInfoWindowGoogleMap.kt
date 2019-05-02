package com.example.simulatorcabbie

import android.app.Activity
import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.map_custom_infowindow.view.*


class CustomInfoWindowGoogleMap(val context : Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(p0: Marker): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInfoContents(marker: Marker): View {
        val view = (context as Activity).layoutInflater.inflate(R.layout.map_custom_infowindow,null)

        view.details
        view.name.text = marker.title
        view.details.text = marker.snippet

        val infoWindowData  = marker.tag as InfoWindowData

        val imageId = context.getResources().getIdentifier(infoWindowData.image.toLowerCase(),
            "drawable", context.getPackageName())
        view.pic.setImageResource(imageId)

        view.hotels.text = infoWindowData.hotel
        view.food.text = infoWindowData.food
        view.transport.text = infoWindowData.transport

        return view
    }

}
