package com.grade.quickid.model;

import android.text.format.DateFormat;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Registro {
    String idRegistro;
    String fechaRegistro;
    String idActividad;
    String horaRegistro;
    String idPersona;
    // una vez se realize el registro se completan los valores del objeto actividad
    // con el fin de mas celeridad al llenar las listas RecyclerView
    String nombreActividad;
    String lugarActividad;
    String imagenActividad;

    public Registro() {
    }
    public Registro(String idRegistro, String fechaRegistro, String idActividad, String horaRegistro, String idPersona, String nombreActividad, String lugarActividad, String imagenActividad) {
        this.idRegistro = idRegistro;
        this.fechaRegistro = fechaRegistro;
        this.idActividad = idActividad;
        this.horaRegistro = horaRegistro;
        this.idPersona = idPersona;
        this.nombreActividad = nombreActividad;
        this.lugarActividad = lugarActividad;
        this.imagenActividad = imagenActividad;
    }

    public String getImagenActividad() {
        return imagenActividad;
    }

    public void setImagenActividad(String imagenActividad) {
        this.imagenActividad = imagenActividad;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getHoraRegistro() {
        return horaRegistro;
    }

    public void setHoraRegistro(String horaRegistro) {
        this.horaRegistro = horaRegistro;
    }

    public String getNombreActividad() {
        return nombreActividad;
    }

    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }

    public String getLugarActividad() {
        return lugarActividad;
    }

    public void setLugarActividad(String lugarActividad) {
        this.lugarActividad = lugarActividad;
    }

    public String getIdPersona() {
        return idPersona;
    }
    public void setIdPersona(String idPersona) {
        this.idPersona = idPersona;
    }

    public String getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(String idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(String idActividad) {
        this.idActividad = idActividad;
    }
}