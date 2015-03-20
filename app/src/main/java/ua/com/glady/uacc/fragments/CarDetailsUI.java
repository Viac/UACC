package ua.com.glady.uacc.fragments;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.vehicle.AVehicle;
import ua.com.glady.uacc.model.vehicle.Car;
import ua.com.glady.uacc.tools.ToolsView;

import static ua.com.glady.uacc.tools.ToolsView.getInt;
import static ua.com.glady.uacc.tools.ToolsView.isChecked;

/**
 * Implements vehicle details UI (fragment) for cars, including snow/golf cars and caravans
 * <p/>
 * Created by vgl on 19.03.2015.
 */
public class CarDetailsUI extends ABaseFragmentUI implements View.OnClickListener {

    @Override
    public void onStart(){
        super.onStart();
        CheckBox cbIsSpecialDesign = (CheckBox) view.findViewById(R.id.cbIsSpecialDesign);
        cbIsSpecialDesign.setOnClickListener(this);
        CheckBox cbIsCaravan = (CheckBox) view.findViewById(R.id.cbIsCaravan);
        cbIsCaravan.setOnClickListener(this);
    }

    @Override
    public AVehicle getVehicle() {
        Car result = new Car(sPref, res, excisesRegistry);

        if (isChecked(view, R.id.cbIsCaravan))
            result.setCaravan(true);

        if (isChecked(view, R.id.cbIsSpecialDesign))
            result.setSpecialDesign(true);

        int engineType = Constants.UNDEFINED;
        if (ToolsView.isChecked(view, R.id.rbCarDiesel))
            engineType = Constants.ENG_DIESEL;
        if (ToolsView.isChecked(view, R.id.rbCarElectric))
            engineType = Constants.ENG_ELECTRIC;
        if (ToolsView.isChecked(view, R.id.rbCarGasoline))
            engineType = Constants.ENG_GASOLINE;
        if (ToolsView.isChecked(view, R.id.rbCarOther))
            engineType = Constants.ENG_OTHER;
        result.getEngine().setType(engineType);
        result.getEngine().setVolume(getInt(view, R.id.edCarVolume, Constants.UNDEFINED));

        int age = Constants.UNDEFINED;
        if (ToolsView.isChecked(view, R.id.rbCarNew))
            age = Constants.AGE_0_YEARS;
        if (ToolsView.isChecked(view, R.id.rbCarLE5))
            age = Constants.AGE_5_YEARS - 1;
        if (ToolsView.isChecked(view, R.id.rbCarG5))
            age = Constants.AGE_5_YEARS + 1;
        result.setAge(age);

        // defining price
        result.setBasicPrice(getInt(view, R.id.edCarPrice, Constants.UNDEFINED));

        return result;
    }

    /**
     * Shows toast if input data is invalid
     */
    void showConflictWarning() {
        Context context = this.getActivity().getBaseContext();
        String message = res.getString(R.string.CaravanConflictsToSpecialDesign);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cbIsCaravan: {
                if (ToolsView.isChecked(view, R.id.cbIsSpecialDesign)) {
                    showConflictWarning();
                    CheckBox cb = (CheckBox) view.findViewById(R.id.cbIsSpecialDesign);
                    cb.setChecked(false);
                }
                break;
            }
            case R.id.cbIsSpecialDesign: {
                if (ToolsView.isChecked(view, R.id.cbIsCaravan)) {
                    showConflictWarning();
                    CheckBox cb = (CheckBox) view.findViewById(R.id.cbIsCaravan);
                    cb.setChecked(false);
                }
                break;
            }
        }
    }
}