package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.adt.Pair;

public class Attribute {
    String attribute;
    String value;

    public Attribute(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    public Attribute(String attribute, double value) {
        this.attribute = attribute;
        this.value = Double.toString(value);
    }

    public String getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }
}
