package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

public enum StartEnd {
    start, end;

    public String toAgnosticString() {
        return this.name();
    }
}
