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
		hierarchicalStructure = new Tree<>(new RootLabel());
	}

    /**
     * Add division at root level
     * @param name
     * @param time
     * @param description
     * @param hexaColor
     * @return New created tree
     * @throws IM3Exception
     * @throws TreeException
     */
    public Tree<FormAnalysisTreeNodeLabel> addDivision(String name, Time time, String description, String hexaColor) throws IM3Exception, TreeException {
	    return addDivision(this.hierarchicalStructure, name, time, description, hexaColor);
    }

    /**
     * Add a hierarchical division below parent
     * @param parent
     * @param name
     * @param time
     * @param description
     * @param hexaColor
     * @return New created tree
     * @throws IM3Exception
     * @throws TreeException
     */
    public Tree<FormAnalysisTreeNodeLabel> addDivision(Tree<FormAnalysisTreeNodeLabel> parent, String name, Time time, String description, String hexaColor) throws IM3Exception, TreeException {
		Tree<FormAnalysisTreeNodeLabel> nextDivision = null;
		for (Iterator<Tree<FormAnalysisTreeNodeLabel>> iterator = parent.getChildrenIterator(); nextDivision == null && iterator.hasNext();) {
			Tree<FormAnalysisTreeNodeLabel> division = iterator.next();
			if (!(division.getLabel() instanceof DivisionLabel)) {
				throw new IM3Exception("The first level should contain just DivisionLabel objects, and this is a " + division.getLabel().getClass());
			}
			DivisionLabel divisionLabel = (DivisionLabel) division.getLabel();
			
			if (divisionLabel.getScoreAnalysisHookStart().getTime().compareTo(time)>0) {
				nextDivision = division;
			} else if (divisionLabel.getScoreAnalysisHookStart().getTime().equals(time)) {
				throw new IM3Exception("Cannot set two divisions at the same time: " + time);
			} 
		}
		
		ScoreAnalysisHook hookFrom = song.getAnalysisStaff().findLastAnalysisHookBeforeOrEqualsOnset(time);
		DivisionLabel label = new DivisionLabel(name, hookFrom);
		label.setDescription(description);
		label.setHexaColor(hexaColor);
		Tree<FormAnalysisTreeNodeLabel> newTree = new Tree<>(label);		
		
		if (nextDivision == null) { // the last one
            parent.addChild(newTree);
		} else {
            parent.addChildBefore(nextDivision, newTree);
		}

		return newTree;
		//System.out.println(hierarchicalStructure.toFunctionalString());
	}
	
	public List<DivisionLabel> getRootDivisions() throws IM3Exception {
		ArrayList<DivisionLabel> result = new ArrayList<>();
		for (Iterator<Tree<FormAnalysisTreeNodeLabel>> iterator = hierarchicalStructure.getChildrenIterator(); iterator.hasNext();) {
			Tree<FormAnalysisTreeNodeLabel> child = iterator.next();
			if (!(child.getLabel() instanceof DivisionLabel)) {
				throw new IM3Exception("The first level should contain just DivisionLabel objects, and this is a " + child.getLabel().getClass());
			}
			DivisionLabel divisionLabel = (DivisionLabel) child.getLabel();
			result.add(divisionLabel);
		}
		return result;
	}
}
