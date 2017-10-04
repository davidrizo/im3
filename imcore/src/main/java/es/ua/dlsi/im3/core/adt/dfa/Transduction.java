package es.ua.dlsi.im3.core.adt.dfa;

import org.apache.commons.math3.fraction.BigFraction;

public class Transduction {
    BigFraction probability;

    public Transduction(BigFraction initialProbability) {
        this.probability = initialProbability;
    }

    public BigFraction getProbability() {
        return probability;
    }

    public void setProbability(BigFraction probability) {
        this.probability = probability;
    }

    public void setZeroProbability() {
        probability = BigFraction.ZERO;
    }
}
