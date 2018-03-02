package es.ua.dlsi.im3.omr.model.pojo;

public class SemanticToken {
    private static final char VALUE_SEPARATOR = '-';
    public static final char SUBVALUE_SEPARATOR = '_';

    SemanticSymbolEnum symbol;
    String value;

    public SemanticToken(SemanticSymbolEnum symbol, String value) {
        this.symbol = symbol;
        this.value = value;
    }

    public SemanticToken(SemanticSymbolEnum symbol) {
        this.symbol = symbol;
    }

    public SemanticSymbolEnum getSymbol() {
        return symbol;
    }

    public void setSymbol(SemanticSymbolEnum symbol) {
        this.symbol = symbol;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(symbol);
        if (value != null) {
            sb.append(VALUE_SEPARATOR);
            sb.append(value);
        }
        return sb.toString();
    }
}
