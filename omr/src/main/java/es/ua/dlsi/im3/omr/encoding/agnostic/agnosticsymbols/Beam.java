package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

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
