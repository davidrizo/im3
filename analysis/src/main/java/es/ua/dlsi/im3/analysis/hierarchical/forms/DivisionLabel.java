package es.ua.dlsi.im3.analysis.hierarchical.forms;


import es.ua.dlsi.im3.core.score.ITimedElement;
import es.ua.dlsi.im3.core.score.ScoreAnalysisHook;

public class DivisionLabel extends FormAnalysisTreeNodeLabel implements ITimedLabel {
	String name;
	String description;
	/**
	 * e.g. red should be represented as FF0000
	 */
	String hexaColor;
	/**
	 * It indicates the start of the section
	 */
	ScoreAnalysisHook scoreAnalysisHookStart;
	
	/**
	 * @param name Should have the name property set
	 */
	public DivisionLabel(String name, ScoreAnalysisHook scoreAnalysisHookStart) {
		this.name = name;
		this.scoreAnalysisHookStart = scoreAnalysisHookStart;
	}
	
	@Override
	public String getStringLabel() {
		return name;
	}
	
	public ScoreAnalysisHook getScoreAnalysisHookStart() {
		return scoreAnalysisHookStart;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setScoreAnalysisHookStart(ScoreAnalysisHook scoreAnalysisHookStart) {
		this.scoreAnalysisHookStart = scoreAnalysisHookStart;
	}

	@Override
	public String getColor() throws Exception {
		return hexaColor;
	}
	
	@Override
	public DivisionLabel clone() {
		return new DivisionLabel(name, scoreAnalysisHookStart);
	}

	@Override
	public ITimedElement getTimedElement() {
		return this.scoreAnalysisHookStart;
	}

	public String getHexaColor() {
		return hexaColor;
	}

	public void setHexaColor(String hexaColor) {
		this.hexaColor = hexaColor;
	}


	
}
