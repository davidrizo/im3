package es.ua.dlsi.im3.analyzers.tonal.academic.melodic;

import es.ua.dlsi.im3.core.score.AtomPitch;

/**
@author drizo
@date 27/11/2011
 **/
public class NoteMelodicAnalysis {
	public static final String CHANGED = "Changed";
	public static final String PROPAGATED = "Propagated";

	AtomPitch note;
	MelodicAnalysisNoteKinds kind;
	String rule;
	Confidence confidence;
	String comment;
	boolean violatesAPrioriConditions;
	MelodicAnalyzer classifier;
	/**
	 * Features with which the note analysis has been computed
	 */
	NoteMelodicAnalysisFeatures features;
	/**
	 * @param kind
	 * @param rule
	 * @param confidence
	 * @param comment*
	 */
	public NoteMelodicAnalysis(AtomPitch note, MelodicAnalyzer classifier, NoteMelodicAnalysisFeatures features, MelodicAnalysisNoteKinds kind, String rule,
			Confidence confidence, String comment) {
		super();
		this.features = features;
		this.note = note;
		this.kind = kind;
		this.rule = rule;
		this.confidence = confidence;
		this.comment = comment;
		this.violatesAPrioriConditions = false;
		this.classifier = classifier;
	}
	/**
	 * @param kind
	 * @param rule
	 */
	public NoteMelodicAnalysis(AtomPitch note, MelodicAnalyzer classifier, NoteMelodicAnalysisFeatures features, MelodicAnalysisNoteKinds kind, String rule) {
		this.note = note;
		this.features = features;

		this.classifier = classifier;
		this.kind = kind;
		this.rule = rule;
		this.confidence = Confidence.INDETERMINATE;
		this.violatesAPrioriConditions = false;
	}

	/**
	 * Usually loaded from a file
	 * @param ap
	 * @param ak
	 */
    public NoteMelodicAnalysis(AtomPitch ap, MelodicAnalysisNoteKinds ak, String comments) {
		this.note = ap;
		this.kind = ak;
		this.comment = comments;
    }

    public AtomPitch getNote() {
		return note;
	}

	/**
	 * @return the kind
	 */
	public final MelodicAnalysisNoteKinds getKind() {
		return kind;
	}
	/**
	 * @param kind the kind to set
	 */
	public final void setKind(MelodicAnalysisNoteKinds kind) {
		this.kind = kind;
	}
	/**
	 * @return the rule
	 */
	public final String getRule() {
		return rule;
	}
	/**
	 * @param rule the rule to set
	 */
	public final void setRule(String rule) {
		this.rule = rule;
	}
	/**
	 * @return the confidence
	 */
	public final Confidence getConfidence() {
		return confidence;
	}
	/**
	 * @param confidence the confidence to set
	 */
	public final void setConfidence(Confidence confidence) {
		this.confidence = confidence;
	}
	/**
	 * @return the comment
	 */
	public final String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public final void setComment(String comment) {
		this.comment = comment;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(kind.getAbbreviation());
		builder.append(rule);
		builder.append(confidence);
		return builder.toString();
	}
	public boolean isViolatesAPrioriConditions() {
		return violatesAPrioriConditions;
	}

    public MelodicAnalyzer getClassifier() {
	return classifier;
    }

    public void setClassifier(MelodicAnalyzer classifier) {
	this.classifier = classifier;
    }

	
    public String toValueString() {
	return kind.getAbbreviation() + "(" + confidence.getAbbr() + ")";
    }

	public NoteMelodicAnalysisFeatures getFeatures() {
		return features;
	}

	/**
	 * Computed using MelodicAnalysisAPrioriRules
	 * @param violatesAPrioriConditions
	 * deprecated See This method is now in violatesConditionsFor in MelodicAnalyzerWeka
	 */
	/*public void setViolatesAPrioriConditions(boolean violatesAPrioriConditions) {
		this.violatesAPrioriConditions = violatesAPrioriConditions;
	}*/
	
}
