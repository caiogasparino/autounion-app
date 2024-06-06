package com.brunofp99.autounion

import com.google.android.gms.maps.model.LatLng

class Address {
    public fun getArray(): ArrayList<LatLng>{
        val array = ArrayList<LatLng>()
        array.add(LatLng(-25.420965,-49.248337))
        array.add(LatLng(-25.442848,-49.304818))
        array.add(LatLng(-23.289992, -51.220447))
        array.add(LatLng(-24.953649, -53.420552))
        array.add(LatLng(-23.570944, -46.673559))
        array.add(LatLng(-23.549976, -46.566610))
        array.add(LatLng(-23.593146, -46.677343))
        array.add(LatLng(-26.316414, -48.863088))
        array.add(LatLng(-26.893736, -49.065158))
        array.add(LatLng(-27.574799, -48.598118))
        array.add(LatLng(-26.958664, -48.639407))
        return array
    }
}
