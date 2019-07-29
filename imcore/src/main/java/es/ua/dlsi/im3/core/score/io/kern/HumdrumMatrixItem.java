package es.ua.dlsi.im3.core.score.io.kern;

import java.util.ArrayList;

/**
 * @autor drizo
 */
public class HumdrumMatrixItem {
    String humdrumEncoding;
    Object parsedObject;
    ArrayList<Long> associatedIDS;

    public HumdrumMatrixItem(String humdrumEncoding, Object parsedObject) {
        this.humdrumEncoding = humdrumEncoding;
        this.parsedObject = parsedObject;
    }

    public HumdrumMatrixItem(String humdrumEncoding) {
        this.humdrumEncoding = humdrumEncoding;
    }

    public String getHumdrumEncoding() {
        return humdrumEncoding;
    }

    public void setHumdrumEncoding(String humdrumEncoding) {
        this.humdrumEncoding = humdrumEncoding;
    }

    public Object getParsedObject() {
        return parsedObject;
    }

    public void setParsedObject(Object parsedObject) {
        this.parsedObject = parsedObject;
    }

    public ArrayList<Long> getAssociatedIDS() {
        return associatedIDS;
    }

    public void setAssociatedIDS(ArrayList<Long> associatedIDS) {
        this.associatedIDS = associatedIDS;
    }

    @Override
    public String toString() {
        return humdrumEncoding;
    }
}
