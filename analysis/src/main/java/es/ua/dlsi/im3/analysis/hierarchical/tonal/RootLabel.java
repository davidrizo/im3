package es.ua.dlsi.im3.analysis.hierarchical.tonal;

import es.ua.dlsi.im3.core.score.Time;

public class RootLabel extends TonalAnalysisTreeNodeLabel {

	public RootLabel() {
		super(null);
	}

	@Override
	public String getStringLabel() {
		return "Root";
	}

	@Override
	public String getColor() {
		return null;
	}

	@Override
	public TonalAnalysisTreeNodeLabel clone() {
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
