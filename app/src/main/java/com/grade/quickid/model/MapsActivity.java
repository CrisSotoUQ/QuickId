package com.grade.quickid.model;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.grade.quickid.R;

import java.io.Serializable;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Serializable {

    private GoogleMap mMap;
    FusedLocationProviderClient client;
    SupportMapFragment mapFragment;
    Actividad receiveActividad;
    private double latitude;
    private double longitude;
    private Button btn_siguiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //inicializar locacion
        client = LocationServices.getFusedLocationProviderClient(this);
        // permisos
        getCurrentLocation();
        btn_siguiente = (Button) findViewById(R.id.btn_siguiente_maps);

        receiveActividad = (Actividad) getIntent().getSerializableExtra("Actividad");
        btn_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        if (latitude== 0.0 && longitude == 0.0 ){
            Toast.makeText(MapsActivity.this, "Es necesario marcar la ubicacion", Toast.LENGTH_SHORT).show();

        }else{
              receiveActividad.setLatitud(latitude);
               receiveActividad.setLongitud(longitude);
            Intent act = new Intent(MapsActivity.this,ConfirmarEvento.class);
            act.putExtra("Actividad", receiveActividad);
            startActivity(act);
        }
            }
        });

    }

    private void getCurrentLocation() {
        //Initialize task location
        //  Task<>
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //satisfactorio
                if(location != null ){
                    //sincronizo
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //inicializar lat long
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            // crear marcador
                            MarkerOptions option = new MarkerOptions().position(latLng)
                                    .title("Estas aqui");
                            //Zoom
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                            //agregar marcador en el mapa
                         //   googleMap.addMarker(option);
                        }
                    });

                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
    @Override
    public void onMapClick(LatLng latLng) {
        //marcador
        MarkerOptions markerOptions = new MarkerOptions();
        // posicion marcador
        markerOptions.position(latLng);
        //latitud y longitud
        markerOptions.title(latLng.latitude+" : "+ latLng.longitude);
        latitude= latLng.latitude;
        longitude= latLng.longitude;
        //limpiar click anterior
        mMap.clear();
        //Zoom al marcador
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));
        //agregar el marcador en el mapa
        mMap.addMarker(markerOptions);

    }
});

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }
}