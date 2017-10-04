package es.ua.dlsi.im3.core.adt.dfa;

import org.apache.commons.math3.fraction.BigFraction;

public class Transduction {
    BigFraction probability;

    public Transduction() {
        this.probability = BigFraction.ZERO;
    }

    public BigFraction getProbability() {
        return probability;
    }

    public void setProbability(BigFraction probability) {
        this.probability = probability;
    }
}
