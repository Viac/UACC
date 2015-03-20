package ua.com.glady.uacc.model.calculators;

import ua.com.glady.uacc.model.Constants;
import ua.com.glady.uacc.tools.StringTable;
import ua.com.glady.uacc.tools.ToolsStringTable;

/**
 * This class contains single item of Backward Calculation output
 * A structure that contains header, some details (explanation) and table with calculation
 * results
 *
 * Created by vgl on 19.03.2015.
 */
public class BcOutput {


    // What will be shown as caption
    private final String header;
    // any information text
    private final String info;
    // Reverse calculation results
    private final StringTable table;

    /**
     * Getter for table.
     * Used to put calculation results in result table.
     *
     * @return table
     */
    public StringTable getTable() {
        return table;
    }

    public BcOutput(String header, String info) {
        this.header = header;
        this.info = info;
        table = new StringTable();
    }

    boolean isRowEmpty(int row){
        for (int col = 1; col <= table.getMaxCol(); col++){
            if (!table.getCell(row, col).equals(Constants.EMPTY_MONEY_VALUE))
                return false;
        }
        return true;
    }

    /**
     * Removes rows from result tables which doesn't have data
     * since amount of these rows could be much enough. row header (col1)
     * combined with first empty row
     *
     * Ex:
     * Col1         42     42     42
     * Col2         42     42     -
     * Col3         -      -      -
     * Col4         -      -      -
     *
     * transferred to
     * Col1         42     42     42
     * Col2         42     42     -
     * Col3 - Col4  -      -      -

     */
    void removeLastEmptyRows() {
        // doesn't make sense to run it with empty table
        if (table.getMaxRow() < 0)
            return;

        boolean stillHaveEmptyRows = isRowEmpty(table.getMaxRow());
        if (!stillHaveEmptyRows)
            return;

        String sLast = table.getCell(table.getMaxRow(), 0);
        while (stillHaveEmptyRows && (table.getMaxRow() > 0)) {
            table.removeRow(table.getMaxRow());
            stillHaveEmptyRows = isRowEmpty(table.getMaxRow()) && (isRowEmpty(table.getMaxRow() - 1));
        }
        // updating col #0 of the table to show range of deleted items
        table.setCell(table.getMaxRow(), 0,
                table.getCell(table.getMaxRow(), 0) + Constants.EMPTY_MONEY_VALUE + sLast);
    }

    /**
     * Returns html text with object content
     *
     * @return html text with object content
     */
    public String getAsHtml() {
        String result = "";
        if (!header.isEmpty())
            result += "<h2>" + header + "</h2>";
        if (!info.isEmpty())
            result += info;

        removeLastEmptyRows();

        result += ToolsStringTable.StringTableToHtml(table, true);
        return result;
    }

}
