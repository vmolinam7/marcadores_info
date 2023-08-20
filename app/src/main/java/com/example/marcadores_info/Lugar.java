package com.example.marcadores_info;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/*Nombre del Lugar
 Ubicación, Logo y Horarios
 */
public class Lugar {
    public transient String business_status;
    @SerializedName("geometry")
    public Geometry geometry;
    @SerializedName("icon")
    public String icon;
    public transient String icon_background_color;
    public transient String icon_mask_base_uri;
    @SerializedName("name")
    public String name;
    @SerializedName("opening_hours")
    public Opening_hours opening_hours;
    @SerializedName("photos")
    public Photos[] photos;
    @SerializedName("place_id")
    public String place_id;
    public transient String reference;
    public transient String scope;
    public transient ArrayList<String> types;
    @SerializedName("vicinity")
    public String vicinity;
}
