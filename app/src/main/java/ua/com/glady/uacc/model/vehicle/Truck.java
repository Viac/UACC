package ua.com.glady.uacc.model.vehicle;

import android.content.Context;

import ua.com.glady.uacc.R;
import static ua.com.glady.uacc.model.Constants.*;
import ua.com.glady.uacc.model.calculators.ForwardCalc;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.types.Age;
import ua.com.glady.uacc.model.types.VehicleType;

import static ua.com.glady.uacc.tools.ConditionsChecker.*;

/**
 * Implements concrete vehicle type for trucks (including off-road dumpers)
 *
 * Created by Slava on 19.03.2015.
 */
public class Truck extends AVehicle {

    // Defines is truck off-road used dumper (see terms definition)
    private boolean isDumper;

    // Defines gross weight, tonnes (see terms definition)
    private int grossWeight;

    public Truck(Context context, ExcisesRegistry excisesRegistry) {
        super(context, excisesRegistry);
        vehicleType = VehicleType.Truck;
        isDumper = false;
        grossWeight = UNDEFINED;
    }

    public void setDumper(boolean isDumper) {
        this.isDumper = isDumper;
    }

    public void setGrossWeight(int grossWeight) {
        this.grossWeight = grossWeight;
    }

    /**
     * Extracted method
     * @return  ETC code for dumpers
     */
    String getDumperEtc(){
        String result = "";
        result += checked(!engine.isPiston(), "10 10 10");
        result += checked(engine.isPiston(), "10 90 90");
        return result;
    }

    /**
     * Extracted method
     * @return  ETC code for trucks with diesel engines
     */
    String getDieselEtc(){
        String result = "";

        boolean gwMatched = grossWeight <= WEIGHT_5T;

        result += checked(gwMatched, engine.getVolume() > 2500, age.isNew(), "21 31 00");
        result += checked(gwMatched, engine.getVolume() > 2500, age.isUsed(), "21 39 00");

        result += checked(gwMatched, engine.getVolume() <= 2500, age.isNew(), "21 91 00");
        result += checked(gwMatched, engine.getVolume() <= 2500, age.isUsed(), "21 99 00");

        gwMatched = (grossWeight > WEIGHT_5T) && (grossWeight <= WEIGHT_20T);
        result += checked(gwMatched, age.isNew(), "22 91 00");
        result += checked(gwMatched, age.isUsed(), "22 99 00");

        gwMatched = (grossWeight > WEIGHT_20T);
        result += checked(gwMatched, age.isNew(), "23 91 00");
        result += checked(gwMatched, age.isUsed(), "23 99 00");

        return result;
    }

    /**
     * Extracted method
     * @return  ETC code for trucks with gasoline engines
     */
    String getGasolineEtc(){
        String result = "";

        boolean gwMatched = grossWeight <= WEIGHT_5T;

        result += checked(gwMatched, engine.getVolume() > 2800, age.isNew(), "31 31 00");
        result += checked(gwMatched, engine.getVolume() > 2800, age.isUsed(), "31 39 00");

        result += checked(gwMatched, engine.getVolume() <= 2800, age.isNew(), "31 91 00");
        result += checked(gwMatched, engine.getVolume() <= 2800, age.isUsed(), "31 99 00");

        gwMatched = (grossWeight > WEIGHT_5T);
        result += checked(gwMatched, age.isNew(), "32 91 00");
        result += checked(gwMatched, age.isUsed(), "32 99 00");

        return result;
    }

    /**
     * @return truck ETC code
     */
    String getEtc() {
        String result = "8704 ";

        // Dumpers
        if (isDumper){
            result += getDumperEtc();
        }
        // Usual trucks
        else {
            if (engine.isDiesel()) {
                result += getDieselEtc();
            }
            else if (engine.isGasoline()) {
                result += getGasolineEtc();
            }
        }

        return result;
    }

    /**
     * Calculated total excises value, EUR
     * @return total excises value
     */
    double getExcise() {
        double result = excisesRegistry.getExciseBase(getEtc());

        result *= engine.getVolume();

        // @see http://zakon2.rada.gov.ua/laws/show/2755-17/print1423175599578849
        if (age.isExceed5() && (!age.isExceed8()))
            result *= 40;
        if (age.isExceed8())
            result *= 50;

        return result;
    }

    @Override
    public void makeBcOutput(int finalPrice) {
        int[] ageCategories = new int[]{Age.AGE_0_YEARS,
                Age.AGE_NOT_EXCEED_5_YEARS, Age.AGE_EXCEED_5_YEARS, Age.AGE_EXCEED_8_YEARS};

        backwardCalc.clear();
        boolean storedDumperState = this.isDumper;
        int storedGrossWeight = this.grossWeight;

        this.isDumper = false;

        engine.setType(ENG_DIESEL);
        this.grossWeight = WEIGHT_5T - 1;
        addBcOutput(context.getString(R.string.Truck) + ", " + context.getString(R.string.Diesel),
                context.getString(R.string.Truck_GrossWeight_NotExceed5),
                ageCategories, finalPrice);

        this.grossWeight = WEIGHT_5T + 1;
        addBcOutput("", context.getString(R.string.Truck_GrossWeight_NotExceed20), ageCategories, finalPrice);

        this.grossWeight = WEIGHT_20T + 1;
        addBcOutput("", context.getString(R.string.Truck_GrossWeight_Exceed20), ageCategories, finalPrice);


        engine.setType(ENG_GASOLINE);
        this.grossWeight = WEIGHT_5T - 1;
        addBcOutput(context.getString(R.string.Truck) + ", " + context.getString(R.string.Gasoline),
                context.getString(R.string.Truck_GrossWeight_NotExceed5),
                ageCategories, finalPrice);

        this.grossWeight = WEIGHT_5T + 1;
        addBcOutput("", context.getString(R.string.Truck_GrossWeight_Exceed5), ageCategories, finalPrice);

        // Both diesel and gasoline engined uses same excise, so we don't need to add both
        this.isDumper = true;
        engine.setType(ENG_GASOLINE);
        addBcOutput(context.getString(R.string.Code_8704_10_10_10_Caption),
                context.getString(R.string.Code_8704_10_10_10),
                ageCategories, finalPrice);

        this.isDumper = storedDumperState;
        this.grossWeight = storedGrossWeight;
    }

    @Override
    public int getCalculatedBasicPrice(int finalPrice) {
        int exciseInt = (int) Math.round(this.getExcise());
        return backwardCalc.getBasicPrice(finalPrice, getImpost(), getSpecialImpost(), exciseInt);
    }

    @Override
    public String getForwardCalcHtml(String htmlTemplate) {
        ForwardCalc fc = new ForwardCalc();
        fc.calculate(basicPrice, getExcise(), getImpost(), getSpecialImpost(),
                this.vehicleSpecificImposts, getEtc(), context.getResources());
        return fc.getHtml(context.getResources(), htmlTemplate, basicPrice);
    }

    /**
     * Returns impost (toll, fee) base value fot this vehicle
     * @return impost base (i.e. 0.1 - 10%)
     */
    private double getImpost() {
        if (this.isDumper)
            return 0.0;

        if (engine.isGasoline() && this.grossWeight <= WEIGHT_5T)
            return 0.05;

        return 0.1;
    }


}