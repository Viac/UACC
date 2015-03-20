package ua.com.glady.uacc.tools;

/**
 * This class contains static methods which used to expand StingTable functionality
 *
 * Created by Slava on 19.03.2015.
 */
public class ToolsStringTable {

    /**
     * Converts string table to html table
     * If cell is empty - html cell will contain default text
     * Output contains only table-tags (not full html environment)
     * @param table source table
     * @return html table
     */
    public static String StringTableToHtml(StringTable table, boolean makeFirstRowCenter){
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        for (int row = 0; row <= table.getMaxRow(); row++){
            sb.append("<tr>");
            for (int col = 0; col <= table.getMaxCol(); col++){
                sb.append("<td>");
                if (makeFirstRowCenter && (row == 0))
                    sb.append("<p align= \"center\">");
                sb.append(table.getCell(row, col).replaceAll("\\r\\n", "</br>"));
                sb.append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

}
