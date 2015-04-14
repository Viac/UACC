package ua.com.glady.uacc.guis;

import android.content.Context;
import android.widget.LinearLayout;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.types.VehicleType;
import ua.com.glady.uacc.model.vehicle.AVehicle;
import ua.com.glady.uacc.model.vehicle.Motorcycle;
import ua.com.glady.uacc.tools.ToolsView;

/**
 * Implements motorcycle data UI
 *
 * Created by Slava on 10.04.2015.
 */
public class MotorcycleDataUi extends VehicleDataUi {

    public MotorcycleDataUi(Context context, ExcisesRegistry excisesRegistry) {

        super(context, excisesRegistry);

        LinearLayout lBase = new LinearLayout(context);
        this.addView(lBase);

        this.addEngineList();
        ToolsView.addSeparatorLine(context, llBase, 2,
                context.getResources().getColor(R.color.aqua_gray), 0, 8, 0, 8);
        this.addVolumeAndPrice();
    }

    @Override
    protected void initializeConcreteVehicle() {
        vehicleType = VehicleType.Bus;

        this.availableEngines = new int[2];
        availableEngines[0] = Constants.ENG_GASOLINE;
        availableEngines[1] = Constants.ENG_DIESEL;
    }

    @Override
    public AVehicle getVehicle() {
        Motorcycle result = new Motorcycle(context, excisesRegistry);

        result.getEngine().setType(getEngineType());
        result.getEngine().setVolume(this.getVolume());

        // defining price
        result.setBasicPrice(this.getPrice());

        return result;
    }

}
