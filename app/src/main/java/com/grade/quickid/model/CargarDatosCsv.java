package com.grade.quickid.model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.grade.quickid.R;

public class CargarDatosCsv extends AppCompatActivity {
private Button btn_cargarCsv;
private Button btn_siguiente;
    Actividad receiveActividad;
    private int update;
    String imagenOriginal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_datos_csv);
        btn_cargarCsv = (Button) findViewById(R.id.buttonCsv);
        btn_siguiente = (Button) findViewById(R.id.btn_csvSiguiente);
        receiveActividad = (Actividad) getIntent().getSerializableExtra("Actividad");
        update = getIntent().getIntExtra("Update",0);
        imagenOriginal = getIntent().getStringExtra("Original");
        btn_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(CargarDatosCsv.this,ConfirmarEvento.class);
                act.putExtra("Actividad", receiveActividad);
                if(update !=0){
                    act.putExtra("Update",1);
                }
                startActivity(act);
            }
        });
        btn_cargarCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }
}