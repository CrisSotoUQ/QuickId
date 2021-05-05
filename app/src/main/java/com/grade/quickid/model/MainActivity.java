package com.grade.quickid.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grade.quickid.R;
import com.grade.quickid.model.comentarios.infraestructure.ComentarioActivity;
import com.grade.quickid.model.eventos.aplication.CrearEventoActivity;
import com.grade.quickid.model.personas.infrastructure.ContactoActivity;
import com.grade.quickid.model.registros.infraestructure.QRScannerActivity;
import com.grade.quickid.model.personas.infrastructure.LoginActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Clase Main de la app, controla todos los componentes principales
 * @author Cristian Camilo Soto
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem tabEventos;
    private TabItem tabRegistros;
    private int CAMERA_PERMISSION_CODE = 1;
    private ImageView imageMenu;
    private FragmentPagerController pagerAdapter;
    private DatabaseReference databaseReference;
    private SharedPreferences settings;
    private FirebaseDatabase firebaseDatabase;
    private ImageView imageprofile;
    private DrawerLayout drawerLayout;
    private TextView textoCorreoGoogle;
    private FusedLocationProviderClient client;
    private TextView textoNombreGoogle;
    private String nombre;
    private String email;
    private LocationRequest locationRequest;
    private int tab;
    public static final int REQUEST_CHECK_SETTING = 1001;
    private Button mainQrScanner;
    private String imagen;
    private int REQUEST_CODE = 1;
    private static LatLng latLng;
    private LocationCallback locationCallback;
    LocationManager locationManager;
    private Context con;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        prenderGps();
        setContentView(R.layout.activity_actividad);
        drawerLayout = findViewById(R.id.drawerLayout);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabEventos = (TabItem) findViewById(R.id.tab_actividades);
        tabRegistros = (TabItem) findViewById(R.id.tab_registros);
        imageMenu = findViewById(R.id.imageMenu);
        client = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        mainQrScanner = findViewById(R.id.mainQrScannner);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View hView = navigationView.getHeaderView(0);
        imageprofile = (ImageView) hView.findViewById(R.id.imageprofile);
        textoCorreoGoogle = (TextView) hView.findViewById(R.id.textoCorreoGoogle);
        textoNombreGoogle = (TextView) hView.findViewById(R.id.textoNombreGoogle);
        obtenerLocation(1);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        //setup
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        imagen = intent.getStringExtra("imagen");
        nombre = intent.getStringExtra("nombre");
        tab = intent.getIntExtra("tab", 0);

        //Guardar datos persistentes de session
        if (email != null) {
            settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("email", email);
            editor.putString("imagen", imagen);
            editor.putString("nombre", nombre);
            editor.apply();
            editor.commit();
        } else {
            // si ya estamos en session
            settings = PreferenceManager.getDefaultSharedPreferences(this);
            email = settings.getString("email", null);
            nombre = settings.getString("nombre", null);
            imagen = settings.getString("imagen", null);
        }
        setup(email, imagen, nombre);
        mainQrScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yaTienePermisos()) {
                    lanzarIntent();
                    finish();
                } else {
                    requestCameraPermission();
                }
            }
        });
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        pagerAdapter = new FragmentPagerController(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    pagerAdapter.notifyDataSetChanged();
                }
                if (tab.getPosition() == 1) {
                    pagerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (tab == 1) {
            tabLayout.getTabAt(1).select();
        }
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        inicializarFirebase();
        // focus en registros


    }
     // obtener localizacion en la pantalla principal
    private void obtenerLocation(int flagLocation) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        Task<Location> task = client.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location1) {
                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(100);
                locationRequest.setFastestInterval(200);
                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) {
                            return;
                        }
                        for (Location location : locationResult.getLocations()) {
                            if (location != null) {
                                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            }
                        }
                    }
                };
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
// acccion para pedir que el usuario active el gps
    private void prenderGps() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "Gps is on", Toast.LENGTH_LONG).show();
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            try {
                                resolvableApiException.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTING);
                            } catch (IntentSender.SendIntentException sendIntentException) {
                                sendIntentException.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }
    // setup correo y nombre en el menu lateral
    private void setup(String email, String imagen, String nombre) {
        Picasso.get().load(imagen).fit().centerInside().into(imageprofile);
        textoCorreoGoogle.setText(email);
        textoNombreGoogle.setText(nombre);
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
    // componentes del navigationView
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.Salir: {
                settings.edit().clear().apply();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.menuScanQr: {
                if (yaTienePermisos()) {
                    lanzarIntent();
                    finish();
                    break;
                } else {
                    requestCameraPermission();
                    break;
                }
            }
            case R.id.menuEventos: {
                Intent intent = new Intent(MainActivity.this, CrearEventoActivity.class);
                intent.putExtra("Update", 0);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.menuComentarios: {
                Intent intent = new Intent(MainActivity.this, ComentarioActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.contacto: {
                Intent intent = new Intent(MainActivity.this, ContactoActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
// lanzar intent para escaner qr
    private void lanzarIntent() {
        Intent intent = new Intent(MainActivity.this, QRScannerActivity.class);
        if (latLng == null) {
            Location location = getLastKnownLocation();
            if (location == null) {
                Context context = getApplicationContext();
                find_Location(context);
                if (latLng == null){
                    Context context2 = getApplicationContext();
                    foo(context2);
                    if (latLng != null) {
                        intent.putExtra("latitude", location.getLatitude());
                        intent.putExtra("longitude", location.getLongitude());
                        startActivity(intent);
                    }
                }else{
                    intent.putExtra("latitude", location.getLatitude());
                    intent.putExtra("longitude", location.getLongitude());
                    startActivity(intent);
                }

            } else {
                intent.putExtra("latitude", location.getLatitude());
                intent.putExtra("longitude", location.getLongitude());
                startActivity(intent);
            }

         } else {
            intent.putExtra("latitude", latLng.latitude);
            intent.putExtra("longitude", latLng.longitude);
            startActivity(intent);
            finish();
        }
    }
// obtener permisos de camara
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);

    }
// validar si ya tiene persmisos
    private boolean yaTienePermisos() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }


    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    lanzarIntent();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "porfavor otorgar permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }
// obtener la ultima localizacion valida
    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;

        }
        Log.d("location ", bestLocation.getLatitude() + " " + bestLocation.getLongitude());
        return bestLocation;
    }

// obtener localizacion 2 por si no existe la ultima en sistema
    public void find_Location(Context con) {
        Log.d("Find Location", "in find_location");
        this.con = con;
        String location_context = con.LOCATION_SERVICE;
        locationManager = (LocationManager) con.getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                        }
                    });
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
    }

    public void foo(Context context) {
        // when you need location
        // if inside activity context = this;

        SingleShotLocationProvider.requestSingleUpdate(context,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                         latLng= new  LatLng(location.latitude,location.longitude);
                    }
                });
    }



}