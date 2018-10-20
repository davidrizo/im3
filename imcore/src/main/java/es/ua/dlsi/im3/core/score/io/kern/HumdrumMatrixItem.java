package es.ua.dlsi.im3.core.score.io.kern;

/**
 * @autor drizo
 */
public class HumdrumMatrixItem {
    String humdrumEncoding;
    Object parsedObject;

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

    @Override
    public String toString() {
        return humdrumEncoding;
    }
}
