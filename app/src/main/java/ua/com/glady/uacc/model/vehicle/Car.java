package ua.com.glady.uacc.model.vehicle;

import android.content.SharedPreferences;
import android.content.res.Resources;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.calculators.BcOutput;
import ua.com.glady.uacc.model.calculators.BcPreferences;
import ua.com.glady.uacc.model.calculators.ForwardCalc;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.types.Age;
import ua.com.glady.uacc.tools.StringTable;

import static ua.com.glady.uacc.tools.ConditionsChecker.*;

/**
 * Implements concrete vehicle type (Car, including racing cars, snow/golf and caravans.
 *
 * Created by Slava on 19.03.2015.
 */
public class Car extends AVehicle {

    // defines is a car designed for snow (golf car, and so on). Code 8703 10
    private boolean isSpecialDesign;
    // defines is this car is a motor caravan
    private boolean isCaravan;

    /**
     * Abstract class constructor.
     *
     * @param sharedPreferences - need to read data for subclasses preferences
     * @param resources         - source of localized strings
     * @param excisesRegistry   - excises directory
     */
    public Car(SharedPreferences sharedPreferences, Resources resources, ExcisesRegistry excisesRegistry) {
        super(sharedPreferences, resources, excisesRegistry);
        isSpecialDesign = false;
        isCaravan = false;
    }

    public void setSpecialDesign(boolean isSpecialDesign) {
        this.isSpecialDesign = isSpecialDesign;
    }

    public void setCaravan(boolean isCaravan) {
        this.isCaravan = isCaravan;
    }

    /**
     * Returns ETC code for gasoline cars. Extracted method.
     * @return ETC code for gasoline cars
     */
    String getGasolineEtc() {
        String result = "";

        boolean volumeMatched = engine.getVolume() <= 1000;
        result += checked(volumeMatched, age.isNew(), "21 10 00");
        result += checked(volumeMatched, age.isNotExceed5(), "21 90 10");
        result += checked(volumeMatched, age.isExceed5(), "21 90 30");

        volumeMatched = (engine.getVolume() > 1000) && (engine.getVolume() <= 1500);
        result += checked(volumeMatched, age.isNew(), "22 10 00");
        result += checked(volumeMatched, age.isNotExceed5(), "22 90 10");
        result += checked(volumeMatched, age.isExceed5(), "22 90 30");

        // group 23
        if ((engine.getVolume() > 1500) && (engine.getVolume() <= 3000)) {
            result += checked(age.isNew(), isCaravan, engine.getVolume() <= 2200, "23 11 10");
            result += checked(age.isNew(), isCaravan, engine.getVolume() > 2200, "23 11 30");
            result += checked(age.isNew(), !isCaravan, engine.getVolume() <= 2200, "23 19 10");
            result += checked(age.isNew(), !isCaravan, engine.getVolume() > 2200, "23 19 30");
            result += checked(age.isNotExceed5(), engine.getVolume() <= 2200, "23 90 11");
            result += checked(age.isExceed5(), engine.getVolume() <= 2200, "23 90 13");
            result += checked(age.isNotExceed5(), engine.getVolume() > 2200, "23 90 31");
            result += checked(age.isExceed5(), engine.getVolume() > 2200, "23 90 33");
        }

        volumeMatched = (engine.getVolume() > 3000);
        result += checked(volumeMatched, age.isNew(), "24 10 00");
        result += checked(volumeMatched, age.isNotExceed5(), "24 90 10");
        result += checked(volumeMatched, age.isExceed5(), "24 90 30");

        return result;
    }

    /**
     * Returns ETC code for diesel cars. Extracted method.
     * @return ETC code for diesel cars
     */
    String getDieselEtc() {
        String result = "";

        boolean volumeMatched = engine.getVolume() <= 1500;
        result += checked(volumeMatched, age.isNew(), "31 10 00");
        result += checked(volumeMatched, age.isNotExceed5(), "31 90 10");
        result += checked(volumeMatched, age.isExceed5(), "31 90 30");

        if ((engine.getVolume() > 1500) && (engine.getVolume() <= 2500)) {
            result += checked(age.isNew(), isCaravan, "32 11 00");
            result += checked(age.isNew(), !isCaravan, "32 19 00");
            result += checked(age.isNotExceed5(), "32 90 10");
            result += checked(age.isExceed5(), "32 90 30");
        }

        if (engine.getVolume() > 2500) {
            result += checked(age.isNew(), isCaravan, "33 11 00");
            result += checked(age.isNew(), !isCaravan, "33 19 00");
            result += checked(age.isNotExceed5(), "33 90 10");
            result += checked(age.isExceed5(), "33 90 30");
        }

        return result;
    }

    /**
     * Returns ETC code
     * @return ETC code
     */
    String getEtc() {
        String result = "8703 ";

        if (!isSpecialDesign) {
            if (engine.isGasoline())
                result += getGasolineEtc();
            if (engine.isDiesel())
                result += getDieselEtc();
            if (engine.isElectric())
                result += "90 10 00";
            if (engine.isOther())
                result += "90 90 00";
        } else {
            result += checked(engine.isPiston(), "10 11 00");
            result += checked(!engine.isPiston(), "10 18 00");
        }
        return result;
    }

