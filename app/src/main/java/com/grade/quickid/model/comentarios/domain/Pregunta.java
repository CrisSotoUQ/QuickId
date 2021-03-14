package com.grade.quickid.model.comentarios.domain;

public class Pregunta {
    private String idPregunta;
    private String idCOmentario;
    private String texto;

    public Pregunta(String idPregunta, String idCOmentario, String texto) {
        this.idPregunta = idPregunta;
        this.idCOmentario = idCOmentario;
        this.texto = texto;
    }
}
