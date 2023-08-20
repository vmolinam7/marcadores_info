package com.example.marcadores_info;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {
    GoogleMap mapa;
    Lugar[] Lugars;
    public RequestQueue cola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        cola = Volley.newRequestQueue(this);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        this.mapa.clear();
        this.rectangulo(latLng, 1500);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(latLng.toString());
        this.mapa.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        Marker marker = this.mapa.addMarker(markerOptions);
        this.AddMarker(latLng);

    }
    private void rectangulo(LatLng center, double radius) {
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        LatLng northwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 315);
        LatLng southeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 135);
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);

        PolygonOptions rectOptions = new PolygonOptions()
                .add(southeast)
                .add(northeast)
                .add(northwest)
                .add(southwest)
                .strokeColor(Color.BLUE)
                .fillColor(Color.TRANSPARENT);
        mapa.addPolygon(rectOptions);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setOnMapClickListener(this);
        LatLng positionInitial = new LatLng(-1.0123866644997372, -79.46946134770887);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(positionInitial, 14));
        mapa.setInfoWindowAdapter(new InfoWindow(this, "mostrarInfo"));
        mapa.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            //para esconder Info
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                marker.hideInfoWindow();
            }
        });
    }

    public void AddMarker(LatLng latLng) {
        String latitud= String.valueOf(latLng.latitude);
        String longitud = String.valueOf(latLng.longitude);
        System.out.println("Latitud: "+latitud+" y longitud: "+longitud);
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?fields=name&location="+latitud+","+longitud+"&radius=1500&type=bar&key=AIzaSyCZ08ZGZLtgd4ZHQRQqAEuL-RJ10zIybxI";
        System.out.println("ENTRO A AddMarker");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject JSONlista = null;
                        try {
                            JSONlista = new JSONObject(response);
                            JSONArray JSONarray = JSONlista.getJSONArray("results");
                            Gson gson = new Gson();
                            Lugars = gson.fromJson(JSONarray.toString(), Lugar[].class);
                            System.out.println("Elemntos de array"+Lugars.length);
                            for (int i = 0; i < Lugars.length; i++) {
                                LatLng posicion = new LatLng(Lugars[i].geometry.location.lat, Lugars[i].geometry.location.lng);
                                mapa.addMarker(new MarkerOptions()
                                        .position(posicion)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                        .title("dddd")).setTag(Lugars[i]);
                            }

                        } catch (JSONException e) {
                            System.out.println("Error JSONARRAY: " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("ERROR EN PARSEO");
                    }
                }
        );
        cola.add(stringRequest);
    }
    public void aumentar(View view) {
        float maxZoom = mapa.getMaxZoomLevel();
        float currentZoom = mapa.getCameraPosition().zoom;
        if (currentZoom < maxZoom) {
            CameraUpdate zoomIn = CameraUpdateFactory.zoomIn();
            mapa.moveCamera(zoomIn);
        }
    }

    public void alejar(View view) {
        float minZoom = mapa.getMaxZoomLevel();
        float currentZoom = mapa.getCameraPosition().zoom;
        if (currentZoom < minZoom) {
            mapa.moveCamera(CameraUpdateFactory.zoomOut());
        }
    }
}