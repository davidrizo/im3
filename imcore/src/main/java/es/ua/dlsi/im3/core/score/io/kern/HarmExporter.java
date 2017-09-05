package es.ua.dlsi.im3.core.score.io.kern;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.harmony.*;

/**
 * Created by drizo on 21/6/17.
 */
public class HarmExporter {
    private StringBuilder sb;

    public String exportHarm(Harm harm) throws ExportException {
        this.sb = new StringBuilder();
        doExportHarm(harm);

        return sb.toString();
    }

    private void doExportHarm(Harm harm) throws ExportException {
        boolean first = true;
        for (ChordSpecification cs: harm.getChordSpecifications()) {
            if (!first) {
                sb.append('/');
            }
            first = false;

            if (cs.isImplicit()) {
                sb.append('(');
            }
            exportChordSpecification(cs);
            if (cs.isImplicit()) {
                sb.append(')');
            }
        }

        if (harm.getAlternate() != null) {
            sb.append('[');
            doExportHarm(harm.getAlternate());
            sb.append(']');
        }



    }

    private void exportChordSpecification(ChordSpecification cs) throws ExportException {
        if (cs instanceof RomanNumberChordSpecification) {
            exportRomanNumberChordSpecification((RomanNumberChordSpecification)cs);
        } else if (cs instanceof SpecialChord) {
            exportSpecialChord((SpecialChord)cs);
        } else if (cs instanceof NonFunctionalChord) {
            exportNonFunctionalChord((NonFunctionalChord)cs);
        } else {
            throw new ExportException("Unsupported chord specification type: " + cs.getClass());
        }
    }

    private void exportNonFunctionalChord(NonFunctionalChord cs) throws ExportException {
        sb.append("?-");
        sb.append(generateQualifiedDegree(cs.getDegree()));
        if (cs.getIntervals() != null) {
            for (ChordInterval interval: cs.getIntervals()) {
                exportChordInterval(interval);
            }
        }
    }

    private void exportSpecialChord(SpecialChord cs) throws ExportException {
        if (cs.isEnharmonicSpelling()) {
            sb.append('~');
        }

        if (cs instanceof NeapolitanChord) {
            sb.append('N');
        } else if (cs instanceof ItalianChord) {
            sb.append("Lt");
        } else if (cs instanceof FrenchChord) {
            sb.append("Fr");
        } else if (cs instanceof GermanChord) {
            sb.append("Gn");
        } else if (cs instanceof TristanChord) {
            sb.append("Tr");
        } else {
            throw new ExportException("Unsupported special chord: " + cs.getClass());
        }

        if (cs.getInversion() != null) {
            sb.append(generateInversion(cs.getInversion()));
        }

    }

    private void exportRomanNumberChordSpecification(RomanNumberChordSpecification cs) throws ExportException {
        if (cs.getAlteration() != null) {
            if (cs.getAlteration() == ChordRootAlteration.lowered) {
                sb.append('-');
            } else if (cs.getAlteration() == ChordRootAlteration.raised) {
                sb.append('#');
            } else {
                throw new ExportException("Unsupported chord alteration: " + cs.getAlteration());
            }
        }

        sb.append(generateQualifiedDegree(cs.getRoot()));
        if (cs.getExtensions() != null) {
            for (ChordInterval interval: cs.getExtensions()) {
                exportChordInterval(interval);
            }
        }
        if (cs.getInversion() != null) {
            sb.append(generateInversion(cs.getInversion()));
        }
    }

    private String generateQualifiedDegree(QualifiedDegree degree) throws ExportException {
        switch (degree.getDegreeType()) {
            case major:
                return degree.getDegree().name().toUpperCase();
            case minor:
                return degree.getDegree().name().toLowerCase();
            case augmented:
                return degree.getDegree().name().toUpperCase() + "+";
            case diminished:
                return degree.getDegree().name().toLowerCase() + "o";
            default:
                throw new ExportException("Unsupported degree type: " + degree.getDegreeType());
        }
    }

    private String generateInversion(ChordInversion inversion) throws ExportException {
        switch (inversion) {
            case root: return "";
            case first: return "a";
            case second: return "b";
            case thrird: return "c";
            case fourth: return "d";
            case fifth: return "e";
            case sixth: return "f";
            default:
                throw new ExportException("Unsupported inversion: " + inversion);

        }
    }

    private void exportChordInterval(ChordInterval interval) throws ExportException {
        if (interval.getQuality() != null) {
            sb.append(generateIntervalQuality(interval.getQuality()));
        }
        sb.append(interval.getInterval());
    }

    private String generateIntervalQuality(ChordIntervalQuality quality) throws ExportException {
        switch (quality) {
            case major: return "M";
            case minor: return "m";
            case perfect: return "P";
            case augmented: return "A";
            case diminished: return "D";
            case double_augmented: return "AA";
            case double_diminished: return "DD";
            default:
                throw new ExportException("Unsupported chord quality: " + quality);

        }
    }

}
