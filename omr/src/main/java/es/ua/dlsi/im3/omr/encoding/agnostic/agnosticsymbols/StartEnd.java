package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

public enum StartEnd {
    start, end;

    public static StartEnd parseAgnosticString(String string) {
        return StartEnd.valueOf(string);
    }

    public String toAgnosticString() {
        return this.name();
    }
}
