package es.ua.dlsi.grfia.im3.typographydesignhelper;

public class Symbol {
    String name;
    String unicode;

    public Symbol(String name, String unicode) {
        this.name = name;
        this.unicode = unicode;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
