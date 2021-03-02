package com.grade.quickid.model.Estadisticas.domain;

import com.grade.quickid.model.Estadisticas.aplication.EstadisticaFecha;

import java.io.Serializable;
import java.util.HashMap;

public class Estadistica implements Serializable {
    private String idEstadistica;
    private String idEvento;
    private int contadorAsistentesFechaActual;
    private int contadorAsistentesHistorico;
    private  EstadisticaFecha estadisticaFecha = new EstadisticaFecha("",null);

    public EstadisticaFecha getEstadisticaFecha() {
        return estadisticaFecha;
    }

    public void setEstadisticaFecha(EstadisticaFecha estadisticaFecha) {
        this.estadisticaFecha = estadisticaFecha;
    }

    public Estadistica() {
    }

    public Estadistica(String idEstadistica, String idEvento, int contadorAsistentesFechaActual, int contadorAsistentesHistorico, EstadisticaFecha estadisticaFecha) {
        this.idEstadistica = idEstadistica;
        this.idEvento = idEvento;
        this.contadorAsistentesFechaActual = contadorAsistentesFechaActual;
        this.contadorAsistentesHistorico = contadorAsistentesHistorico;
        this.estadisticaFecha = estadisticaFecha;
    }

    public String getIdEstadistica() {
        return idEstadistica;
    }

    public void setIdEstadistica(String idEstadistica) {
        this.idEstadistica = idEstadistica;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public int getContadorAsistentesFechaActual() {
        return contadorAsistentesFechaActual;
    }

    public void setContadorAsistentesFechaActual(int contadorAsistentesFechaActual) {
        this.contadorAsistentesFechaActual = contadorAsistentesFechaActual;
    }

    public int getContadorAsistentesHistorico() {
        return contadorAsistentesHistorico;
    }

    public void setContadorAsistentesHistorico(int contadorAsistentesHistorico) {
        this.contadorAsistentesHistorico = contadorAsistentesHistorico;
    }
}
