package ua.com.glady.uacc.model.vehicle;

import android.content.Context;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.calculators.BackwardCalc;
import ua.com.glady.uacc.model.calculators.BcOutput;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.calculators.UaccPreferences;
import ua.com.glady.uacc.model.types.Age;
import ua.com.glady.uacc.model.types.Engine;
import ua.com.glady.uacc.model.types.VehicleType;
import ua.com.glady.uacc.tools.StringTable;

import static ua.com.glady.uacc.tools.ToolsStr.getMoneyStr;

/**
 * Declares abstract class of vehicle. Subclasses are "Car", "Bus" and so on.
 * They different in a way of Forward/backward calculations, each one has own
 * ETC code builder.
 *
 * Created by vgl on 19.03.2015.
 */
public abstract class AVehicle {

    // Required external resources
    Context context;

    // Stores excises base registry
    final ExcisesRegistry excisesRegistry;

    // Own object fields

    protected VehicleType vehicleType;

    // See definition of terms
    int basicPrice;
    // Assumption is - ANY vehicle has an engine
    final Engine engine;
    // Not required, for instance motorcycles doesn't depends on age at all
    final Age age;

    // Calculators and preferences (See definition of terms)
    final BackwardCalc backwardCalc;

    /**
     * Abstract class constructor.
     * @param context - to get localized text and preferences
     * @param excisesRegistry - excises directory
     */
    AVehicle(Context context, ExcisesRegistry excisesRegistry) {
        this.context = context;
        this.excisesRegistry = excisesRegistry;

        engine = new Engine();
        age = new Age();

        backwardCalc = new BackwardCalc();
    }

    public void setBasicPrice(int basicPrice) {
        this.basicPrice = basicPrice;
    }

    public int getBasicPrice() {
        return this.basicPrice;
    }

    public void setAge(int age) {
        this.age.setAge(age);
    }

    public Engine getEngine() {
        return engine;
    }

    public abstract void makeBcOutput(int finalPrice);

    /**
     * Returns rounded value of basic price by given final price in Ukraine
     * @param finalPrice final price in Ukraine, EUR
     * @return rounded value of basic price, EUR
     */
    protected abstract int getCalculatedBasicPrice(int finalPrice);

    /**
     * Returns String with rounded value of basic price by given final price in Ukraine
     * @param finalPrice final price in Ukraine, EUR
     * @return String with rounded value of basic price, EUR. If it's not valid returns "-"
     */
    String getCalculatedBasicPriceStr(int finalPrice){
        int initialPrice = getCalculatedBasicPrice(finalPrice);
        return getMoneyStr(initialPrice, Constants.EMPTY_MONEY_VALUE);
    }

    /**
     * Returns html string with results of forward calculations
     * @param htmlTemplate - html Template, must contains text
     *                     GET_RESULT_RESUME and GET_RESULT_TABLE
     * @return html string with results of forward calculations
     */
    public abstract String getForwardCalcHtml(String htmlTemplate);

    /**
     * Returns html string with results of backward calculations
     * @param htmlTemplate - html Template, must contains text GET_RESULT_OUTPUT
     * @return html string with results of backward calculations
     */
    public String getBackwardCalcHtml(String htmlTemplate){
        return backwardCalc.getHtml(htmlTemplate);
    }

    protected final UaccPreferences.VehiclePreferences getVehiclePreferences(){
        UaccPreferences preferences = new UaccPreferences(context, this.vehicleType);
        return preferences.getVehiclePreferences();
    }

    /**
     * Adds single Backward calculation output to the backward calculator
     * @param header output header
     * @param info output clarification text
     * @param ageCategories array of ages to calculate results
     * @param finalPrice final price in Ukraine, EUR
     */
    void addBcOutput(String header, String info, int[] ageCategories, int finalPrice){
        BcOutput out = new BcOutput(header, info);

        StringTable table = out.getTable();

        // Headers
        table.setCell(0, 0, context.getString(R.string.volume_cm3));
        int col = 1;

        if (ageCategories.length != 0) {
            for (int age : ageCategories) {
                table.setCell(0, col, context.getString(R.string.price) + ",</br>" +
                        Age.getStringValue(age, context.getResources()));
                col++;
            }
        }
        else {
            table.setCell(0, col, context.getString(R.string.price));
        }

        // data, cols count depends on ageCategories array which has own value in each subclass
        int row = 1;

        UaccPreferences.VehiclePreferences preferences = getVehiclePreferences();
        for (int volume = preferences.lowVolume; volume <= preferences.highVolume; volume += preferences.stepVolume) {
            engine.setVolume(volume);
            table.setCell(row, 0, String.valueOf(volume));
            col = 1;
            if (ageCategories.length != 0) {
                for (int age : ageCategories) {
                    this.age.setAge(age);
                    table.setCell(row, col, getCalculatedBasicPriceStr(finalPrice));
                    col++;
                }
            }
            else {
                table.setCell(row, col, getCalculatedBasicPriceStr(finalPrice));
            }
            row++;
        }

        backwardCalc.addBcOutput(out);
    }

}
