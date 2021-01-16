package com.grade.quickid.model;

import android.text.format.DateFormat;

import java.util.Date;
// en esta clase se agrupan las actividades creadas por los usuarios
public class ActividadPersona {
    Persona idPersona;
    Actividad idActividad;
    Date fecha;

    public ActividadPersona() {
    }

    public Persona getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Persona idPersona) {
        this.idPersona = idPersona;
    }

    public Actividad getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(Actividad idActividad) {
        this.idActividad = idActividad;
    }

    public Date getFecha() {
        Date d = new Date();
        CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());
        return d;
    }


    public void setFecha(Date fecha) {

    }
}
