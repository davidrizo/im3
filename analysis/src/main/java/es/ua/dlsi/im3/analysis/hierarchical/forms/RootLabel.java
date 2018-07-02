package es.ua.dlsi.im3.analysis.hierarchical.forms;

import es.ua.dlsi.im3.core.score.Time;

public class RootLabel extends FormAnalysisTreeNodeLabel {
    String label;
	public RootLabel() {
		super();
		label = "Root";
	}

    public RootLabel(String label) {
        super();
        this.label = label;
    }

	@Override
	public String getStringLabel() {
		return label;
	}

	@Override
	public String getColor() {
		return null;
	}

	@Override
	public FormAnalysisTreeNodeLabel clone() {
		return new RootLabel();
	}

    @Override
    public Time getTime() {
        return Time.TIME_ZERO;
    }

    @Override
    public Double getPredefinedHorizontalPosition() {
        return null;
    }

}
