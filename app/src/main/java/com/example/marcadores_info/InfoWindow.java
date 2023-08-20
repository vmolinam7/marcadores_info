package com.example.marcadores_info;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InfoWindow implements GoogleMap.InfoWindowAdapter {
    Context context;
    LayoutInflater inflater;    
    String titulo;
    View view;
    public RequestQueue requestQueue;
    public JSONArray respuesta=null;

    public String numero = "";

    public InfoWindow(Context context, String titulo) {
        this.context = context;
        this.titulo = titulo;
        requestQueue = Volley.newRequestQueue(context);

        view = LayoutInflater.from(context).inflate(R.layout.ly_windowinfo, null);
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        Lugar lugar;
        try {
            lugar = (Lugar) marker.getTag();
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return this.view;
        }

        //Nombre del Lugar, Ubicación, Logo y Horarios
        //this.parsear("");

        this.detalles(lugar.place_id);

        if (lugar != null) {
            ImageView image = (ImageView)view.findViewById(R.id.image);
            ((TextView) view.findViewById(R.id.lblnombre)).setText(lugar.name);
            ((TextView) view.findViewById(R.id.lblubicación)).setText(lugar.vicinity);
            ((TextView) view.findViewById(R.id.lbltelefono)).setText(numero);
            if (lugar.opening_hours == null ) { lugar.opening_hours = new Opening_hours(); }
            ((TextView) view.findViewById(R.id.lblhorarios)).setText(lugar.opening_hours.open_now ? "Abierto" : "Cerrado");


            if (lugar.photos != null && lugar.photos.length != 0 ) {
                String referencia = lugar.photos[0].photo_reference;
                Glide.with(view)
                        .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference="+ referencia +"&key=AIzaSyB5MkIB5lNnQH1kC1tZ3ATeEsv7z66moKs")
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(image);
                Log.d("GetInfoWindow-img", referencia);
            }else {
                Log.d("GetInfoWindow", lugar.icon);
                Glide.with(view).load(lugar.icon).into(image);
            }

        } else {
            ((TextView) view.findViewById(R.id.lblnombre)).setText("No info");
            ((TextView) view.findViewById(R.id.lblubicación)).setText("No info");
            ((TextView) view.findViewById(R.id.lblhorarios)).setText("Cerrado");
        }

        return view;
    }
    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        //Devolver el contenido del info window

        return null;
    }
    public JSONArray parsear(String url, String parametros, String data) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject JSONlista = null;
                        try {
                            JSONlista = new JSONObject(response);
                            respuesta = JSONlista.getJSONArray(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(stringRequest);
        return respuesta;
    }

    public void detalles(String placeID) {
        String url = "https://maps.googleapis.com/maps/api/place/details/json" +
                "?fields=name%2Crating%2Cformatted_phone_number" +
                "&place_id=" + placeID +
                "&key=AIzaSyB5MkIB5lNnQH1kC1tZ3ATeEsv7z66moKs";
        JsonObjectRequest detallesRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("Detalles", response.getString("status"));
                            numero = response.getJSONObject("result").getString("formatted_phone_number");
                            System.out.println("El número es: "+numero);
                        } catch (JSONException e) {
                            Log.d("Detalles", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Detalles", error.getMessage());
                    }
                });

        requestQueue.add(detallesRequest);
    }
}
