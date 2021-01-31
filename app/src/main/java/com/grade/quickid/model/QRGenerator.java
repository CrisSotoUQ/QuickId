package com.grade.quickid.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.grade.quickid.BuildConfig;
import com.grade.quickid.MainActivity;
import com.grade.quickid.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRGenerator extends AppCompatActivity {
EditText txt_textoqr;
TextView textViewNombreActividad;
ImageView imgQR;
Button btnSaveQr;
OutputStream outputStream;
final int REQUEST_CODE = 1;
private String nombreActividad;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_generator);
        imgQR= findViewById(R.id.imageView_qr);
        textViewNombreActividad = (TextView) findViewById(R.id.textViewNombreActividad);
        btnSaveQr = (Button) findViewById(R.id.btn_save_qr);
        Intent intent = getIntent();
        final String actividad = intent.getStringExtra("idActividad");
        nombreActividad = intent.getStringExtra("nombre");
        textViewNombreActividad.setText(nombreActividad);
        titulo();

        // genera el Qrcode
        generateQR(actividad);

        btnSaveQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(QRGenerator.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    try {
                        saveToGallery();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    pedirPermisos();
                }

            }
        });
    }

    private void pedirPermisos() {
        ActivityCompat.requestPermissions(QRGenerator.this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        },1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode ==  REQUEST_CODE){

            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                try {
                    saveToGallery();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this, "porfavor otorgar permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveToGallery() throws IOException {
        Drawable drawable = imgQR.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        try {
            File file =  new File (getApplicationContext().getFilesDir(),File.separator+"Evento.jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true,false);
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(),BuildConfig.APPLICATION_ID+".provider",file);
            intent.putExtra(Intent.EXTRA_STREAM,photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setType("image/jpg");
            startActivity(Intent.createChooser(intent,"Share image via "));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void titulo() {
        this.setTitle("Generador QR");
    }

    private void generateQR(String actividad) {
        QRGEncoder qrgEncoder = new QRGEncoder(actividad,null, QRGContents.Type.TEXT,2000);
        try {
            Bitmap qrBits = qrgEncoder.getBitmap();
            imgQR.setImageBitmap(qrBits);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}