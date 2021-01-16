package com.grade.quickid.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.grade.quickid.MainActivity;
import com.grade.quickid.R;

import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRGenerator extends AppCompatActivity {
EditText txt_textoqr;
TextView textViewNombreActividad;
ImageView imgQR;
Button btnAtras;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_generator);
        imgQR= findViewById(R.id.imageView_qr);
        textViewNombreActividad = (TextView) findViewById(R.id.textViewNombreActividad);
        Intent intent = getIntent();
        final String actividad = intent.getStringExtra("idActividad");
        final String nombreActividad = intent.getStringExtra("nombre");
        textViewNombreActividad.setText(nombreActividad);
        titulo();
        generateQR(actividad);
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