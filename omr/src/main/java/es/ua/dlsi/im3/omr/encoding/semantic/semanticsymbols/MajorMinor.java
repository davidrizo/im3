package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

public enum MajorMinor {
    major("M"), minor("m");

    private String semanticString;

    MajorMinor(String semanticString) {
        this.semanticString = semanticString;
    }

    public String toSemanticString() {
        return this.semanticString;
    }
}
