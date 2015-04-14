package ua.com.glady.uacc.guis;

import android.content.Context;
import android.widget.LinearLayout;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.types.Age;
import ua.com.glady.uacc.model.types.VehicleType;
import ua.com.glady.uacc.model.vehicle.AVehicle;
import ua.com.glady.uacc.model.vehicle.Bus;
import ua.com.glady.uacc.tools.ToolsView;

/**
 * Implements bus details UI
 *
 * Created by Slava on 10.04.2015.
 */
public class BusDataUi extends VehicleDataUi {

    public BusDataUi(Context context, ExcisesRegistry excisesRegistry) {

        super(context, excisesRegistry);

        LinearLayout lBase = new LinearLayout(context);
        this.addView(lBase);

        this.addEngineList();
        ToolsView.addSeparatorLine(context, llBase, 2,
                context.getResources().getColor(R.color.aqua_gray), 0, 8, 0, 0);

        this.addAgeList();
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

        this.availableAges = new int[3];
        availableAges[0] = Age.AGE_0_YEARS;
        availableAges[1] = Age.AGE_NOT_EXCEED_8_YEARS;
        availableAges[2] = Age.AGE_EXCEED_8_YEARS;
    }

    @Override
    public AVehicle getVehicle() {
        Bus result = new Bus(context, excisesRegistry);

        result.getEngine().setType(getEngineType());
        result.getEngine().setVolume(this.getVolume());

        result.setAge(this.getAge());

        // defining price
        result.setBasicPrice(this.getPrice());

        return result;
    }

}
