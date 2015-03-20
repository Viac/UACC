package ua.com.glady.uacc.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Class provides basis string table
 *
 * Created by Slava on 19.03.2015.
 */
public class StringTable {

    private final int UNDEFINED = -1;

    public int getMaxCol() {
        return maxCol;
    }

    public int getMaxRow() {
        return maxRow;
    }

    private class Cell{
        final int row;
        final int col;
        String string;

        public Cell(int row, int col, String string){
            this.string = string;
            this.row = row;
            this.col = col;
        }
    }

    private int maxCol = UNDEFINED;
    private int maxRow = UNDEFINED;

    // "Table" in fact is a list where each item has 'col' and 'row'
    private final List<Cell> table = new ArrayList<>();

    /**
     * Returns cell index from the cells list
     * @param row - row index
     * @param col - col index
     * @return cell index in table list, undefined if not found
     */
    private int getCellIndex(int row, int col){
        for (Cell cell : table){
            if ((cell.col == col) && (cell.row == row))
                return table.indexOf(cell);
        }
        return UNDEFINED;
    }

    /**
     * Returns text from certain cell or empty string if the cell empty(or doesn't exists)
     * @param row - row index
     * @param col - col index
     * @return - text from specified cell
     */
    public String getCell(int row, int col){
        for (Cell cell : table){
            if ((cell.col == col) && (cell.row == row))
                return cell.string;
        }
        return "";
    }

    /**
     * Sets text to the certain cell. Updates table dimensions if needed
     * @param row - row index
     * @param col -col   index
     * @param string - a text itself
     */
    public void setCell(int row, int col, String string){
        int idx = getCellIndex(row, col);
        if (idx == UNDEFINED)
            table.add(new Cell(row, col, string));
        else
            table.get(idx).string = string;
        if (row > maxRow)
            maxRow = row;
        if (col > maxCol)
            maxCol = col;
    }

    /**
     * Removes certain row from table. It will not recalculate other rows, in fact, only
     * cells removed.
     * @param row - row index to remove
     */
    public void removeRow(int row){
        if (row < 0)
            return;
        for (int i = table.size() - 1; i >= 0; i--){
            if (table.get(i).row == row)
                table.remove(i);
        }
        if (row == maxRow)
            maxRow--;
    }

}
