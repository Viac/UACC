package ua.com.glady.uacc.model.vehicle;

import android.content.Context;

import org.json.JSONException;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.calculators.BcOutput;
import ua.com.glady.uacc.model.calculators.ForwardCalc;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.model.calculators.UaccPreferences;
import ua.com.glady.uacc.model.types.Age;
import ua.com.glady.uacc.model.types.RussianMfParams;
import ua.com.glady.uacc.model.types.VehicleSpecificImpost;
import ua.com.glady.uacc.model.types.VehicleType;
import ua.com.glady.uacc.tools.StringTable;

import static java.lang.Double.compare;
import static ua.com.glady.uacc.tools.ConditionsChecker.*;
import static ua.com.glady.uacc.tools.ToolsRes.getRawResAsString;

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

    public RussianMfParams getRussianMfParams() {
        return russianMfParams;
    }

    public void setRussianMfParams(RussianMfParams russianMfParams) {
        this.russianMfParams = russianMfParams;
    }

    private RussianMfParams russianMfParams;

    @Override
    protected void updateVehicleSpecificImpost(){

        super.updateVehicleSpecificImpost();

        double compensationImpostBase;
        switch (russianMfParams) {
            case rus_None:
                compensationImpostBase = 0.0;
                break;
            case rus_Sollers:
                compensationImpostBase = 17.66;
                break;
            case rus_AutoVAZ:
                compensationImpostBase = 14.57;
                break;
            case rus_Other:
                compensationImpostBase = 10.41;
                break;
            case rus_Undefined:
                compensationImpostBase = 17.66;
                break;
            default:
                compensationImpostBase = 0.0;
                break;
        }

        if (compare(compensationImpostBase, 0.0) <= 0)
            return;

        String template = this.context.getString(R.string.CompensationImpostDescription);
        VehicleSpecificImpost vsi = new VehicleSpecificImpost();
        vsi.header = this.context.getString(R.string.CompensationImpost);
        vsi.description = String.format(template, compensationImpostBase);
        vsi.value = compensationImpostBase;
        this.vehicleSpecificImposts.add(vsi);
    }

    public Car(Context context, ExcisesRegistry excisesRegistry) {
        super(context, excisesRegistry);
        vehicleType = VehicleType.Car;
        isSpecialDesign = false;
        isCaravan = false;
        russianMfParams = RussianMfParams.rus_None;
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
        addBcOutput(context.getString(R.string.Gasoline),
                context.getString(R.string.FcCarGasolineEngineDescription),
                ageCategories, finalPrice);

        engine.setType(Constants.ENG_DIESEL);
        addBcOutput(context.getString(R.string.Diesel),
                context.getString(R.string.FcCarDieselEngineDescription),
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
                context.getString(R.string.FcCaravanCaption),
                context.getString(R.string.FcCaravanDescription)
        );
        StringTable table = out.getTable();

        // Headers
        table.setCell(0, 0, context.getString(R.string.volume_cm3));

        // Price for gasoline engine
        table.setCell(0, 1, context.getString(R.string.price) + "</br>" + context.getString(R.string.Gasoline));

        // Price for diesel engine
        table.setCell(0, 2, context.getString(R.string.price) + "</br>" + context.getString(R.string.Diesel));

        // Data
        int row = 1;
        UaccPreferences.VehiclePreferences preferences = getVehiclePreferences();

        for (int volume = preferences.lowVolume; volume <= preferences.highVolume; volume += preferences.stepVolume) {
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
                context.getString(R.string.Other),
                context.getString(R.string.FcCarOtherEngineDescription)
        );
        StringTable table = out.getTable();

        table.setCell(0, 0, context.getString(R.string.engine));
        table.setCell(0, 1, context.getString(R.string.price));

        table.setCell(1, 0, context.getString(R.string.Electric));
        engine.setType(Constants.ENG_ELECTRIC);
        table.setCell(1, 1, getCalculatedBasicPriceStr(finalPrice));

        table.setCell(2, 0, context.getString(R.string.Other));
        engine.setType(Constants.ENG_OTHER);
        table.setCell(2, 1, getCalculatedBasicPriceStr(finalPrice));

        backwardCalc.addBcOutput(out);
    }


    @Override
    public int getCalculatedBasicPrice(int finalPrice) {
        int exciseInt = (int) Math.round(this.getExcise());
        return backwardCalc.getBasicPrice(finalPrice, getImpost(), getSpecialImpost(), exciseInt);
    }

    private String getData3251(int existingFinalPrice){
        // switch excises to the new one
        String excisesSource = getRawResAsString(this.context.getResources(), R.raw.excise_list_3251);
        try {
            excisesRegistry.makeExcisesRegistry(excisesSource);
        } catch (JSONException e) {
            return context.getResources().getString(R.string.errCanNotRunCalculation);
        }

        Car car3251 = new Car(this.context, this.excisesRegistry);
        car3251.setAge(this.age.getAge());
        car3251.setBasicPrice(this.basicPrice);
        car3251.engine.setVolume(this.engine.getVolume());
        car3251.engine.setType(this.engine.getType());
        car3251.setRussianMfParams(this.russianMfParams);
        car3251.updateVehicleSpecificImpost();

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"area3251\"><p class=\"act_3251_caption\">").append(context.getString(R.string.Act_3251_Caption)).append("</p>");
        sb.append("<p class=\"act_3251_details\">").append(context.getString(R.string.Act_3251_Explanation)).append("</p>");

        ForwardCalc fc = new ForwardCalc();
        fc.calculate(basicPrice, car3251.getExcise(), car3251.getImpost(), car3251.getSpecialImpost(),
                car3251.vehicleSpecificImposts, car3251.getEtc(), context.getResources());


        String htmlTemplate =  getRawResAsString(this.context.getResources(), R.raw.addon_template_3251);
        sb.append(fc.getHtml(context.getResources(), htmlTemplate, basicPrice));

        // Under table resume:
        String resumeTemplate = context.getString(R.string.Act_3251_Resume);
        int predictedFinalPrice = fc.getTotalPrice();
        double percentageD = 100.0 * predictedFinalPrice / existingFinalPrice;
        long discountPercent = 100 - Math.round(percentageD);
        String resume = String.format(resumeTemplate, existingFinalPrice - predictedFinalPrice, discountPercent);
        sb.append("</br><p class=\"act_3251_caption\">").append(resume).append("</p></br>");
        sb.append("<a href=\"www.viac-soft.in.ua/uacc/uacc_compare.html\">");
        sb.append(context.getString(R.string.Act_3251_Link) + "</a></br>");
        sb.append("</div>");

        // Restoring original excises
        excisesSource = getRawResAsString(this.context.getResources(), R.raw.excise_list);
        try {
            excisesRegistry.makeExcisesRegistry(excisesSource);
        } catch (JSONException e) {
            return context.getResources().getString(R.string.errCanNotRunCalculation);
        }

        return sb.toString();
    }


    @Override
    public String getForwardCalcHtml(String htmlTemplate) {
        ForwardCalc fc = new ForwardCalc();
        updateVehicleSpecificImpost();
        fc.calculate(basicPrice, getExcise(), getImpost(), getSpecialImpost(),
                this.vehicleSpecificImposts, getEtc(), context.getResources());
        String result = fc.getHtml(context.getResources(), htmlTemplate, basicPrice);

        // 3251 kludge
        //
        // In version 1.3 there was an additional feature - to tease users with promised by act
        // #3251 excises. This act assume that all used cars excises made at the same volume as
        // for the new cars. But it's only suitable for the used cars with gasoline or diesel
        // engines.
        // As far this act is just s something far away and undetermined, we can assume it as
        // a point to apply quick fix
        if ((this.age.getAge() != Age.AGE_0_YEARS) && (this.getEngine().isPiston()) ){
            int existingFinalPrice = fc.getTotalPrice();
            String data3251 = getData3251(existingFinalPrice);
            result = result.replace("</body>", data3251 + "</body>");
        }

        return result;
    }

    public int getTotalPrice() {
        ForwardCalc fc = new ForwardCalc();
        updateVehicleSpecificImpost();
        fc.calculate(basicPrice, getExcise(), getImpost(), getSpecialImpost(), this.vehicleSpecificImposts, getEtc(), context.getResources());
        return fc.getTotalPrice();
    }

    /**
     * Returns impost (toll, fee) base value fot this vehicle
     * @return impost base (i.e. 0.1 - 10%)
     */
    private double getImpost() {

        // business_logic#020116-slava-3
        if (this.getEngine().isElectric())
            return 0.0;

        if (this.isSpecialDesign)
            return 0.12;

        // In most cases this is 10%
        return 0.1;
    }


}
