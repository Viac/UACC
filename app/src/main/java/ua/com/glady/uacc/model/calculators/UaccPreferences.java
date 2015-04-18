package ua.com.glady.uacc.model.calculators;

import android.content.Context;
import android.content.SharedPreferences;

import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.types.VehicleType;

/**
 * Class provides access to application preferences
 *
 * Created by Slava on 09.04.2015.
 */
public class UaccPreferences {

    private static final String DEFAULT_ACTIVE_PAGE = "DefaultActivePage";

    public class VehiclePreferences{
        public int lowVolume;
        public int highVolume;
        public int stepVolume;
    }

    private final SharedPreferences sharedPreferences;

    private String[] keys;
    private int[] defaults;

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(
                context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
    }

    public UaccPreferences(Context context, VehicleType vehicleType){
        this.sharedPreferences = getSharedPreferences(context);

        String[] carKeys = {"RcCarLowVolume", "RcCarHighVolume", "RcCarStepVolume"};
        int[] carDefaults = {800, 4200, 100};

        String[] busKeys = {"RcBusLowVolume", "RcBusHighVolume", "RcBusStepVolume", "RcBusDefaultVolume"};
        int[] busDefaults = {1500, 5500, 250};

        String[] truckKeys = {"RcTruckLowVolume", "RcTruckHighVolume", "RcTruckStepVolume", "RcTruckDefaultVolume"};
        int[] truckDefaults = {2500, 8500, 250};

        String[] motorcycleKeys = {"RcBikeLowVolume", "RcBikeHighVolume", "RcBikeStepVolume", "RcBikeDefaultVolume"};
        int[] motorcycleDefaults = {50, 500, 50};

        switch (vehicleType){
            case Car:
                keys = carKeys;
                defaults = carDefaults;
                break;
            case Bus:
                keys = busKeys;
                defaults = busDefaults;
                break;
            case Truck:
                keys = truckKeys;
                defaults = truckDefaults;
                break;
            case Motorcycle:
                keys = motorcycleKeys;
                defaults = motorcycleDefaults;
                break;
        }
    }

    public VehiclePreferences getVehiclePreferences(){
        VehiclePreferences result = new VehiclePreferences();
        result.lowVolume = sharedPreferences.getInt(keys[0], defaults[0]);
        result.highVolume = sharedPreferences.getInt(keys[1], defaults[1]);
        result.stepVolume = sharedPreferences.getInt(keys[2], defaults[2]);
        return result;
    }

    public void setVehiclePreferences(int lowVolume, int highVolume, int stepVolume){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(keys[0], lowVolume);
        edit.putInt(keys[1], highVolume);
        edit.putInt(keys[2], stepVolume);
        edit.apply();
    }

    /**
     * Saves value of active index
     * @param context context to retrieve preferences
     * @param activePageIndex new value
     */
    public static void setActivePage(Context context, int activePageIndex){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(DEFAULT_ACTIVE_PAGE, activePageIndex);
        edit.apply();
    }

    /**
     * Returns value of active page stored in preferences
     * @param context context to retrieve preferences
     * @param defaultValue obvious, isn't it?
     * @return value stored in preferences
     */
    public static int getActivePage(Context context, int defaultValue){
        return getSharedPreferences(context).getInt(DEFAULT_ACTIVE_PAGE, defaultValue);
    }


}
