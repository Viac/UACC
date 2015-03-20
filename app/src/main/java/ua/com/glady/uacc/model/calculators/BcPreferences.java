package ua.com.glady.uacc.model.calculators;

import android.content.SharedPreferences;

/**
 * Class contains backward calculation preferences, each vehicle type has own set
 * This class is very short, so fields made public instead of getters.
 *
 * Created by vgl on 19.03.2015.
 */
public class BcPreferences {

    public int minVolume;
    public int maxVolume;
    public int stepVolume;

    public final String minVolumeKey;
    public final String maxVolumeKey;
    public final String stepVolumeKey;

    private final SharedPreferences sharedPreferences;

    public BcPreferences(SharedPreferences sharedPreferences, int minVolume, int maxVolume,
                         int stepVolume, String minVolumeKey, String maxVolumeKey,
                         String stepVolumeKey) {

        this.sharedPreferences = sharedPreferences;

        this.minVolumeKey = minVolumeKey;
        this.minVolume = sharedPreferences.getInt(minVolumeKey, minVolume);

        this.maxVolumeKey = maxVolumeKey;
        this.maxVolume = sharedPreferences.getInt(maxVolumeKey, maxVolume);

        this.stepVolumeKey = stepVolumeKey;
        this.stepVolume = sharedPreferences.getInt(stepVolumeKey, stepVolume);
    }

    public void setValues(int minVolume, int maxVolume, int stepVolume) {
        this.minVolume = minVolume;
        this.maxVolume = maxVolume;
        this.stepVolume = stepVolume;

        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putInt(minVolumeKey, minVolume);
        ed.putInt(maxVolumeKey, maxVolume);
        ed.putInt(stepVolumeKey, stepVolume);
        ed.apply();
    }
}
