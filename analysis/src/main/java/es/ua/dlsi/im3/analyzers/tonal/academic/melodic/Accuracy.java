package es.ua.dlsi.im3.analyzers.tonal.academic.melodic;

import es.ua.dlsi.im3.core.score.AtomPitch;
import es.ua.dlsi.im3.core.score.MelodicFunction;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.Map;

/**
 * It computes the accuracy of a melodic analysis
 * Created by drizo on 24/6/17.
 */
public class Accuracy {
    private final MelodicAnalysis expected;
    private final MelodicAnalysis computed;
    int [][] confusionMatrix;
    int notesWithoutExpectedAnalysis;
    int ok;
    int ko;

    public Accuracy(MelodicAnalysis expected, MelodicAnalysis computed) {
        this.confusionMatrix = new int[MelodicAnalysisNoteKinds.values().length][MelodicAnalysisNoteKinds.values().length];
        this.expected = expected;
        this.computed = computed;
        computeAccuracy();
    }

    private void computeAccuracy() {
        notesWithoutExpectedAnalysis = 0;
        ok = 0;
        ko = 0;
        for (Map.Entry<AtomPitch, NoteMelodicAnalysis> noteMelodicAnalysisEntry: computed.getNoteAnalyses().entrySet()) {
            AtomPitch ap = noteMelodicAnalysisEntry.getKey();
            NoteMelodicAnalysis computedAnalysis = noteMelodicAnalysisEntry.getValue();

            NoteMelodicAnalysis expectedAnalysis = expected.getAnalysis(ap);
            if (expectedAnalysis == null) {
                notesWithoutExpectedAnalysis++;
            } else {
                confusionMatrix[expectedAnalysis.getKind().ordinal()] [computedAnalysis.getKind().ordinal()]++;
                if (expectedAnalysis.getKind() == computedAnalysis.getKind()) {
                    ok++;
                } else {
                    ko++;
                }
            }
        }
    }

    /**
     *
     * @return matrix[expected MelodicAnalysisNoteKinds][computed MelodicAnalysisNoteKinds]
     */
    public int[][] getConfusionMatrix() {
        return confusionMatrix;
    }

    public int getOk() {
        return ok;
    }

    public int getKo() {
        return ko;
    }

    public double getSuccessRate() {
        return (double) ok / (double) (ok + ko);
    }

    public int getNotesWithoutExpectedAnalysis() {
        return notesWithoutExpectedAnalysis;
    }
}
