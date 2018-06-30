package es.ua.dlsi.im3.analysis.hierarchical.tonal;

import es.ua.dlsi.im3.core.adt.tree.ITreeLabel;
import es.ua.dlsi.im3.core.score.ScoreAnalysisHook;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.harmony.Harm;

/**
 * @autor drizo
 */
public abstract class TonalAnalysisTreeNodeLabel implements ITreeLabel {
    /**
     * It indicates the start of the section
     */
    Harm harm;

    public TonalAnalysisTreeNodeLabel(Harm harm) {
        this.harm = harm;
    }

    public Harm getHarm() {
        return harm;
    }

    public Time getTime() {
        return harm.getTime();
    }

    @Override
    public Double getPredefinedHorizontalPosition() {
        return harm.getTime().getComputedTime();
    }

    public abstract ITreeLabel clone();

}
