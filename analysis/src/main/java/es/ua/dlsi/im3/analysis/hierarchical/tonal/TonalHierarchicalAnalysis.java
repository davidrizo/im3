package es.ua.dlsi.im3.analysis.hierarchical.tonal;

import es.ua.dlsi.im3.analysis.hierarchical.TreeAnalysis;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.adt.tree.TreeException;
import es.ua.dlsi.im3.core.score.Key;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.harmony.ChordSpecification;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import es.ua.dlsi.im3.core.score.harmony.RomanNumberChordSpecification;

/**
 * It infers the tonal analysis from the Harm classes in the song
 * @autor drizo
 */
public class TonalHierarchicalAnalysis extends TreeAnalysis<TonalAnalysisTreeNodeLabel> {
    public TonalHierarchicalAnalysis(ScoreSong song) throws IM3Exception, TreeException {
        super(song);
        createTree();
    }

    private void createTree() throws IM3Exception, TreeException {
        this.hierarchicalStructure = new Tree<>(new RootLabel());

        Key lastKey = null;
        Tree<TonalAnalysisTreeNodeLabel> currentKeyTree = null;
        for (Harm harm: song.getOrderedHarms()) {
            Key key = harm.getKey();
            if (lastKey == null || !lastKey.equals(key)) {
                // add new branch
                currentKeyTree = new Tree<>(new KeyNodeLabel(harm, key));
                this.hierarchicalStructure.addChild(currentKeyTree);
                lastKey = key;
            }

            if (currentKeyTree == null) {
                throw new IM3Exception("Cannot add a degree without a key");
            }
            Tree<TonalAnalysisTreeNodeLabel> degreeNodeLabelTree = new Tree<>(new DegreeNodeLabel(harm, harm.getChordSpecifications()));
            currentKeyTree.addChild(degreeNodeLabelTree);
            ////DegreeNodeLabel degreeNodeLabel = new DegreeNodeLabel(harm, harm.)
        }
    }
}
