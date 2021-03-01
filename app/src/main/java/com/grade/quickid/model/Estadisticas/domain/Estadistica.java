package com.grade.quickid.model.Estadisticas.domain;

import java.io.Serializable;
import java.util.HashMap;

public class Estadistica implements Serializable {
    private String idEstadistica;
    private String idEvento;
    private int contadorAsistentesFechaActual;
    private int contadorAsistentesHistorico;
    private HashMap<String,HashMap<String,HashMap<String,String>>> fechas = new HashMap<String,HashMap<String,HashMap<String,String>>> ();
    public Estadistica() {
    }

    public Estadistica(String idEstadistica, String idEvento, int contadorAsistentesFechaActual, int contadorAsistentesHistorico, HashMap<String, HashMap<String, HashMap<String, String>>> fechas) {
        this.idEstadistica = idEstadistica;
        this.idEvento = idEvento;
        this.contadorAsistentesFechaActual = contadorAsistentesFechaActual;
        this.contadorAsistentesHistorico = contadorAsistentesHistorico;
        this.fechas = fechas;
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

    public HashMap<String, HashMap<String, HashMap<String, String>>> getFechas() {
        return fechas;
    }

    public void setFechas(HashMap<String, HashMap<String, HashMap<String, String>>> fechas) {
        this.fechas = fechas;
    }
}
