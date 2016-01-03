package ua.com.glady.uacc.model.calculators;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import ua.com.glady.uacc.R;
import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.model.types.VehicleSpecificImpost;
import ua.com.glady.uacc.tools.StringTable;
import ua.com.glady.uacc.tools.ToolsStr;
import ua.com.glady.uacc.tools.ToolsStringTable;

import static java.lang.Double.compare;
import static ua.com.glady.uacc.tools.ToolsStr.getMoneyStr;

/**
 * Implementation of forward calculator. See the R.raw.definition_of_terms for more details
 *
 * Created by vgl on 19.03.2015.
 */
public class ForwardCalc {

    public int getTotalPrice() {
        return totalPrice;
    }

    // Means basic price with all custom duties
    private int totalPrice = Constants.UNDEFINED;
    // Means sum of all tolls, duties, excises, i.e. each payments
    private int totalCustomDuties = Constants.UNDEFINED;
    // Percentage rate of vehicle price increase related to basic price
    private int priceMultiplier = Constants.UNDEFINED;

    // It's easier to store result in the table
    private final StringTable tResult = new StringTable();

    /**
     * Different vehicle could have different duties. So by design result of forward calculation
     * is a list of items that could be different for each client. Sample of this item:
     * "VAT", "20%", 2000
     * "Excise", "based on law", 325
     */
    private class FcItem {
        String name;
        String description;
        int value;
    }

    private final List<FcItem> itemList = new ArrayList<>();

    void add(String name, String description, int value){
        FcItem item = new FcItem();
        item.name = name;
        item.description = description;
        item.value = value;
        itemList.add(item);
    }

    /**
     *  Calculates data
     *
     * @param basicPrice - basic price
     * @param excise - excise value
     * @param impostBase - multiplier value of impost base (i.e., 0,1 if impost = 10%)
     * @param etc - Code {@link ua.com.glady.uacc.R.raw definition_of_terms}
     * @param resources - to get localized text
     */
    public void calculate(int basicPrice, double excise, double impostBase, double specialImpostBase,
                          List<VehicleSpecificImpost> vehicleSpecificImpostList,
                          String etc, Resources resources) {
        totalPrice = basicPrice;

        String template;
        int impost = (int) Math.round(basicPrice * impostBase);
        template = resources.getString(R.string.ImpostDescription);
        int impostBasePercentage = (int) Math.round(impostBase * 100);
        add(resources.getString(R.string.Impost), String.format(template, impostBasePercentage), impost);

        int exciseValue = (int) Math.round(excise);
        template = resources.getString(R.string.ExciseDescription);
        add(resources.getString(R.string.Excise), String.format(template, etc), exciseValue);
        totalPrice += exciseValue;

        if (compare(specialImpostBase, 0.0) != 0){
            int specialImpost = (int) Math.round(basicPrice * specialImpostBase);
            template = resources.getString(R.string.SpecialImpostDescription);
            double specialImpostPercentage = specialImpostBase * 100;
            String specialImpostDescription = String.format(template, specialImpostPercentage);
            add(resources.getString(R.string.SpecialImpost), specialImpostDescription, specialImpost);
            totalPrice += exciseValue;
        }

        if (vehicleSpecificImpostList.size() > 0){
            for (VehicleSpecificImpost vehicleSpecificImpost : vehicleSpecificImpostList ){
                if (compare(vehicleSpecificImpost.value, 0.0) != 0) {
                    int value = (int) Math.round(basicPrice * vehicleSpecificImpost.value / 100);
                    add(vehicleSpecificImpost.header, vehicleSpecificImpost.description, value);
                    totalPrice += value;
                }
            }
        }

        int vat = (int) Math.round((totalPrice) * Constants.VAT_BASE);
        template = resources.getString(R.string.VATDescription);
        String vatStr = String.format(template, ToolsStr.makePlusList(1, itemList.size() + 1));
        add(resources.getString(R.string.VAT), vatStr, vat);

        totalPrice += vat;
    }

    /**
     * Results of the forward calculation presented as string table. This method creates it.
     * @param res to get localized string
     * @param basicPrice will be shown in the table and used in calculations
     */
    private void makeResultTable(Resources res, int basicPrice){
        int row = 0;
        tResult.setCell(row, 0, res.getString(R.string.TableHeaderId));
        tResult.setCell(row, 1, res.getString(R.string.TableHeaderCaption));
        tResult.setCell(row, 2, res.getString(R.string.Euro));
        row++;

        tResult.setCell(row, 0, String.valueOf(row));
        tResult.setCell(row, 1, res.getString(R.string.BasicPrice));
        tResult.setCell(row, 2, getMoneyStr(basicPrice, "-"));
        row ++;

        totalCustomDuties = 0;
        for (FcItem fcItem : itemList ){
            tResult.setCell(row, 0, String.valueOf(row));
            tResult.setCell(row, 1, fcItem.name + Constants.sLineBreak + fcItem.description);
            tResult.setCell(row, 2, getMoneyStr(fcItem.value, "-"));
            totalCustomDuties += fcItem.value;
            row ++;
        }

        totalPrice = basicPrice + totalCustomDuties;
        double d = (( (double) totalPrice / (double) basicPrice ) - 1) * 100;
        priceMultiplier = (int) Math.round(d);
    }

    /**
     * In each cell of table defines 2nd line as 'details' style. In the html template
     * this style allows to show it in another way
     */
    void highlightDetails(){
        String oldText;
        String[] list;
        for (int row = 0; row <= tResult.getMaxRow(); row++){
            for (int col = 0; col <= tResult.getMaxCol(); col++){
                oldText = tResult.getCell(row, col);
                if (oldText.contains("\r\n")){
                    list = oldText.split("\\r\\n");
                    StringBuilder sb = new StringBuilder();
                    sb.append(list[0]).append("</br><p class=\"details\">");
                    for (int i = 1; i < list.length; i++){
                        sb.append(list[i]);
                        if (i < (list.length - 1))
                            sb.append("</br>");
                    }
                    sb.append("</p>");
                    tResult.setCell(row, col, sb.toString());
                }
            }
        }
    }

    /**
     * @param res required to get localized strings
     * @param htmlTemplate must contain strings GET_RESULT_RESUME and GET_RESULT_TABLE
     * @param basicPrice only one thing that we have to know to build the result table
     * @return html string with calculator result
     */
    public String getHtml(Resources res, String htmlTemplate, int basicPrice){
        if (basicPrice <= 0)
            throw new IllegalArgumentException(res.getString(R.string.errCanNotRunCalculation));
        makeResultTable(res, basicPrice);
        highlightDetails();
        String s = htmlTemplate.replace("GET_RESULT_RESUME", getResultResumeHtml(res));
        return s.replace("GET_RESULT_TABLE", ToolsStringTable.StringTableToHtml(tResult, true));
    }

    /**
     * @param res required to get localized strings
     * @return html string with output resume information (highlighted total price and
     * total custom duties)
     */
    private String getResultResumeHtml(Resources res) {
        String result = "<h2>" + res.getString(R.string.TotalPrice) + ": " +
                getMoneyStr(totalPrice, "-") + res.getString(R.string.Euro) + "</h2>";
        result += "<p>" + res.getString(R.string.CustomDuties) + ": " +
                getMoneyStr(totalCustomDuties, "-") + res.getString(R.string.Euro);
        result += " ( +" + String.valueOf(priceMultiplier) + "%)</p>";
        return result;
    }

}