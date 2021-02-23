package com.grade.quickid.model.eventos.aplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Clase para obtener la geolocalizacion
 * @author  Cristian Camilo Soto
 */
public class GetCurrentLocation extends AppCompatActivity {
    private LatLng latLng;
    private FusedLocationProviderClient client;
    
    public LatLng obtenerGeoLocalizacion(int flagLocation) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        client = LocationServices.getFusedLocationProviderClient(this);

        Task<Location> task = client.getLastLocation();

        if (flagLocation != 0) {
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //satisfactorio
                    if (location != null) {
                        //sincronizoLatLng
                        LatLng latLong;
                        latLong = new LatLng(location.getLatitude(), location.getLongitude());
                        latLng = latLong;
                    }
                }
            });
        }
        return latLng;
    }
}
