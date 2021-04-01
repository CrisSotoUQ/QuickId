package com.grade.quickid.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;

/**
 * Clase Main de la app, controla todos los componentes principales
 * @author  Cristian Camilo Soto
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem tabEventos, tabRegistros;
    private int CAMERA_PERMISSION_CODE = 1;
    private ImageView imageMenu;
    private FragmentPagerController pagerAdapter;
    private DatabaseReference databaseReference;
    private SharedPreferences settings;
    private FirebaseDatabase firebaseDatabase;
    private ImageView imageprofile;
    private DrawerLayout drawerLayout;
    private TextView textoCorreoGoogle;
    private TextView textoNombreGoogle;
    private String nombre;
    private String email;
    private int tab;
    private Button mainQrScanner;
    private String imagen;
    private int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad);
        drawerLayout = findViewById(R.id.drawerLayout);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabEventos = (TabItem) findViewById(R.id.tab_actividades);
        tabRegistros = (TabItem) findViewById(R.id.tab_registros);
        imageMenu = findViewById(R.id.imageMenu);
        mainQrScanner = findViewById(R.id.mainQrScannner);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View hView = navigationView.getHeaderView(0);
        imageprofile = (ImageView) hView.findViewById(R.id.imageprofile);
        textoCorreoGoogle = (TextView) hView.findViewById(R.id.textoCorreoGoogle);
        textoNombreGoogle = (TextView) hView.findViewById(R.id.textoNombreGoogle);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        //setup
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        imagen = intent.getStringExtra("imagen");
        nombre = intent.getStringExtra("nombre");
        tab= intent.getIntExtra("tab",0);

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
            settings = PreferenceManager.getDefaultSharedPreferences(this);
            email = settings.getString("email", null);
            nombre = settings.getString("nombre", null);
            imagen = settings.getString("imagen", null);
        }
        setup(email, imagen, nombre);
        mainQrScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(yaTienePermisos()){
                    lanzarIntent();
                    finish();
                }else{
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
        if (tab == 1){
            tabLayout.getTabAt(1).select();
        }
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        inicializarFirebase();
        // focus en registros



    }
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

    private void lanzarIntent() {
        Intent intent = new Intent(MainActivity.this, QRScannerActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);

    }
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

}