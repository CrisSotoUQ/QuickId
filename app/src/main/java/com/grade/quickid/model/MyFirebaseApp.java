package com.grade.quickid.model;

import com.google.firebase.database.FirebaseDatabase;

public class MyFirebaseApp extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
