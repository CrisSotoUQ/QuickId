package com.grade.quickid.model.comentarios.domain;

import java.util.ArrayList;

public class Comentario {
    private String idCOmentario;
    private String comentario;
    private int factible;
    private String idUsuario;
    private String fechaComentario;
    ArrayList<Pregunta> preguntas = new ArrayList<Pregunta>();

    public String getIdCOmentario() {
        return idCOmentario;
    }

    public void setIdCOmentario(String idCOmentario) {
        this.idCOmentario = idCOmentario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getFactible() {
        return factible;
    }

    public void setFactible(int factible) {
        this.factible = factible;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFechaComentario() {
        return fechaComentario;
    }

    public void setFechaComentario(String fechaComentario) {
        this.fechaComentario = fechaComentario;
    }
}
