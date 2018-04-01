package es.ua.dlsi.im3.analysis.hierarchical.motives;

public abstract class MotiveNodeLabel<MotiveType extends Motive> extends MotivesNodeLabel<MotiveType> {
	MotiveType motive;
	
	public MotiveNodeLabel(MotiveType motive) {
		this.motive = motive;
	}

	public MotiveNodeLabel() {		
	}
	
	

	public MotiveType getMotive() {
		return motive;
	}
	public void setMotive(MotiveType motive) {
		this.motive = motive;
	}

	@Override
	public String toString() {
		return "MotiveNodeLabel [motive=" + motive + "]";
	}
	
	
}
