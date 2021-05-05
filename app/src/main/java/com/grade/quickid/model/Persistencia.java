package com.grade.quickid.model;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Clase que controla la persistencia de la sesion
 *
 * @author Cristian Camilo Soto
 */
public class Persistencia extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }


}
