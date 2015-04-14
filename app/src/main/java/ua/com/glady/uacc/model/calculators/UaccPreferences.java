package ua.com.glady.uacc.model.calculators;

import android.content.Context;
import android.content.SharedPreferences;

import ua.com.glady.uacc.model.types.VehicleType;

/**
 * Created by Slava on 09.04.2015.
 */
public class UaccPreferences {

    public class VehiclePreferences{
        public int lowVolume;
        public int highVolume;
        public int stepVolume;
        public int defaultVolume;
    }

    private final SharedPreferences sharedPreferences;

    private String[] carKeys = {"RcCarLowVolume", "RcCarHighVolume", "RcCarStepVolume", "RcCarDefaultVolume"};
    private int[] carDefaults = {800, 4200, 100, 2000};

    private String[] busKeys = {"RcBusLowVolume", "RcBusHighVolume", "RcBusStepVolume", "RcBusDefaultVolume"};
    private int[] busDefaults = {1500, 5500, 250, 5000};

    private String[] truckKeys = {"RcTruckLowVolume", "RcTruckHighVolume", "RcTruckStepVolume", "RcTruckDefaultVolume"};
    private int[] truckDefaults = {2500, 8500, 250, 5000};

    private String[] motorcycleKeys = {"RcBikeLowVolume", "RcBikeHighVolume", "RcBikeStepVolume", "RcBikeDefaultVolume"};
    private int[] motorcycleDefaults = {50, 500, 50, 500};

    private String[] keys;
    private int[] defaults;

    public UaccPreferences(Context context, VehicleType vehicleType){
        this.sharedPreferences = context.getSharedPreferences(
                context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
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
        result.defaultVolume = sharedPreferences.getInt(keys[3], defaults[3]);
        return result;
    }

    public void setVehiclePreferences(int lowVolume, int highVolume, int stepVolume,
                                      int defaultVolume){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(keys[0], lowVolume);
        edit.putInt(keys[1], highVolume);
        edit.putInt(keys[2], stepVolume);
        edit.putInt(keys[3], defaultVolume);
        edit.apply();
        edit.commit();
    }

}
