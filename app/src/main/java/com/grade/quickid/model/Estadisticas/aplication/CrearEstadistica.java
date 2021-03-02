package com.grade.quickid.model.Estadisticas.aplication;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.grade.quickid.model.Estadisticas.domain.Estadistica;
import com.grade.quickid.model.Time;
import com.grade.quickid.model.eventos.domain.Evento;
import com.grade.quickid.model.registros.domain.Registro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.UUID;

public class CrearEstadistica {
    ValueEventListener mEstadisticaListener;
    DatabaseReference myRefEstadistica;

    public Object setearEstadisticas(DataSnapshot objSnapshot, Result result, String claveActPer, String idRegistro) {
        String idEvento = (String) objSnapshot.child("idEvento").getValue();
        Object estadistica = new Object();
        String fIni = (String) objSnapshot.child("fIni").getValue();
        mEstadisticaListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotEstadistica) {
                if (snapshotEstadistica.exists() ) {
                    for (DataSnapshot objSnapshot : snapshotEstadistica.getChildren()) {
                        Estadistica est = objSnapshot.getValue(Estadistica.class);
                        HashMap<String, String> hashDia = new HashMap<String, String>();
                        HashMap<String, Object> hashMes = new HashMap<String, Object>();
                        HashMap<String, Object> hashAnio = new HashMap<String, Object>();
                        StringTokenizer st = new StringTokenizer(fIni, "-"); //delimitador -
                        String dia = st.nextToken();
                        String mes = st.nextToken();
                        String anio = st.nextToken();
                        EstadisticaFecha anioEst = new EstadisticaFecha(anio,null);
                        EstadisticaFecha mesEst = new EstadisticaFecha(mes,null);
                        EstadisticaFecha diaEst = new EstadisticaFecha(dia,null);

                        anioEst.addChild(mesEst);

                    }
                } else {
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        FirebaseDatabase firebaseDatabase3 = FirebaseDatabase.getInstance();
        myRefEstadistica = firebaseDatabase3.getInstance().getReference().child("Estadistica");
        myRefEstadistica.orderByChild("idEvento").equalTo(idEvento).addValueEventListener(mEstadisticaListener);
        return estadistica;
    }

    public Object CrearObjetoEstadistica(String fecha, String idEvento) {
        Estadistica estadistica = new Estadistica();
        estadistica.setIdEstadistica(UUID.randomUUID().toString());
        return estadistica;
    }
}
