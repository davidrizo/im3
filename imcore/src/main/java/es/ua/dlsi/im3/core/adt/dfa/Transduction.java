package es.ua.dlsi.im3.core.adt.dfa;

import org.apache.commons.math3.fraction.BigFraction;

public class Transduction {
    BigFraction probability;
    /**
     * Number of accepted tokens so far
     */
    int acceptedTokensCount;

    /**
     * When an error occurs, description of the offending symbol
     */
    String errorMessage;

    public Transduction(BigFraction initialProbability) {
        this.probability = initialProbability;
        this.acceptedTokensCount = 0;
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

    public int getAcceptedTokensCount() {
        return acceptedTokensCount;
    }

    public void incrementAcceptedTokens() {
        this.acceptedTokensCount++;
    }

    public void setAcceptedTokensCount(int acceptedTokensCount) {
        this.acceptedTokensCount = acceptedTokensCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void addErrorMessage(String s) {
        if (errorMessage != null) {
            this.errorMessage += ("\n" + s);
        } else {
            this.errorMessage = s;
        }
    }
}
