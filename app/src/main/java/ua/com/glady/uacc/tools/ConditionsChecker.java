package ua.com.glady.uacc.tools;

/**
 * This is a set of static methods that solves same task - if input condition(s) true, return
 * input text, otherwise empty string. Used as syntax sugar.
 *
 * Created by Slava on 19.03.2015.
 */
public class ConditionsChecker {

    /**
     * Checks condition and return result depending on boolean condition value
     * @param condition condition to check
     * @param text text to return if condition is true
     * @return text if condition is true, empty string in other case
     */
    public static String checked(boolean condition, String text){
        if (condition)
            return text;
        else
            return "";
    }

    /**
     * Checks conditions and return result depending on boolean conditions value
     * @param condition1 condition to check
     * @param condition2 condition to check
     * @param text text to return if condition is true
     * @return text if condition1 AND condition2 is true, empty string in other case
     */
    public static String checked(boolean condition1, boolean condition2, String text){
        if (condition1 && condition2)
            return text;
        else
            return "";
    }

    /**
     * Checks conditions and return result depending on boolean conditions value
     * @param condition1 condition to check
     * @param condition2 condition to check
     * @param condition3 condition to check
     * @param text text to return if condition is true
     * @return text if boolean multiplication of conditions is true, empty string in other case
     */
    public static String checked(boolean condition1, boolean condition2, boolean condition3, String text){
        if (condition1 && condition2 && condition3)
            return text;
        else
            return "";
    }

    /**
     * Checks conditions and return result depending on boolean conditions value
     * @param condition1 condition to check
     * @param condition2 condition to check
     * @param condition3 condition to check
     * @param condition4 condition to check
     * @param text text to return if condition is true
     * @return text if boolean multiplication of conditions is true, empty string in other case
     */
    public static String checked(boolean condition1, boolean condition2, boolean condition3, boolean condition4, String text){
        if (condition1 && condition2 && condition3 && condition4)
            return text;
        else
            return "";
    }

    /**
     * Checks conditions and return result depending on Relational operators
     * (testedValue greater or equal (GE) than value1 and less than (LT) value2)
     * @param value1 int value
     * @param value2 int value
     * @param testedValue value to test
     * @return true if testedValue greater or equal (GE) than value1 and less than (LT) value2
     */
    public static boolean isGE1_LT2(int value1, int value2, int testedValue){
        return ((testedValue >= value1) && (testedValue < value2));
    }


}
