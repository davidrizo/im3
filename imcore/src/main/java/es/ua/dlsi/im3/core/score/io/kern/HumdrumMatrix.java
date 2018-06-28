package es.ua.dlsi.im3.core.score.io.kern;

import java.util.ArrayList;

/**
 * A matrix representing either **krn or **mens data
 * @autor drizo
 */
public class HumdrumMatrix {
    ArrayList<ArrayList<HumdrumMatrixItem>> matrix;
    private ArrayList<HumdrumMatrixItem> currentRow;

    public HumdrumMatrix() {
        matrix = new ArrayList<>();
    }

    public void addRow() {
        currentRow = new ArrayList<>();
        matrix.add(currentRow);
    }

    public void addItemToCurrentRow(HumdrumMatrixItem item) {
        if (currentRow == null) {
            addRow();
        }
        currentRow.add(item);
    }

    public ArrayList<ArrayList<HumdrumMatrixItem>> getMatrix() {
        return matrix;
    }

    public void addItemToCurrentRow(String text, Object parsedObject) {
        addItemToCurrentRow(new HumdrumMatrixItem(text, parsedObject));
    }

    public void addItemToCurrentRow(String text) {
        addItemToCurrentRow(new HumdrumMatrixItem(text));
    }

    public HumdrumMatrixItem get(int row, int column) {
        return matrix.get(row).get(column);
    }
}
