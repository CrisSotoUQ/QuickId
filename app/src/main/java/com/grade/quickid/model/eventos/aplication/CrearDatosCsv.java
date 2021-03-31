package com.grade.quickid.model.eventos.aplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grade.quickid.BuildConfig;
import com.grade.quickid.model.eventos.domain.Evento;
import com.grade.quickid.model.eventos.infraestructure.fragments.FragmentEventos;
import com.grade.quickid.model.personas.domain.Persona;
import com.grade.quickid.model.registros.domain.Registro;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Clase que se encarga de generar los datos csv correspondientes a cada evento
 */
public class CrearDatosCsv {
    DatabaseReference myRefDatosPersonaEvento ;
    DatabaseReference myRefDatosRegistroEvento ;
    ValueEventListener  mEventListnerRegistroEvento ;
    ValueEventListener mEventListnerPersonaEvento ;
    int contador = 0;
    public  void CrearDatosCsv(Evento evento, Context context){
        StringBuilder data = new StringBuilder();
        ValueEventListener valueEventListenerRegistroEvento = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    data.append("Nombre Evento: " + evento.getNombre());
                    data.append("\n" + "Lugar Evento: " + evento.getLugar());
                    data.append("\n");
                    data.append("\n");
                    data.append("\n" + "Numero ,   Correo,         Apellido,          Nombre,           Fecha,   Hora entrada");

                    for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                        Registro ra = objSnapshot.getValue(Registro.class);

                        ValueEventListener valueEventListenerPersonaEvento = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                contador = contador+1;
                                for (DataSnapshot objSnapshot : snapshot1.getChildren()) {
                                    Persona per = objSnapshot.getValue(Persona.class);
                                    data.append("\n" + String.valueOf(contador) + "," + String.valueOf(per.getCorreo()) + "," + String.valueOf(per.getApellido())
                                            + "," + String.valueOf(per.getNombre()) + "," + String.valueOf(ra.getFechaRegistro())
                                            + "," + String.valueOf(ra.getHoraRegistro()));
                                }
                                try {

                                    if (data != null) {
                                        String formato = "yyyy-MM-dd";
                                        Calendar calendar = Calendar.getInstance();
                                        Date date = calendar.getTime();
                                        SimpleDateFormat sdf;
                                        sdf = new SimpleDateFormat(formato);
                                        sdf.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
                                        sdf.format(date);
                                        FileOutputStream out = context.openFileOutput(evento.getNombre()+".csv", Context.MODE_PRIVATE);
                                        out.write((data.toString()).getBytes());
                                        out.close();
                                        Context auxContext = context;
                                        File filelocation = new File(context.getFilesDir(), evento.getNombre()+".csv");
                                        Uri path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", filelocation);

                                        Intent fileIntent = new Intent(Intent.ACTION_SEND);
                                        fileIntent.setType("text/csv");
                                        fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Reporte : "+evento.getNombre()+" "+ sdf.format(date));
                                        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                                        fileIntent.putExtra(Intent.EXTRA_TEXT, "QuickId.");
                                        context.startActivity(Intent.createChooser(fileIntent, "Send mail"));

                                        myRefDatosPersonaEvento.removeEventListener(mEventListnerPersonaEvento);
                                        myRefDatosRegistroEvento.removeEventListener(mEventListnerRegistroEvento);
                                    }

                                    myRefDatosPersonaEvento.removeEventListener(mEventListnerPersonaEvento);
                                    myRefDatosRegistroEvento.removeEventListener(mEventListnerRegistroEvento);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                myRefDatosPersonaEvento.removeEventListener(mEventListnerPersonaEvento);
                                myRefDatosRegistroEvento.removeEventListener(mEventListnerRegistroEvento);
                            }
                        };
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference myRefDatosPersonaEvento = firebaseDatabase.getInstance().getReference().child("Persona");
                        myRefDatosPersonaEvento.orderByChild("id").equalTo(ra.getIdPersona()).addValueEventListener(valueEventListenerPersonaEvento);
                        mEventListnerPersonaEvento = valueEventListenerPersonaEvento;
                    }

                }else{
                    FragmentEventos.showAlertDialog(evento);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase firebaseDatabase3 = FirebaseDatabase.getInstance();
        myRefDatosRegistroEvento = firebaseDatabase3.getInstance().getReference().child("Registro");
        myRefDatosRegistroEvento.orderByChild("idEvento").equalTo(evento.getIdEvento()).addValueEventListener(valueEventListenerRegistroEvento);
        mEventListnerRegistroEvento = valueEventListenerRegistroEvento;

    }

}
