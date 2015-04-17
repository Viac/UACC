package ua.com.glady.uacc.model.calculators;

import java.util.ArrayList;
import java.util.List;

import ua.com.glady.uacc.model.Constants;

/**
 * Provides basic functionality of backward calculator.
 * By design this calculator must receive final price in Ukraine and return
 * output in form of few bundles of data in format "Header" - "Details" - "Data table"
 *
 * This object doesn't fill this outputs, it only stores them. To make this outputs (BcOutput)
 * implement it in Vehicle subclassed
 *
 * Created by vgl on 19.03.2015.
 */
public class BackwardCalc {

    // list of output bundles
    private final List<BcOutput> outputList;

    public BackwardCalc() {
        outputList = new ArrayList<>();
    }

    /**
     * Adds external output bundle to the list
     * @param bcOutput output bundle
     */
    public void addBcOutput(BcOutput bcOutput){
        outputList.add(bcOutput);
    }

    /**
     * Clears output bundles list
     */
    public void clear(){
        outputList.clear();
    }

    /**
     * Returns html text with result of calculation
     * @param htmlTemplate - html template, must have GET_RESULT_OUTPUT text inside
     * @return html text with result of calculation
     */
    public String getHtml(String htmlTemplate) {
        StringBuilder output = new StringBuilder();
        for (BcOutput out : outputList) {
            output.append(out.getAsHtml()).append("</br>");
        }

        return htmlTemplate.replace("GET_RESULT_OUTPUT", output.toString());
    }

    /**
     * Implements one of the method of calculating basic price.
     *
     * @param finalPrice target final price in Ukraine
     * @param impost double value of impost (%), i.e. 0.1 if impost = 10%
     * @param specialImpost double value of impost (%), i.e. 0.0253 if impost = 2.53%
     * @param excise rounded excise value
     * @return basic price
     */
    public int getBasicPrice(int finalPrice, double impost, double specialImpost, int excise) {
        double vatM = Constants.VAT_BASE + 1;
        double result = (finalPrice / vatM - excise) /
                (1 + Constants.TEMPORARY_PROTECTION_FEE_BASE + impost + specialImpost);
        return (int) Math.round(result);
    }

}
