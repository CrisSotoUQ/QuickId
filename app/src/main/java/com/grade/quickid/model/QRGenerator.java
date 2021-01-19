package com.grade.quickid.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_generator);
        imgQR= findViewById(R.id.imageView_qr);
        textViewNombreActividad = (TextView) findViewById(R.id.textViewNombreActividad);
        btnSaveQr = (Button) findViewById(R.id.btn_save_qr);
        Intent intent = getIntent();
        final String actividad = intent.getStringExtra("idActividad");
        final String nombreActividad = intent.getStringExtra("nombre");
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
        BitmapDrawable bitmapDrawable= (BitmapDrawable) imgQR.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        File sdcard =  Environment.getExternalStorageDirectory();
        File directory = new File(sdcard.getAbsolutePath()+ "/Folder");

        if (!directory.exists()){
            directory.mkdir();
        }
        String filename =  String.format("%d.jpg",System.currentTimeMillis());
        File outfile = new File(directory,filename);
        try {
             outputStream = new FileOutputStream(outfile);
        }catch (Exception e){
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        try {
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }try {
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intent =  new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(outfile));
        sendBroadcast(intent);
        Toast.makeText(this, "Se ha guardado la imagen en galeria", Toast.LENGTH_LONG).show();
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