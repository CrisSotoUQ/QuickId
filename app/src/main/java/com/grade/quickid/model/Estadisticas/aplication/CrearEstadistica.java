package com.grade.quickid.model.Estadisticas.aplication;

import com.google.firebase.database.DataSnapshot;
import com.google.zxing.Result;
import com.grade.quickid.model.Estadisticas.domain.Estadistica;

import java.util.UUID;

public class CrearEstadistica {

    public Object setearEstadisticas(DataSnapshot objSnapshot, Result result, String claveActPer, String idRegistro) {
        String idEvento = (String) objSnapshot.child("idEvento").getValue();
        Estadistica estadistica = new Estadistica();
        estadistica.setIdEstadistica(UUID.randomUUID().toString());
        estadistica.setIdEvento(idEvento);
        return estadistica;
    }

    public Object CrearObjetoEstadistica(String idEvento) {
        Estadistica estadistica = new Estadistica();
        estadistica.setIdEstadistica(UUID.randomUUID().toString());
        estadistica.setIdEvento(idEvento);
        return estadistica;
    }
}
