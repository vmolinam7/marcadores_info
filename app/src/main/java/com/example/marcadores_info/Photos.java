package com.example.marcadores_info;

import com.google.gson.annotations.SerializedName;

public class Photos {
    @SerializedName("height")
    public int height;
    @SerializedName("html_attributions")
    public String[] html_attributions;
    @SerializedName("photo_reference")
    public String photo_reference;
    @SerializedName("width")
    public int width;
}
