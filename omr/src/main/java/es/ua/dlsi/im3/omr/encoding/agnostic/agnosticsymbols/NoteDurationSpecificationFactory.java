package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * @autor drizo
 */
public class NoteDurationSpecificationFactory {
    public static INoteDurationSpecification parseString(String string) throws IM3Exception {
        INoteDurationSpecification result = null;
        try {
            result = NoteFigures.parseAgnosticString(string);
        } catch (Throwable t) {
            //it is not a note, it must be a beam
            try {
                result = Beam.parseAgnosticString(string);
            } catch (IM3Exception e) {
                throw new IM3Exception("Cannot parse '" + string + "' as a note figure or as a beam");
            }
        }
        return result;
    }
}
