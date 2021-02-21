package com.grade.quickid.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.grade.quickid.model.eventos.aplication.CrearEventoActivity;
import com.grade.quickid.model.eventos.infraestructure.QRScanner;
import com.grade.quickid.model.personas.infrastructure.LoginActivity;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TabLayout tabLayout;
    ViewPager viewPager;
    TabItem tab1, tab2;
    private int CAMERA_PERMISSION_CODE = 1;
    ImageView imagemenu;
    FragmentPagerController pagerAdapter;
    DatabaseReference databaseReference;
    SharedPreferences settings;
    FirebaseDatabase firebaseDatabase;
    ImageView imageprofile;
    DrawerLayout drawerLayout;
    TextView textoCorreoGoogle;
    TextView textoNombregoogle;
    private String nombre;
    private String email;
    private Button mainQrScanner;
    private String imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad);
        drawerLayout = findViewById(R.id.drawerLayout);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tab1 = (TabItem) findViewById(R.id.tab_actividades);
        tab2 = (TabItem) findViewById(R.id.tab_registros);
        imagemenu = findViewById(R.id.imageMenu);
        mainQrScanner = findViewById(R.id.mainQrScannner);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View hView = navigationView.getHeaderView(0);
        imageprofile = (ImageView) hView.findViewById(R.id.imageprofile);
        textoCorreoGoogle = (TextView) hView.findViewById(R.id.textoCorreoGoogle);
        textoNombregoogle = (TextView) hView.findViewById(R.id.textoNombreGoogle);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        //setup
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        imagen = intent.getStringExtra("imagen");
        nombre = intent.getStringExtra("nombre");
        //Guardar datos
        if (email!= null){
            settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("email", email);
            editor.putString("imagen", imagen);
            editor.putString("nombre", nombre);
            editor.apply();
            editor.commit();
        }else{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            email = prefs.getString("email", null);
            nombre = prefs.getString("nombre", null);
            imagen = prefs.getString("imagen", null);
        }
        setup(email, imagen, nombre);
        mainQrScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permiso Aceptado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, QRScanner.class);
                    startActivity(intent);
                    finish();
                } else {
                    requestCameraPermission();
                }
            }
        });
        imagemenu.setOnClickListener(new View.OnClickListener() {
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
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        inicializarFirebase();
    }


    private void setup(String email, String imagen, String nombre) {
        Picasso.get().load(imagen).fit().centerInside().into(imageprofile);
        textoCorreoGoogle.setText(email);
        textoNombregoogle.setText(nombre);

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

            //    SharedPreferences.Editor prefs = (SharedPreferences.Editor) getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit();
            //    prefs.clear();
             //   prefs.apply();
                settings.edit().clear().apply();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.menuScanQr: {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permiso Aceptado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, QRScanner.class);
                    startActivity(intent);
                    finish();
                    break;
                } else {
                    requestCameraPermission();
                }

            }
            case R.id.menuEventos: {
                Intent intent = new Intent(MainActivity.this, CrearEventoActivity.class);
                intent.putExtra("Update", 0);
                startActivity(intent);
                finish();
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setMessage("Este permiso es requerido")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

    }

}