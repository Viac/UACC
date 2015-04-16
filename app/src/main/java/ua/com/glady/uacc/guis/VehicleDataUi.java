package ua.com.glady.uacc.guis;

import android.content.Context;
import android.widget.LinearLayout;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.calculators.UaccPreferences;
import ua.com.glady.uacc.model.types.Age;
import ua.com.glady.uacc.model.types.Engine;
import ua.com.glady.uacc.model.types.VehicleType;
import ua.com.glady.uacc.model.vehicle.AVehicle;
import ua.com.glady.uacc.tools.FlatListPicker;
import ua.com.glady.uacc.tools.NumberEdit;

/**
 * This UI defines interface for the vehicle data
 *
 * Created by Slava on 08.04.2015.
 */
public abstract class VehicleDataUi extends LinearLayout {

    protected final Context context;
    protected final ExcisesRegistry excisesRegistry;

    protected VehicleType vehicleType;

    // Typically used controls
    private FlatListPicker flEngine;
    private FlatListPicker flAge;
    private NumberEdit edVolume;
    private NumberEdit edPrice;

    protected LinearLayout llBase;

    protected int[] availableEngines;
    protected int[] availableAges;

    private UaccPreferences.VehiclePreferences vehiclePreferences;

    protected final void addEngineList(){
        String[] enginesCaptions = new String[availableEngines.length];

        for (int i = 0; i < availableEngines.length; i++){
            enginesCaptions[i] = context.getString(Engine.getNameResId(availableEngines[i]));
        }

        flEngine = CustomControlsBuilder.createFlatListPicker(context, enginesCaptions, context.getString(R.string.engine));
        llBase.addView(flEngine);
    }

    protected final void addAgeList(){
        String[] agesCaptions = new String[availableAges.length];
        for (int i = 0; i < availableAges.length; i++){
            agesCaptions[i] = Age.getStringValue(availableAges[i], context.getResources());
        }

        flAge = CustomControlsBuilder.createFlatListPicker(context, agesCaptions, context.getString(R.string.Age));
        llBase.addView(flAge);
    }


    protected final void addVolumeAndPrice(){

        LinearLayout llVolumePrice = new LinearLayout(context);
        llVolumePrice.setWeightSum(1f);
        llVolumePrice.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        llVolumePrice.setOrientation(HORIZONTAL);
        llBase.addView(llVolumePrice);

        edVolume = CustomControlsBuilder.createNumberEdit(context,
                context.getString(R.string.volume_cm3), Constants.DEFAULT_PRICE, 0, Constants.ENGINE_MAX_BOUND);

        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        p1.weight = 0.5f;
        llVolumePrice.addView(edVolume, p1);

        edPrice = CustomControlsBuilder.createNumberEdit(context,
                context.getString(R.string.price),
                Constants.DEFAULT_PRICE, Constants.PRICE_MIN_BOUND, Constants.PRICE_MAX_BOUND);
        llVolumePrice.addView(edPrice, p1);
    }

    protected final int getEngineType(){
        return availableEngines[flEngine.getIndex()];
    }

    protected final int getAge(){
        return availableAges[flAge.getIndex()];
    }

    protected final int getVolume(){
        return edVolume.getValue();
    }

    protected final int getPrice(){
        return edPrice.getValue();
    }

    public VehicleDataUi(Context context, ExcisesRegistry excisesRegistry) {
        super(context);
        this.context = context;
        this.excisesRegistry = excisesRegistry;

        initializeConcreteVehicle();

        llBase = new LinearLayout(context);
        llBase.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        llBase.setOrientation(VERTICAL);
        this.addView(llBase);

        // now the time for subclasses to fill this UI
    }

    protected abstract void initializeConcreteVehicle();

    /**
     * Each subclass crates own vehicle object
     * @return vehicle object of concrete type
     */
    public abstract AVehicle getVehicle();



}
