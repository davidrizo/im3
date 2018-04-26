package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * @author drizo
 */
public class Beam implements INoteDurationSpecification {
    BeamType beamType;
    int beams;

    public Beam(BeamType beamType, int beams) {
        this.beamType = beamType;
        this.beams = beams;
    }

    public Beam(int beams) {
        this.beams = 1;
    }

    public static INoteDurationSpecification parseAgnosticString(String string) throws IM3Exception {
        for (BeamType bt: BeamType.values()) {
            if (string.startsWith(bt.toAgnosticString())) {
                String beamsStr = string.substring(bt.toAgnosticString().length());
                return new Beam(bt, Integer.parseInt(beamsStr));
            }
        }
        throw new IM3Exception("Cannot parse '" + string + "' as a beam");
    }

    public BeamType getBeamType() {
        return beamType;
    }

    public void setBeamType(BeamType beamType) {
        this.beamType = beamType;
    }

    public int getBeams() {
        return beams;
    }

    public void setBeams(int beams) {
        this.beams = beams;
    }

    @Override
    public String toAgnosticString() {
        if (beamType != null) {
            return beamType.toAgnosticString() + beams;
        } else {
            return "<UNSETBEAM> " + beams;
        }
    }
}
