package com.grade.quickid.model.registros.domain;

public class Registro {
    String idRegistro;
    String fechaRegistro;
    String idEvento;
    String horaRegistro;
    String idPersona;
    // una vez se realize el registro se completan los valores del objeto Evento
    // con el fin de mas celeridad al llenar las listas RecyclerView
    String nombreEvento;
    String lugarEvento;
    String imagenEvento;
    String visibilidad;
    //campo para poder filtrar EventoPersona en un mismo query
    String idAct_idPer;

    public Registro(String idRegistro, String fechaRegistro, String idEvento, String horaRegistro, String idPersona,
                    String nombreEvento, String lugarEvento, String imagenEvento, String visibilidad, String idAct_idPer) {
        this.idRegistro = idRegistro;
        this.fechaRegistro = fechaRegistro;
        this.idEvento = idEvento;
        this.horaRegistro = horaRegistro;
        this.idPersona = idPersona;
        this.nombreEvento = nombreEvento;
        this.lugarEvento = lugarEvento;
        this.imagenEvento = imagenEvento;
        this.visibilidad = visibilidad;
        this.idAct_idPer = idAct_idPer;
    }

    public Registro() { }
    public String getVisibilidad() {
        return visibilidad;
    }

    public void setVisibilidad(String visibilidad) {
        this.visibilidad = visibilidad;
    }

    public void setIdAct_idPer(String idAct_idPer) {
        this.idAct_idPer = idAct_idPer;
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

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public String getLugarEvento() {
        return lugarEvento;
    }

    public void setLugarEvento(String lugarEvento) {
        this.lugarEvento = lugarEvento;
    }

    public String getImagenEvento() {
        return imagenEvento;
    }

    public void setImagenEvento(String imagenEvento) {
        this.imagenEvento = imagenEvento;
    }

    public String getIdAct_idPer() {
        return idAct_idPer;
    }
}