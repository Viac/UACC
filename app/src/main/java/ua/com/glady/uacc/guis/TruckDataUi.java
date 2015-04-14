package ua.com.glady.uacc.guis;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.types.Age;
import ua.com.glady.uacc.model.types.VehicleType;
import ua.com.glady.uacc.model.vehicle.AVehicle;
import ua.com.glady.uacc.model.vehicle.Truck;
import ua.com.glady.uacc.tools.FlatListPicker;
import ua.com.glady.uacc.tools.ToolsView;

/**
 * Implements trucks data UI
 *
 * Created by Slava on 10.04.2015.
 */
public class TruckDataUi extends VehicleDataUi {

    private final FlatListPicker flGrossWeight;
    private final CheckBox cbIsDumper;

    protected String[] availableGrossWeight;

    public TruckDataUi(Context context, ExcisesRegistry excisesRegistry) {

        super(context, excisesRegistry);

        LinearLayout lBase = new LinearLayout(context);
        this.addView(lBase);

        this.addEngineList();
        ToolsView.addSeparatorLine(context, llBase, 2,
                context.getResources().getColor(R.color.aqua_gray), 0, 8, 0, 0);

        this.addAgeList();
        ToolsView.addSeparatorLine(context, llBase, 2,
                context.getResources().getColor(R.color.aqua_gray), 0, 8, 0, 0);


        availableGrossWeight =  new String[3];
        availableGrossWeight[0] = context.getString(R.string.GwNotExceed5);
        availableGrossWeight[1] = context.getString(R.string.Gw5to20);
        availableGrossWeight[2] = context.getString(R.string.GwExceed20);

        flGrossWeight = CustomControlsBuilder.createFlatListPicker(context, availableGrossWeight,
                context.getString(R.string.GrossWeight));
        llBase.addView(flGrossWeight);
        ToolsView.addSeparatorLine(context, llBase, 2,
                context.getResources().getColor(R.color.aqua_gray), 0, 8, 0, 8);


        this.addVolumeAndPrice();
        ToolsView.addSeparatorLine(context, llBase, 2,
                context.getResources().getColor(R.color.aqua_gray), 0, 0, 0, 0);

        cbIsDumper = new CheckBox(context);
        cbIsDumper.setText(context.getString(R.string.Dumper));
        cbIsDumper.setScaleX(0.75f);
        cbIsDumper.setScaleY(0.75f);
        cbIsDumper.setTextAppearance(context, R.style.flat_list_item);

        llBase.addView(cbIsDumper);
    }

    @Override
    protected void initializeConcreteVehicle() {
        vehicleType = VehicleType.Truck;

        this.availableEngines = new int[3];
        availableEngines[0] = Constants.ENG_GASOLINE;
        availableEngines[1] = Constants.ENG_DIESEL;
        availableEngines[2] = Constants.ENG_OTHER;

        this.availableAges = new int[4];
        availableAges[0] = Age.AGE_0_YEARS;
        availableAges[1] = Age.AGE_NOT_EXCEED_5_YEARS;
        availableAges[2] = Age.AGE_EXCEED_5_YEARS;
        availableAges[3] = Age.AGE_EXCEED_8_YEARS;
    }

    @Override
    public AVehicle getVehicle() {
        Truck result = new Truck(context, excisesRegistry);

        if (cbIsDumper.isChecked())
            result.setDumper(true);

        int grossWeightIdx = flGrossWeight.getIndex();
        if (grossWeightIdx == 0)
            result.setGrossWeight(Constants.WEIGHT_5T - 1);
        if (grossWeightIdx == 1)
            result.setGrossWeight(Constants.WEIGHT_5T + 1);
        if (grossWeightIdx == 2)
            result.setGrossWeight(Constants.WEIGHT_20T + 1);

        result.getEngine().setType(getEngineType());
        result.getEngine().setVolume(this.getVolume());
        result.setAge(this.getAge());
        result.setBasicPrice(this.getPrice());

        return result;
    }

}
