package com.grade.quickid.model;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.grade.quickid.model.registros.infraestructure.FragmentRegistros;
import com.grade.quickid.model.eventos.infraestructure.fragments.FragmentEventos;

public class FragmentPagerController extends FragmentPagerAdapter {
int numoftabs;

    public FragmentPagerController(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.numoftabs = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0 :
                return new FragmentEventos();
            case 1 :
                return new FragmentRegistros();
            default:
                return  null;
        }
    }

    @Override
    public int getCount() {
        return numoftabs;
    }

}
