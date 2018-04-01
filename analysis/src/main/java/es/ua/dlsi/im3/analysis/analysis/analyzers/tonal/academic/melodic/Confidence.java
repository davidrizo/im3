package es.ua.dlsi.im3.analysis.analysis.analyzers.tonal.academic.melodic;

/**
 * @author drizo
 * @date 26/11/2011
 *
 */
public enum Confidence {

    LOW("lo", 0.25),
    MEDIUM("me", 0.5),
    HIGH("hi", 0.9),
    SURE("su", 1.0),
    INDETERMINATE("??", 0.1);

    String abbr;
    double confidenceValue;

    Confidence(String a, double conf) {
	this.abbr = a;
	this.confidenceValue = conf;
    }

    public String getAbbr() {
	return abbr;
    }

    public double getConfidenceValue() {
	return confidenceValue;
    }

}
