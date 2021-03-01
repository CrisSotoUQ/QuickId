package com.grade.quickid.model.personas.domain;

public class Persona {
    public String id;
    public String nombre;
    public String apellido;
    public String correo;
    public String telefono;
    public String genero;
    public String tipo;
    public String imagenUri;
    public Persona() {
    }

    public Persona(String id, String nombre, String apellido, String correo, String telefono, String genero, String tipo, String imagenUri) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.genero = genero;
        this.tipo = tipo;
        this.imagenUri = imagenUri;
    }

    public void setImagenUri(String imagenUri) {
        this.imagenUri = imagenUri;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return  nombre+' '+apellido;
    }
}
