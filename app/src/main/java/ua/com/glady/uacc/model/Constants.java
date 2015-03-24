package ua.com.glady.uacc.model;

/**
 * This class contains various constants that used trough application, so
 * they couldn't be localized in certain classes
 *
 * Created by vgl on 19.03.2015.
 */
public class Constants {

    public static final int UNDEFINED = -1;

    // Engine types
    public static final int ENG_GASOLINE = 0;
    public static final int ENG_DIESEL = 1;
    public static final int ENG_ELECTRIC = 2;
    public static final int ENG_OTHER = 3;

    public static final int PRICE_MIN_BOUND = 1;
    public static final int PRICE_MAX_BOUND = 1000000;
    public static final int ENGINE_MIN_BOUND = 0;
    public static final int ENGINE_MAX_BOUND = 20000;

    // Gross vehicle weight
    public static final int WEIGHT_5T = 5;
    public static final int WEIGHT_20T = 20;

    // Line break
    public static final String sLineBreak = "\r\n";
    public static final String EMPTY_MONEY_VALUE = "-";

    // VAT ( Value-added tax )
    public static final double VAT_BASE = 0.2; // 20%

    // Temp fee (1-year protection fee)
    public static final double TEMPORARY_PROTECTION_FEE_BASE = 0.05; // 5%

    public static final String LAYOUT_ID_TAG = "layoutId";

}
