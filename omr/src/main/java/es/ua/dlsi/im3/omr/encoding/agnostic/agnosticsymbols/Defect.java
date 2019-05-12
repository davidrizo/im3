package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.enums.Defects;

public class Defect extends AgnosticSymbolType {
    private static final String CODE = "defect" +  SEPSYMBOL;
    Defects defectType;

    public Defect(Defects defectType) {
        this.defectType = defectType;
    }

    public Defect() {
    }

    @Override
    public String toAgnosticString() {
        if (defectType != null) {
            return CODE + defectType.name();
        } else {
            return CODE;
        }
    }

    @Override
    public void setSubtype(String string) throws IM3Exception {
        this.defectType = Defects.valueOf(string);
    }
}
