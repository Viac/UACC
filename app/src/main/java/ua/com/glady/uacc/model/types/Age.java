package ua.com.glady.uacc.model.types;

import static ua.com.glady.uacc.model.Constants.*;

/**
 * Age class is a wrapper for int value with few additional function to improve code
 * readability
 *
 * Created by Slava on 19.03.2015.
 */
public class Age {

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

}