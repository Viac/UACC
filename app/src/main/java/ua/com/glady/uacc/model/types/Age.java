package ua.com.glady.uacc.model.types;

import android.content.res.Resources;

import ua.com.glady.uacc.R;

import static ua.com.glady.uacc.model.Constants.*;

/**
 * Age class is a wrapper for int value with few additional function to improve code
 * readability
 *
 * Created by Slava on 19.03.2015.
 */
public class Age {

    // Age categories
    public static final int AGE_0_YEARS = 0;
    public static final int AGE_NOT_EXCEED_5_YEARS = 59;
    public static final int AGE_5_YEARS = 60;
    public static final int AGE_EXCEED_5_YEARS = 61;
    public static final int AGE_NOT_EXCEED_8_YEARS = 95;
    public static final int AGE_8_YEARS = 96;
    public static final int AGE_EXCEED_8_YEARS = 97;

    public void setAge(int age) {
        this.age = age;
    }

    private int age = UNDEFINED;

    public boolean isNew(){
        return (age == AGE_0_YEARS);
    }

    public boolean isUsed(){
        return (age > AGE_0_YEARS);
    }

    public boolean isNotExceed5(){
        return isUsed() && (age <= AGE_5_YEARS);
    }

    public boolean isExceed5(){
        return isUsed() && (age > AGE_5_YEARS);
    }

    public boolean isExceed8(){
        return isUsed() && (age > AGE_8_YEARS);
    }

    /**
     * @param resources resources to have local strings
     * @return age value converted to local string
     */
    public static String getStringValue(int age, Resources resources) {
        if (age == AGE_0_YEARS)
            return resources.getString(R.string.AgeNew);
        if (age == AGE_NOT_EXCEED_5_YEARS)
            return resources.getString(R.string.AgeNotExceed5);
        if (age == AGE_5_YEARS)
            return resources.getString(R.string.Age5);
        if (age == AGE_EXCEED_5_YEARS)
            return resources.getString(R.string.AgeExceed5);
        if (age == AGE_NOT_EXCEED_8_YEARS)
            return resources.getString(R.string.AgeNotExceed8);
        if (age == AGE_8_YEARS)
            return resources.getString(R.string.Age8);
        if (age == AGE_EXCEED_8_YEARS)
            return resources.getString(R.string.AgeExceed8);

        if (age > AGE_8_YEARS)
            return resources.getString(R.string.AgeExceed8);
        if (age > AGE_5_YEARS)
            return resources.getString(R.string.AgeExceed5);

        return resources.getString(R.string.AgeNotExceed5);
    }

}