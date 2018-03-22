package es.ua.dlsi.im3.analysis.hierarchical;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.ITreeLabel;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.score.ScoreSong;

/**
 * Leaves must implement ITimedElement
 * @author drizo
 *
 * @param <LabelType>
 */
public class TreeAnalysis<LabelType extends ITreeLabel> extends HierarchicalAnalysis<Tree<LabelType>>{
	public TreeAnalysis(ScoreSong song) throws IM3Exception {
		super(song);
	}

	public Tree<LabelType> getTree() {
		return hierarchicalStructure;
	}	
}
