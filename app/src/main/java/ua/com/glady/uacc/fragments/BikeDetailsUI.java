package ua.com.glady.uacc.fragments;

import ua.com.glady.uacc.R;
import static ua.com.glady.uacc.tools.ToolsView.getInt;

import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.vehicle.AVehicle;
import ua.com.glady.uacc.model.vehicle.Motorcycle;
import ua.com.glady.uacc.tools.ToolsView;

/**
 * UI for direct calculation details for bikes
 * Created by vgl on 19.03.2015.
 */
public class BikeDetailsUI extends ABaseFragmentUI {


    @Override
    public AVehicle getVehicle() {
        Motorcycle result = new Motorcycle(sPref, res, excisesRegistry);

        int engineType = Constants.UNDEFINED;
        if (ToolsView.isChecked(view, R.id.rbBikeEngineGasoline))
            engineType = Constants.ENG_GASOLINE;
        if (ToolsView.isChecked(view, R.id.rbBikeEngineOther))
            engineType = Constants.ENG_OTHER;

        result.getEngine().setType(engineType);
        result.getEngine().setVolume(getInt(view, R.id.edBikeVolume, Constants.UNDEFINED));

        result.setBasicPrice(getInt(view, R.id.edBikePrice, Constants.UNDEFINED));

        return result;
    }
}