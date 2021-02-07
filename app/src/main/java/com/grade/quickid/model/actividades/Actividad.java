package com.grade.quickid.model.actividades;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Actividad implements Serializable  {
    public String idActividad;
    public String nombre;
    public String fIni;
    public String fFin;
    public String lugar;
    public double latitud;
    public double longitud;
    public String estadoActividad;//1 iniciada 2 pausada
    public String geolocStatus;
    public String cargueArchivoStatus;
    public String id_persona;
    public String horaIni;
    public String urlImagen;

    public Actividad() {
    }

    public Actividad(String idActividad, String nombre, String fIni, String fFin, String lugar, double latitud, double longitud,
                     String estadoActividad, String geolocStatus, String cargueArchivoStatus, String id_persona, String horaIni,
                     String urlImagen) {
        this.idActividad = idActividad;
        this.nombre = nombre;
        this.fIni = fIni;
        this.fFin = fFin;
        this.lugar = lugar;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estadoActividad = estadoActividad;
        this.geolocStatus = geolocStatus;
        this.cargueArchivoStatus = cargueArchivoStatus;
        this.id_persona = id_persona;
        this.horaIni = horaIni;
        this.urlImagen = urlImagen;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(String idActividad) {
        this.idActividad = idActividad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getfIni() {
        return fIni;
    }

    public void setfIni(String fIni) {
        this.fIni = fIni;
    }

    public double getLatitud() {
        return latitud;
    }

   public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public String getHoraIni() {
        return horaIni;
    }

    public void setHoraIni(String horaIni) {
        this.horaIni = horaIni;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getfFin() {
        return fFin;
    }

    public void setfFin(String fFin) {
        this.fFin = fFin;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getEstadoActividad() {
        return estadoActividad;
    }

    public void setEstadoActividad(String estadoActividad) {
        this.estadoActividad = estadoActividad; }

    public String getGeolocStatus() {
        return geolocStatus;
    }

    public void setGeolocStatus(String geolocStatus) {
        this.geolocStatus = geolocStatus;
    }

    public String getCargueArchivoStatus() {
        return cargueArchivoStatus;
    }

    public void setCargueArchivoStatus(String cargueArchivoStatus) {
        this.cargueArchivoStatus = cargueArchivoStatus;
    }

    public String getId_persona() {
        return id_persona;
    }

    public void setId_persona(String id_persona) {
        this.id_persona = id_persona;
    }
}