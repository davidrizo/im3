package es.ua.dlsi.im3.omr.encoding.agnostic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.VerticalLine;

import java.util.Arrays;

/**
 * It creates agnostic symbol types from strings
 * @autor drizo
 */
public class AgnosticSymbolTypeFactory {
    public static AgnosticSymbolType parseString(String string) throws IM3Exception {
        AgnosticSymbolType agnosticSymbolType = null;

        if (string.isEmpty()) {
            throw new IM3Exception("Empty string");
        }
        //SemanticNote AgnosticSymbolType.SEPSYMBOL is a .
        String [] tokens = string.split("\\.");
        if (tokens.length == 0) {
            throw new IM3Exception("Empty tokens in string '" + string + "'");
        }
        if (tokens[0].equals(VerticalLine.BARLINE_V1)) {
            return new VerticalLine(AgnosticVersion.v1);
        } else if (tokens[0].equals(VerticalLine.BARLINE_V2)) {
            return new VerticalLine(AgnosticVersion.v2);
        }

        String className;
        switch (tokens[0]) {
            case "metersign":
                className = "MeterSign";
                break;
            case "gracenote":
                className = "GraceNote";
                break;
            default:
                className = tokens[0].substring(0, 1).toUpperCase() + tokens[0].substring(1);
        }

        String qualifiedclassName = "es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols." + className;

        try {
            Class<?> agnosticTypeClass = Class.forName(qualifiedclassName);
            agnosticSymbolType = (AgnosticSymbolType) agnosticTypeClass.newInstance();
            if (tokens.length == 2) {
                agnosticSymbolType.setSubtype(tokens[1]);
            }
        } catch (ClassNotFoundException e) {
            throw new IM3Exception("Cannot find a class named: " + qualifiedclassName + " for agnostic symbol type '" + string + "'");
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IM3Exception("Cannot instantiate default constructor of " + qualifiedclassName, e);
        }


        return agnosticSymbolType;
    }
}
