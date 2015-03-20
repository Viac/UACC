package ua.com.glady.uacc.model.types;

import ua.com.glady.uacc.model.Constants;

/**
 * Class describes vehicle engine (basic params are - type and volume)
 *
 * Created by vgl on 19.03.2015.
 */
public class Engine {

    public void setType(int type) {
        this.type = type;
    }

    private int type = Constants.UNDEFINED;

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    private int volume = Constants.UNDEFINED;

    public boolean isPiston() {
        return (isDiesel() || isGasoline());
    }

    public boolean isDiesel(){
        return (type == Constants.ENG_DIESEL);
    }

    public boolean isGasoline(){
        return (type == Constants.ENG_GASOLINE);
    }

    public boolean isElectric(){
        return (type == Constants.ENG_ELECTRIC);
    }

    public boolean isOther(){
        return (type == Constants.ENG_OTHER);
    }

}
