package es.ua.dlsi.im3.analysis.hierarchical.tonal;

import es.ua.dlsi.im3.core.adt.tree.ITreeLabel;
import es.ua.dlsi.im3.core.score.harmony.ChordSpecification;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import es.ua.dlsi.im3.core.score.harmony.QualifiedDegree;

import java.util.ArrayList;

/**
 * @autor drizo
 */
public class DegreeNodeLabel extends TonalAnalysisTreeNodeLabel {
    ArrayList<ChordSpecification> chordSpecifications;

    public DegreeNodeLabel(Harm harm, ArrayList<ChordSpecification> chordSpecifications) {
        super(harm);
        this.chordSpecifications = chordSpecifications;
    }

    public ArrayList<ChordSpecification> getChordSpecifications() {
        return chordSpecifications;
    }

    @Override
    public String getStringLabel() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ChordSpecification chordSpecification: chordSpecifications) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append('/');
            }
            String string = chordSpecification.toString();
            stringBuilder.append(string);
        }

        return stringBuilder.toString();
    }

    @Override
    public String getColor() throws Exception {
        return null;
    }

    @Override
    public ITreeLabel clone() {
        return new DegreeNodeLabel(harm, chordSpecifications);
    }
}
