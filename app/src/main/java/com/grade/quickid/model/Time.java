package com.grade.quickid.model;

import java.text.SimpleDateFormat;

public class Time<dateString> {
    long date = System.currentTimeMillis();
public String fecha(){
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    String dateString = sdf.format(date);
    return dateString;
    }
    public String hora(){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String dateString = sdf.format(date);
        return dateString;
    }
}
