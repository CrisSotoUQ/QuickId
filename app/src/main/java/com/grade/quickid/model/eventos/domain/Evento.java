package com.grade.quickid.model.eventos.domain;

import java.io.Serializable;
import java.util.HashMap;

public class Evento implements Serializable  {
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
    public HashMap<String,String> listaPersonas = new HashMap<>();
    public Evento() {
    }

    public Evento(String idActividad, String nombre, String fIni, String fFin, String lugar,
                  double latitud, double longitud, String estadoActividad, String geolocStatus,
                  String cargueArchivoStatus, String id_persona, String horaIni, String urlImagen,
                  HashMap<String, String> listaPersonas) {
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
        this.listaPersonas = listaPersonas;
    }

    public HashMap<String, String> getListaPersonas() {
        return listaPersonas;
    }

    public void setListaPersonas(String a,String b) {
        listaPersonas.put("correo:"+a,b);
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