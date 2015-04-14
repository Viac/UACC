package ua.com.glady.uacc.model.vehicle;

import android.content.Context;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.calculators.ForwardCalc;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.types.Age;
import ua.com.glady.uacc.model.types.VehicleType;

import static ua.com.glady.uacc.tools.ConditionsChecker.*;

/**
 * Implements concrete abstract vehicle object for buses
 *
 * Created by Slava on 19.03.2015.
 */
public class Bus extends AVehicle {

    public Bus(Context context, ExcisesRegistry excisesRegistry) {
        super(context, excisesRegistry);
        vehicleType = VehicleType.Bus;
    }

    /**
     * Returns vehicle's ETC code (see definition of terms)
     * @return ETC code
     */
    String getEtc() {
        String result = "8702 ";

        result += checked(engine.isDiesel(), age.isNew(), ((engine.getVolume() > 2500) && (engine.getVolume() <= 5000)), "10 11 10");
        result += checked(engine.isDiesel(), age.isNew(), engine.getVolume() > 5000, "10 11 30");

        result += checked(engine.isDiesel(), age.isUsed(), ((engine.getVolume() > 2500) && (engine.getVolume() <= 5000)), "10 19 10");
        result += checked(engine.isDiesel(), age.isUsed(), engine.getVolume() > 5000, "10 19 90");

        result += checked(engine.isDiesel(), age.isNew(), engine.getVolume() <= 2500, "10 91 00");
        result += checked(engine.isDiesel(), age.isUsed(), engine.getVolume() <= 2500, "10 99 00");

        result += checked(engine.isGasoline(), age.isNew(), engine.getVolume() > 2800, "90 11 00");
        result += checked(engine.isGasoline(), age.isUsed(), engine.getVolume() > 2800, "90 19 00");

        result += checked(engine.isGasoline(), age.isNew(), engine.getVolume() <= 2800, "90 31 00");
        result += checked(engine.isGasoline(), age.isUsed(), engine.getVolume() <= 2800, "90 39 00");

        return result;
    }

    /**
     * Returns total excise value (with impact of engine volume / age)
     * @return total excise value, EUR
     */
    double getExcise() {
        double result = excisesRegistry.getExciseBase(getEtc());

        result *= engine.getVolume();

        // @see http://zakon2.rada.gov.ua/laws/show/2755-17/print1423175599578849
        if (age.isExceed8())
            result *= 50;

        return result;
    }

    @Override
    public void makeBcOutput(int finalPrice) {
        int[] ageCategories = new int[]{Age.AGE_0_YEARS, Age.AGE_NOT_EXCEED_8_YEARS,
                Age.AGE_EXCEED_8_YEARS};

        backwardCalc.clear();

        this.engine.setType(Constants.ENG_DIESEL);
        addBcOutput(context.getString(R.string.Diesel),
                context.getString(R.string.FcBusDieselEngineDescription),
                ageCategories, finalPrice);

        this.engine.setType(Constants.ENG_GASOLINE);
        addBcOutput(context.getString(R.string.Gasoline),
                context.getString(R.string.FcBusGasolineEngineDescription),
                ageCategories, finalPrice);
    }

    @Override
    public int getCalculatedBasicPrice(int finalPrice)
    {
        int exciseInt = (int) Math.round(this.getExcise());
        return backwardCalc.getCalculatedBasicPriceWithProtectionFee(finalPrice, getImpostBase(), exciseInt);
    }

    @Override
    public String getForwardCalcHtml(String htmlTemplate) {
        ForwardCalc fc = new ForwardCalc();
        fc.calculate(basicPrice, getExcise(), getImpostBase(), getEtc(), context.getResources());
        return fc.getHtml(context.getResources(), htmlTemplate, basicPrice);
    }

    /**
     * Returns impost (toll, fee) base value fot this vehicle
     * @return impost base (i.e. 0.1 - 10%)
     */
    private double getImpostBase() {
        // By default it's 10%
        double result = 0.1;

        // but for high-volume diesels it's 20%
        if (this.engine.isDiesel() && (engine.getVolume() > 5000))
            result = 0.2;

        return result;
    }

}
