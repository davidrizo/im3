package es.ua.dlsi.im3.gui.score.javafx;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * @autor drizo
 */
public class KernRecord {
    ObservableList<String> fields;

    public KernRecord(int width) {
        fields = FXCollections.observableArrayList();
    }

    public ObservableList<String> fieldsProperty() {
        return fields;
    }

    public void add(String field) {
        fields.add(field);
    }
}
