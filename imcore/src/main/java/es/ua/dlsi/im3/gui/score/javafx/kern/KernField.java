package es.ua.dlsi.im3.gui.score.javafx.kern;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.io.kern.MensImporter;

/**
 * @autor drizo
 */
public class KernField {
    private static final String EMPTYSTRING = "";
    String value;
    Object parsedObject;

    public KernField() {
    }

    //TODO Usar el KernExporter
    public KernField(String value) {
        if (value != null) {
            MensImporter mensImporter = new MensImporter();
            //this.parsedObject = mensImporter.importField(value);
            this.value = value;
            throw new UnsupportedOperationException("TO-DO");
        }
    }

    public static KernField parseString(String string) throws ImportException {
        return new KernField(string);
    }

    @Override
    public String toString() {
        if (value == null) {
            return EMPTYSTRING;
        } else {
            return value;
        }
    }

    public Object getParsedObject() {
        return parsedObject;
    }
}
