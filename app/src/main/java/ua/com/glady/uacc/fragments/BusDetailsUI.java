package ua.com.glady.uacc.fragments;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.types.Age;
import ua.com.glady.uacc.model.vehicle.AVehicle;
import ua.com.glady.uacc.model.vehicle.Bus;
import ua.com.glady.uacc.tools.ToolsView;

import static ua.com.glady.uacc.tools.ToolsView.getInt;

/**
 * Implements vehicle details UI (fragment) for buses
 *
 * Created by vgl on 19.03.2015.
 */
public class BusDetailsUI extends ABaseFragmentUI {

    @Override
    public AVehicle getVehicle() {
        Bus result = new Bus(sPref, res, excisesRegistry);

        int engineType = Constants.UNDEFINED;
        if (ToolsView.isChecked(view, R.id.rbBusDiesel))
            engineType = Constants.ENG_DIESEL;
        if (ToolsView.isChecked(view, R.id.rbBusGasoline))
            engineType = Constants.ENG_GASOLINE;
        result.getEngine().setType(engineType);
        result.getEngine().setVolume(getInt(view, R.id.edBusVolume, Constants.UNDEFINED));

        int age = Constants.UNDEFINED;
        if (ToolsView.isChecked(view, R.id.rbBusNew))
            age =  Age.AGE_0_YEARS;
        if (ToolsView.isChecked(view, R.id.rbBusLE8))
            age =  Age.AGE_NOT_EXCEED_8_YEARS;
        if (ToolsView.isChecked(view, R.id.rbBusG8))
            age =  Age.AGE_EXCEED_8_YEARS;
        result.setAge(age);

        result.setBasicPrice(getInt(view, R.id.edBusPrice, Constants.UNDEFINED));

        return result;
    }
}