    /**
     * Returns vehicle excise, EUR
     * @return vehicle excise, EUR
     */
    double getExcise() {
        double result = excisesRegistry.getExciseBase(getEtc());

        if (isSpecialDesign && (!engine.isElectric())) {
            result *= engine.getVolume();
        } else {
            if (engine.isPiston())
                result *= engine.getVolume();
        }

        return result;
    }

    @Override
    public void makeBcOutput(int finalPrice) {
        int[] ageCategories = new int[]{Age.AGE_0_YEARS,
                Age.AGE_NOT_EXCEED_5_YEARS, Age.AGE_EXCEED_5_YEARS};

        backwardCalc.clear();

        engine.setType(Constants.ENG_GASOLINE);
        addBcOutput(resources.getString(R.string.Gasoline),
                resources.getString(R.string.FcCarGasolineEngineDescription),
                ageCategories, finalPrice);

        engine.setType(Constants.ENG_DIESEL);
        addBcOutput(resources.getString(R.string.Diesel),
                resources.getString(R.string.FcCarDieselEngineDescription),
                ageCategories, finalPrice);

        addBcOutputForSpecialDesign(finalPrice);

        addBcOutputCaravan(finalPrice);
    }

    /**
     * Adds backward output for caravans, extracted method
     * @param finalPrice final price in Ukraine
     */
    private void addBcOutputCaravan(int finalPrice) {
        boolean storedField = this.isCaravan;
        this.isCaravan = true;

        BcOutput out = new BcOutput(
                resources.getString(R.string.FcCaravanCaption),
                resources.getString(R.string.FcCaravanDescription)
        );
        StringTable table = out.getTable();

        // Headers
        table.setCell(0, 0, resources.getString(R.string.volume_cm3));

        // Price for gasoline engine
        table.setCell(0, 1, resources.getString(R.string.price) + "</br>" + resources.getString(R.string.Gasoline));

        // Price for diesel engine
        table.setCell(0, 2, resources.getString(R.string.price) + "</br>" + resources.getString(R.string.Diesel));

        // Data
        int row = 1;
        for (int volume = bcPreferences.minVolume; volume <= bcPreferences.maxVolume; volume += bcPreferences.stepVolume) {
            engine.setVolume(volume);
            table.setCell(row, 0, String.valueOf(volume));
            engine.setType(Constants.ENG_GASOLINE);
            table.setCell(row, 1, getCalculatedBasicPriceStr(finalPrice));
            engine.setType(Constants.ENG_DIESEL);
            table.setCell(row, 2, getCalculatedBasicPriceStr(finalPrice));
            row++;
        }
        this.isCaravan = storedField;

        backwardCalc.addBcOutput(out);
    }

    /**
     * Adds backward output for special design vehicles
     * @param finalPrice final price in Ukraine
     */
    private void addBcOutputForSpecialDesign(int finalPrice) {
        BcOutput out = new BcOutput(
                resources.getString(R.string.Other),
                resources.getString(R.string.FcCarOtherEngineDescription)
        );
        StringTable table = out.getTable();

        table.setCell(0, 0, resources.getString(R.string.engine));
        table.setCell(0, 1, resources.getString(R.string.price));

        table.setCell(1, 0, resources.getString(R.string.Electric));
        engine.setType(Constants.ENG_ELECTRIC);
        table.setCell(1, 1, getCalculatedBasicPriceStr(finalPrice));

        table.setCell(2, 0, resources.getString(R.string.Other));
        engine.setType(Constants.ENG_OTHER);
        table.setCell(2, 1, getCalculatedBasicPriceStr(finalPrice));

        backwardCalc.addBcOutput(out);
    }

    @Override
    public void initializeBcPreferences() {
        bcPreferences = new BcPreferences(sharedPreferences, 800, 4200, 100,
                "RcCarLowVolume", "RcCarHighVolume", "RcCarStepVolume");
    }

    @Override
    public int getCalculatedBasicPrice(int finalPrice) {
        int exciseInt = (int) Math.round(this.getExcise());
        return backwardCalc.getCalculatedBasicPriceWithProtectionFee(finalPrice, getImpost(), exciseInt);
    }

    @Override
    public String getForwardCalcHtml(String htmlTemplate) {
        ForwardCalc fc = new ForwardCalc();
        fc.calculate(basicPrice, getExcise(), getImpost(), getEtc(), resources);
        return fc.getHtml(resources, htmlTemplate, basicPrice);
    }

    /**
     * Returns impost (toll, fee) base value fot this vehicle
     * @return impost base (i.e. 0.1 - 10%)
     */
    private double getImpost() {
        // In most cases this is 10%
        double result = 0.1;
        if (this.isSpecialDesign)
            result = 0.12;
        return result;
    }

}
