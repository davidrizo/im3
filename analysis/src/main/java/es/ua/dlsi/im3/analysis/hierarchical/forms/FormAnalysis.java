package es.ua.dlsi.im3.analysis.hierarchical.forms;

import es.ua.dlsi.im3.analysis.hierarchical.TreeAnalysis;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.adt.tree.TreeException;
import es.ua.dlsi.im3.core.score.ScoreAnalysisHook;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Time;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FormAnalysis extends TreeAnalysis<FormAnalysisTreeNodeLabel> {
	
	public FormAnalysis(ScoreSong song) throws IM3Exception {
		super(song);		
		hierarchicalStructure = new Tree<FormAnalysisTreeNodeLabel>(new RootLabel());
	}
	public void addSection(String name, Time time, String description, String hexaColor) throws IM3Exception, TreeException {
		Tree<FormAnalysisTreeNodeLabel> nextSection = null;
		for (Iterator<Tree<FormAnalysisTreeNodeLabel>> iterator = hierarchicalStructure.getChildrenIterator(); nextSection == null && iterator.hasNext();) {
			Tree<FormAnalysisTreeNodeLabel> section = iterator.next();
			if (!(section.getLabel() instanceof SectionLabel)) {
				throw new IM3Exception("The first level should contain just SectionLabel objects, and this is a " + section.getLabel().getClass());
			}
			SectionLabel sectionLabel = (SectionLabel) section.getLabel();
			
			if (sectionLabel.getScoreAnalysisHookStart().getTime().compareTo(time)>0) {
				nextSection = section;
			} else if (sectionLabel.getScoreAnalysisHookStart().getTime().equals(time)) {
				throw new IM3Exception("Cannot set two sections at the same time: " + time);
			} 
		}
		
		ScoreAnalysisHook hookFrom = song.getAnalysisStaff().findLastAnalysisHookBeforeOrEqualsOnset(time);
		SectionLabel label = new SectionLabel(name, hookFrom);
		label.setDescription(description);
		label.setHexaColor(hexaColor);
		Tree<FormAnalysisTreeNodeLabel> newTree = new Tree<>(label);		
		
		if (nextSection == null) { // the last one
			hierarchicalStructure.addChild(newTree);			
		} else {
			hierarchicalStructure.addChildBefore(nextSection, newTree);						
		}
		
		//System.out.println(hierarchicalStructure.toFunctionalString());
	}
	
	public List<SectionLabel> getSections() throws IM3Exception {
		ArrayList<SectionLabel> result = new ArrayList<>();
		for (Iterator<Tree<FormAnalysisTreeNodeLabel>> iterator = hierarchicalStructure.getChildrenIterator(); iterator.hasNext();) {
			Tree<FormAnalysisTreeNodeLabel> child = iterator.next();
			if (!(child.getLabel() instanceof SectionLabel)) {
				throw new IM3Exception("The first level should contain just SectionLabel objects, and this is a " + child.getLabel().getClass());
			}
			SectionLabel sectionLabel = (SectionLabel) child.getLabel();
			result.add(sectionLabel);
		}
		return result;
	}
}
