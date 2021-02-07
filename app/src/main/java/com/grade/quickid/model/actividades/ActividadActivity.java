package com.grade.quickid.model.actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.grade.quickid.R;
import com.grade.quickid.model.personas.LoginActivity;
import com.grade.quickid.model.controller.FragmentPagerController;
import com.squareup.picasso.Picasso;

public class ActividadActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    TabLayout tabLayout;
    ViewPager viewPager;
    TabItem tab1,tab2;
    private int CAMERA_PERMISSION_CODE = 1;
    ImageView imagemenu;
    FragmentPagerController pagerAdapter;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    ImageView imageprofile;
    DrawerLayout drawerLayout;
    TextView textoCorreoGoogle;
    TextView textoNombregoogle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad);
        drawerLayout= findViewById(R.id.drawerLayout);

        tabLayout =(TabLayout) findViewById(R.id.tab_layout);
        viewPager =(ViewPager) findViewById(R.id.view_pager);
        tab1 = (TabItem) findViewById(R.id.tab_actividades);
        tab2 = (TabItem) findViewById(R.id.tab_registros);
        imagemenu = findViewById(R.id.imageMenu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View hView =  navigationView.getHeaderView(0);
        imageprofile = (ImageView) hView.findViewById(R.id.imageprofile);
        textoCorreoGoogle = (TextView) hView.findViewById(R.id.textoCorreoGoogle);
        textoNombregoogle = (TextView) hView.findViewById(R.id.textoNombreGoogle);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String tipo = intent.getStringExtra("tipo");
        String imagen= intent.getStringExtra("imagen");
        String nombre = intent.getStringExtra("nombre");
        setup(email,tipo,imagen, nombre);

        // Guardar datos session
        SharedPreferences.Editor prefs= (SharedPreferences.Editor) getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit();
        prefs.putString("email",email);
        prefs.putString("tipo",tipo);
        prefs.putString("imagen",imagen);
        prefs.putString("nombre", nombre);
        prefs.apply();
        imagemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        pagerAdapter = new FragmentPagerController(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition()==0){
                    pagerAdapter.notifyDataSetChanged();
                }
                if (tab.getPosition()==1){
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
        inicializarFirebase ();
    }

    private void setup( String email, String tipo,String imagen, String nombre) {
        Picasso.get().load(imagen).fit().centerInside().into(imageprofile);
        textoCorreoGoogle.setText(email);
        textoNombregoogle.setText(nombre);

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.Salir: {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor prefs= (SharedPreferences.Editor) getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit();
                prefs.clear();
                prefs.apply();
                Intent intent = new Intent(ActividadActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            } case R.id.menuScanQr:{
                if (ContextCompat.checkSelfPermission(ActividadActivity.this,
                        Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(ActividadActivity.this,"Permiso Aceptado",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActividadActivity.this, QRScanner.class);
                    startActivity(intent);
                    break;
                }else{
                    requestCameraPermission();
                }

            } case R.id.menuEventos:{
                Intent intent = new Intent(ActividadActivity.this, CrearEventoActivity.class);
                intent.putExtra("Update",0);
                startActivity(intent);
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            new  AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setMessage("Este permiso es requerido")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ActividadActivity.this,new String[]{ Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }

    }

}