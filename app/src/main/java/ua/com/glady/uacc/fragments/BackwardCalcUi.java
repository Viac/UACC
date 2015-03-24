package ua.com.glady.uacc.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.INotifyEvent;
import ua.com.glady.uacc.model.calculators.BcPreferences;
import ua.com.glady.uacc.tools.InputFilterMinMax;
import ua.com.glady.uacc.tools.ToolsView;

import static ua.com.glady.uacc.R.layout.backward_calc;

/**
 * This frame used to get data to run reverse calculator (in fact - final price)
 * Typically results of this calculation has a list of engine volume (min..max)
 * Each type of the vehicle has own engine range.
 *
 * Created by vgl on 19.03.2015.
 */
public class BackwardCalcUi extends Fragment implements View.OnClickListener {

    private View view;

    // Each vehicle type has own set of preferences (engine range, step)
    // So each time this object must be assigned depending on vehicle type
    private BcPreferences bcPreferences = null;

    public void setBcPreferences(BcPreferences bcPreferences) {
        this.bcPreferences = bcPreferences;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(backward_calc, container, false);

        // Set up limits for price
        EditText ed = (EditText) view.findViewById(R.id.edReversePrice);
        ed.setFilters(new InputFilter[]{ new InputFilterMinMax(
                Constants.PRICE_MIN_BOUND, Constants.PRICE_MAX_BOUND)});

        SharedPreferences sPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        Bundle b = getArguments();
        int minVolume = b.getInt("minVolume");
        int maxVolume = b.getInt("maxVolume");
        int stepVolume = b.getInt("stepVolume");

        String minVolumeKey = b.getString("minVolumeKey");
        String maxVolumeKey = b.getString("maxVolumeKey");
        String stepVolumeKey = b.getString("stepVolumeKey");

        bcPreferences = new BcPreferences(sPref, minVolume, maxVolume, stepVolume,
                minVolumeKey, maxVolumeKey, stepVolumeKey);

        ImageButton btEditPreferences = (ImageButton) view.findViewById(R.id.btEditPreferences);
        btEditPreferences.setOnClickListener(this);

        return view;
    }

    /**
     * Updates UI with preferences-based information about engine volume range
     */
    public void invalidatePreferencesInfo() {
        String template = getResources().getString(R.string.BackwardCalcPreferencesTemplate);

        String uiText = String.format(template, bcPreferences.minVolume, bcPreferences.maxVolume, bcPreferences.stepVolume);
        TextView tv = (TextView) view.findViewById(R.id.tvReverseEngineRange);
        tv.setText(uiText);
    }

    public void onStart() {
        super.onStart();
        invalidatePreferencesInfo();
    }

    /**
     * @return UI final price value, UNDEFINED if wrong
     */
    public int getPrice() {
        return ToolsView.getInt(view, R.id.edReversePrice, Constants.UNDEFINED);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btEditPreferences) {
            runPreferencesEditorUi();
        }
    }

    /**
     * Runs popup dialog frame where used can set-up engine range.
     *
     * Editing of the engine preferences implemented as separate frame
     * Standard Android pref. frame wasn't in use due to simplify users experience.
     */
    private void runPreferencesEditorUi() {
        final BcPreferencesDialog dialog = new BcPreferencesDialog();

        // Need to update data on this view
        dialog.onSave = new INotifyEvent() {
            @Override
            public void onEvent() {
                bcPreferences.setValues(dialog.minEngineDefaultValue,
                        dialog.maxEngineDefaultValue,
                        dialog.stepEngineDefaultValue);
                invalidatePreferencesInfo();
            }
        };

        dialog.minEngineKey = bcPreferences.minVolumeKey;
        dialog.minEngineDefaultValue = bcPreferences.minVolume;

        dialog.maxEngineKey = bcPreferences.maxVolumeKey;
        dialog.maxEngineDefaultValue = bcPreferences.maxVolume;

        dialog.stepEngineKey = bcPreferences.stepVolumeKey;
        dialog.stepEngineDefaultValue = bcPreferences.stepVolume;

        dialog.show(this.getFragmentManager(), "enginePreferences");
    }
}