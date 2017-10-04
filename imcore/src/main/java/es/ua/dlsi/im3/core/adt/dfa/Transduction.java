package es.ua.dlsi.im3.core.adt.dfa;

import org.apache.commons.math3.fraction.BigFraction;

import java.util.List;

public class Transduction {
    BigFraction probability;
    List tokens;

    public Transduction(BigFraction probability) {
        this.probability = probability;
        tokens = null;
    }

    public Transduction(BigFraction probability, List tokens) {
        this.probability = probability;
        this.tokens = tokens;
    }

    public BigFraction getProbability() {
        return probability;
    }

    public List getTokens() {
        return tokens;
    }

    public void setProbability(BigFraction probability) {
        this.probability = probability;
    }
}
