package ua.com.glady.uacc.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.vehicle.AVehicle;

/**
 * This UI defines base of fragment for vehicle-specific UIs
 *
 * Created by vgl on 19.03.2015.
 */
public abstract class ABaseFragmentUI extends Fragment {

    SharedPreferences sPref;
    Resources res;
    ExcisesRegistry excisesRegistry;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        res = getActivity().getResources();
        sPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        excisesRegistry = ExcisesRegistry.getInstance();

        int layoutId = getArguments().getInt(Constants.LAYOUT_ID_TAG, Constants.UNDEFINED);

        view = inflater.inflate(layoutId, container, false);
        return view;
    }

    /**
     * Each subclass crates own vehicle object
     * @return vehicle object of concrete type
     */
    public abstract AVehicle getVehicle();

}