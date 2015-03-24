package ua.com.glady.uacc.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.vehicle.AVehicle;
import ua.com.glady.uacc.tools.InputFilterMinMax;

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

        prepareView();

        return view;
    }

    /**
     * Each subclass crates own vehicle object
     * @return vehicle object of concrete type
     */
    public abstract AVehicle getVehicle();

    protected void setMinMaxLimit(int editTextId, int min, int max){
        View v = view.findViewById(editTextId);
        if (v instanceof EditText)
            ((EditText) v).setFilters(new InputFilter[]{ new InputFilterMinMax(min, max)});
    }

    /**
     * Provides all required actions that required before show UI. Since Fragment's constructor
     * is parameters-free, we call this method in OnCreateView. By design each subclass can
     * fill this template with own set of commands
     */
    protected void prepareView(){
        // does nothing in common case, but we can't do it abstract - subclassed could have
        // no restrictions
    }

}