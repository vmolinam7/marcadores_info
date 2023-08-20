package com.example.marcadores_info;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Geometry {
    @SerializedName("location")
    public Location location;
    public transient LatLng viewport;
    public transient LatLng southwest;
}
