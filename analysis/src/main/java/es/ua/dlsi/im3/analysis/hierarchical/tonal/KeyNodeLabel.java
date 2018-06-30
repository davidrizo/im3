package es.ua.dlsi.im3.analysis.hierarchical.tonal;

import es.ua.dlsi.im3.core.adt.tree.ITreeLabel;
import es.ua.dlsi.im3.core.score.Key;
import es.ua.dlsi.im3.core.score.ScoreAnalysisHook;
import es.ua.dlsi.im3.core.score.harmony.Harm;

/**
 * @autor drizo
 */
public class KeyNodeLabel extends TonalAnalysisTreeNodeLabel {
    Key key;

    public KeyNodeLabel(Harm harm, Key key) {
        super(harm);
        this.key = key;
    }

    @Override
    public String getStringLabel() {
        return key.getAbbreviationString();
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    public ITreeLabel clone() {
        return new KeyNodeLabel(harm, key);
    }
}
