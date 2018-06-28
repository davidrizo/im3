package es.ua.dlsi.im3.analysis.hierarchical.forms;


import es.ua.dlsi.im3.core.adt.tree.ITreeLabel;
import es.ua.dlsi.im3.core.score.Time;

public abstract class FormAnalysisTreeNodeLabel implements ITreeLabel {
	public abstract FormAnalysisTreeNodeLabel clone();
	public abstract Time getTime();
}
