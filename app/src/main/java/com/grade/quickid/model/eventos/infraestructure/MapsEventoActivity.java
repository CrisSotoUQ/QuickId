package com.grade.quickid.model.eventos.infraestructure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.grade.quickid.R;
import com.grade.quickid.model.MainActivity;
import com.grade.quickid.model.eventos.domain.Evento;

import java.io.Serializable;
import java.util.Locale;

public class MapsEventoActivity extends FragmentActivity implements OnMapReadyCallback, Serializable {

    private GoogleMap mMap;
    FusedLocationProviderClient client;
    SupportMapFragment mapFragment;
    Evento receiveEvento;
    private double latitude;
    private double longitude;
    private Button btn_siguiente;
    private int update;
    String imagenOriginal;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

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
        // permisos|
        getCurrentLocation(1);

        btn_siguiente = (Button) findViewById(R.id.btn_siguiente_maps);

        receiveEvento = (Evento) getIntent().getSerializableExtra("Evento");
        update = getIntent().getIntExtra("Update", 0);
        imagenOriginal = getIntent().getStringExtra("Original");
        btn_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiveEvento.setLatitud(latitude);
                receiveEvento.setLongitud(longitude);
                if (latitude == 0.0 && longitude == 0.0) {
                    Toast.makeText(MapsEventoActivity.this, "Es necesario marcar la ubicacion", Toast.LENGTH_SHORT).show();
                } else {
                    if (receiveEvento.getCargueArchivoStatus().equals("0")) {
                        Intent act = new Intent(MapsEventoActivity.this, ConfirmarEventoActivity.class);
                        act.putExtra("Evento", receiveEvento);
                        act.putExtra("Original", imagenOriginal);
                        if (update != 0) {
                            act.putExtra("Update", 1);
                        }
                        startActivity(act);
                    } else {
                        Intent act = new Intent(MapsEventoActivity.this, CargarDatosCsvActivity.class);
                        act.putExtra("Evento", receiveEvento);
                        act.putExtra("Original", imagenOriginal);
                        if (update != 0) {
                            act.putExtra("Update", 1);
                        }
                        startActivity(act);

                    }
                }
            }
        });

    }

    private void marcarOnUpdate(Double lati, Double longi) {

        LatLng latLng = new LatLng(lati, longi);
        //marcador
        MarkerOptions markerOptions = new MarkerOptions();
        // posicion marcador
        markerOptions.position(latLng);
        //latitud y longitud
        markerOptions.title("ubicacion del evento");
        //Zoom al marcador
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        //agregar el marcador en el mapa
        mMap.addMarker(markerOptions);
    }

    private void getCurrentLocation(int flagLocation) {
        //Initialize task location
        //  Task<>
        final int flaglocation = flagLocation;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsEventoActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        Task<Location> task = client.getLastLocation();
        if (flagLocation != 0) {
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    locationRequest = LocationRequest.create();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    locationRequest.setInterval(20 * 1000);
                    locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult == null) {
                                return;
                            }
                            for (Location location : locationResult.getLocations()) {
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {

                                    //inicializar lat long
                                    LatLng latLng = new LatLng(latitude, longitude);
                                    // crear marcador
                                    MarkerOptions option = new MarkerOptions().position(latLng)
                                            .title("Estas aqui").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                    //Zoom
                                    // si vuelvo a llamar el metodo que no haga zoom la geolocalizacion
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                    //agregar marcador en el mapa
                                    googleMap.addMarker(option);
                                    client.removeLocationUpdates(locationCallback);
                                }
                            });
                        }
                    };
                    if (ActivityCompat.checkSelfPermission(MapsEventoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsEventoActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                }
            });
        } else {
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    locationRequest = LocationRequest.create();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    locationRequest.setInterval(20 * 1000);
                    locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult == null) {
                                return;
                            }
                            for (Location location : locationResult.getLocations()) {
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();

                                }
                            }
                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {

                                    //inicializar lat long
                                    LatLng latLng = new LatLng(latitude, longitude);
                                    // crear marcador
                                    MarkerOptions option = new MarkerOptions().position(latLng)
                                            .title("Estas aqui").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                    ;
                                    // no hace Zoom
                                    //agregar marcador en el mapa
                                    googleMap.addMarker(option);
                                    client.removeLocationUpdates(locationCallback);
                                }
                            });
                        }
                    };
                    if (ActivityCompat.checkSelfPermission(MapsEventoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsEventoActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                }
            });
        }

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
        if (update != 0) {
            double lati;
            double longi;
            lati = receiveEvento.getLatitud();
            longi = receiveEvento.getLongitud();
            marcarOnUpdate(lati,longi);
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                getCurrentLocation(0);
                //marcador
                MarkerOptions markerOptions = new MarkerOptions();
                // posicion marcador
                markerOptions.position(latLng);
                //latitud y longitud
                markerOptions.title("ubicacion del evento");
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                //limpiar click anterior
                mMap.clear();
                //Zoom al marcador
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                //agregar el marcador en el mapa
                mMap.addMarker(markerOptions);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation(1);
            }
        }
    }
}