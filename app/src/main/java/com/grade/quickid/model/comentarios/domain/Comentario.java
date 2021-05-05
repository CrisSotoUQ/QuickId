package com.grade.quickid.model.comentarios.domain;


/**
 * Clase que contiene el objeto para los comentarios
 *
 * @author Cristian Camilo Soto
 */
public class Comentario {
    private String idCOmentario;
    private String comentario;
    private String idUsuario;
    private String fechaComentario;


    public void setIdCOmentario(String idCOmentario) {
        this.idCOmentario = idCOmentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setFechaComentario(String fechaComentario) {
        this.fechaComentario = fechaComentario;
    }
}
