package ua.com.glady.uacc.fragments;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import ua.com.glady.uacc.R;
import static ua.com.glady.uacc.model.Constants.*;

import ua.com.glady.uacc.model.types.Age;
import ua.com.glady.uacc.model.vehicle.AVehicle;
import ua.com.glady.uacc.model.vehicle.Truck;
import ua.com.glady.uacc.tools.ToolsView;

import static ua.com.glady.uacc.tools.ToolsView.getInt;
import static ua.com.glady.uacc.tools.ToolsView.isChecked;

/**
 * Implements vehicle details UI (fragment) for trucks
 *
 * Created by vgl on 19.03.2015.
 */
public class TruckDetailsUI extends ABaseFragmentUI implements View.OnClickListener {

    @Override
    public void onStart(){
        super.onStart();
        RadioButton rbEngineOther = (RadioButton) view.findViewById(R.id.rbTruckEngineOther);
        rbEngineOther.setOnClickListener(this);
    }

    @Override
    public AVehicle getVehicle() {
        Truck result = new Truck(sPref, res, excisesRegistry);

        if (isChecked(view, R.id.cbDumper))
            result.setDumper(true);

        int engineType = UNDEFINED;
        if (ToolsView.isChecked(view, R.id.rbTrackEngineDiesel))
            engineType = ENG_DIESEL;
        if (ToolsView.isChecked(view, R.id.rbTrackEngineGasoline))
            engineType = ENG_GASOLINE;
        if (ToolsView.isChecked(view, R.id.rbTruckEngineOther))
            engineType = ENG_OTHER;
        result.getEngine().setType(engineType);
        result.getEngine().setVolume(getInt(view, R.id.edTrackVolume, UNDEFINED));

        int age = UNDEFINED;
        if (ToolsView.isChecked(view, R.id.rbTrackAgeNew))
            age = Age.AGE_0_YEARS;
        if (ToolsView.isChecked(view, R.id.rbTrackAgeLE5))
            age = Age.AGE_NOT_EXCEED_5_YEARS;
        if (ToolsView.isChecked(view, R.id.rbTrackG5LE8))
            age = Age.AGE_EXCEED_5_YEARS;
        if (ToolsView.isChecked(view, R.id.rbTrackAgeG8))
            age = Age.AGE_EXCEED_8_YEARS;
        result.setAge(age);

        if (isChecked(view, R.id.rbTrackGrossWNotExceed5))
            result.setGrossWeight(WEIGHT_5T - 1);
        if (isChecked(view, R.id.rbTrackGrossW5To20))
            result.setGrossWeight(WEIGHT_5T + 1);
        if (isChecked(view, R.id.rbTrackGrossWExceed20))
            result.setGrossWeight(WEIGHT_20T + 1);

        // defining price
        result.setBasicPrice(getInt(view, R.id.edTrackBasicPrice, UNDEFINED));

        return result;
    }

    void showConflictWarning() {
        Context context = this.getActivity().getBaseContext();
        String message = res.getString(R.string.TruckEngineTypeConflict);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rbTruckEngineOther: {
                if (!ToolsView.isChecked(view, R.id.cbDumper)) {
                    showConflictWarning();
                    CheckBox cb = (CheckBox) view.findViewById(R.id.cbDumper);
                    cb.setChecked(true);
                }
                break;
            }
        }
    }
}
