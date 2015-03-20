package ua.com.glady.uacc.model.vehicle;

import android.content.SharedPreferences;
import android.content.res.Resources;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.calculators.BcOutput;
import ua.com.glady.uacc.model.calculators.BcPreferences;
import ua.com.glady.uacc.model.calculators.ForwardCalc;
import ua.com.glady.uacc.model.ExcisesRegistry;
import ua.com.glady.uacc.tools.StringTable;

import static ua.com.glady.uacc.tools.ConditionsChecker.*;

/**
 * Implements concrete vehicle type for motorcycles
 *
 * Created by vgl on 19.03.2015.
 */
public class Motorcycle extends AVehicle {

    /**
     * Abstract class constructor.
     *
     * @param sharedPreferences - need to read data for subclasses preferences
     * @param resources         - source of localized strings
     * @param excisesRegistry           - excises directory
     */
    public Motorcycle(SharedPreferences sharedPreferences, Resources resources, ExcisesRegistry excisesRegistry) {
        super(sharedPreferences, resources, excisesRegistry);
    }

    /**
     * @return ETC code
     */
    String getEtc() {
        String result = "8711 ";

        result += checked(engine.getVolume() <= 50, "10 00 00");
        result += checked((engine.getVolume() > 50) && (engine.getVolume() <= 250), "20");
        result += checked((engine.getVolume() > 250) && (engine.getVolume() <= 500), "30");
        result += checked((engine.getVolume() > 500) && (engine.getVolume() <= 800), "40 00 00");
        result += checked(engine.getVolume() > 800, "50 00 00");
        result += checked(!engine.isPiston(), "90 00 00");

        return result;
    }

    /**
     * Calculates total excise volume
     * @return total excise volume
     */
    double getExcise() {
        double result = excisesRegistry.getExciseBase(getEtc());
        if (engine.isPiston())
            result *= engine.getVolume();
        return result;
    }

    @Override
    public void makeBcOutput(int finalPrice) {
        int[] ageCategories = new int[]{};
        backwardCalc.clear();

        this.engine.setType(Constants.ENG_GASOLINE);
        addBcOutput(resources.getString(R.string.Gasoline),
                resources.getString(R.string.FcMotorcycleRegularEngineDescription),
                ageCategories, finalPrice);

        this.engine.setType(Constants.ENG_OTHER);
        addBcOutputOther(finalPrice);
    }

    /**
     * Adds backward calculation output for the motorcycles with unusual engines
     * @param finalPrice target final price in Ukraine, EUR
     */
    private void addBcOutputOther(int finalPrice) {
        BcOutput out = new BcOutput(
                resources.getString(R.string.Other),
                resources.getString(R.string.FcMotorcycleOtherEngineDescription)
        );
        StringTable table = out.getTable();

        table.setCell(0, 0, resources.getString(R.string.engine));
        table.setCell(0, 1, resources.getString(R.string.price));

        table.setCell(0, 0, resources.getString(R.string.Other));
        table.setCell(0, 1, getCalculatedBasicPriceStr(finalPrice));

        backwardCalc.addBcOutput(out);
    }

    @Override
    public void initializeBcPreferences() {
        bcPreferences = new BcPreferences(sharedPreferences, 50, 550, 50,
                "RcBikeLowVolume", "RcBikeHighVolume", "RcBikeStepVolume");
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
    @SuppressWarnings("SameReturnValue")
    private double getImpost() {
        // For now all motorcycles have 10% impost
        return 0.1;
    }

}