package com.grade.quickid.model;

import java.text.SimpleDateFormat;

/**
 * Clase que controla el tiempo de la aplicacion
 *
 * @author Cristian Camilo Soto
 */
public class Time<dateString> {
    long date = System.currentTimeMillis();
public String fecha(){
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    String dateString = sdf.format(date);
    return dateString;
    }
    public String hora(){
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
        String dateString = sdf.format(date);
        return dateString;
    }
}
