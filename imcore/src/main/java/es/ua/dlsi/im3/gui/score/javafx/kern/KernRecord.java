package es.ua.dlsi.im3.gui.score.javafx.kern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @autor drizo
 */
public class KernRecord {
    ObservableList<KernField> fields;

    public KernRecord(int width) {
        fields = FXCollections.observableArrayList();
    }

    public ObservableList<KernField> fieldsProperty() {
        return fields;
    }

    public void add(KernField fieldValue) {
        fields.add(fieldValue);
    }

    public KernField getField(int spine) {
        return fields.get(spine);
    }

    public void setField(int spine, KernField value) {
        fields.set(spine, value);
    }
}
