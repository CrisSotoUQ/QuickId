package com.grade.quickid;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.grade.quickid.model.actividades.ActividadActivity;
import com.grade.quickid.model.personas.LoginActivity;
import com.grade.quickid.model.actividades.QRScanner;


public class MainActivity extends AppCompatActivity {
private Button btnEscanner;
private Button btnLogOut;
private Button btnActividades;
private TextView emailtxt,tipotxt;
private int CAMERA_PERMISSION_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailtxt = (TextView) findViewById(R.id.UsertextView);
        tipotxt = (TextView) findViewById( R.id.TypeOfUsertextView);
        btnEscanner = (Button) findViewById(R.id.btnEscanner);
        btnLogOut= (Button) findViewById(R.id.btnCerrarSesion);
        btnActividades = (Button) findViewById(R.id.btnActividades);

        btnActividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActividadActivity.class);
                startActivity(intent);
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor prefs= (SharedPreferences.Editor) getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit();
                prefs.clear();
                prefs.apply();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



        btnEscanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this,"Permiso Aceptado",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, QRScanner.class);

                    startActivity(intent);
                }else{
                    requestCameraPermission();
                }

            }
        });
    }

    private void setup(String email,String tipo) {
        setTitle("Opciones");
        emailtxt.setText(email);
        tipotxt.setText(tipo);
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            new  AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setMessage("Este permiso es requerido")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{ Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==CAMERA_PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permiso Aceptado",Toast.LENGTH_SHORT);
            }else{
                Toast.makeText(this, "Permiso Denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


}