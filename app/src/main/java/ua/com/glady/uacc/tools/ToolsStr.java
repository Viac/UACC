package ua.com.glady.uacc.tools;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * This class contains various function for strings for current application
 *
 * Created by Slava on 19.03.2015.
 */
public class ToolsStr {

    /**
     * Checks is given string contains integer number
     * @param str string to text
     * @return true if str is integer, otherwise false
     */
    public static boolean isInteger(String str) {
        try {
            // We don't need result of this expression. All that we're interested in -
            // could it be performed without exception
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Returns string with value converted to nice-look format (with thousand separator),
     * if value <= 0, than returns default text
     *
     * @param value - money value
     * @param defaultText - text that will be returned if money <= 0
     * @return value as money string
     */
    public static String getMoneyStr(int value, String defaultText) {
        if (value > 0) {
            // France locale is same for Ukraine
            return NumberFormat.getNumberInstance(Locale.FRANCE).format(value);
        }
        else
            return defaultText;
    }

    /**
     * Overloaded method, used to get string in common cases
     * @param value - money value as string
     * @return value converted to string, '-' if it was <= 0
     */
    public static String getMoneyStr(int value){
        return getMoneyStr(value, "-");
    }

}