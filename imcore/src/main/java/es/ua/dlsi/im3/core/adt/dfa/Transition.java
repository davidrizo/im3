package es.ua.dlsi.im3.core.adt.dfa;

import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.Fraction;

/**
 * @param <StateType>
 * @param <AlphabetSymbolType>
 */
public class Transition<StateType extends State, AlphabetSymbolType extends IAlphabetSymbolType> {
    private StateType from;
    private AlphabetSymbolType token;
    private StateType to;
    /**
     * Used to learn the probabilities if required
     */
    private int countVisits;

    /**
     * Avoid problems with overflows
     */
    private BigFraction probability;

    public Transition(StateType from, AlphabetSymbolType token, StateType to, Fraction probability) {
        this.from = from;
        this.token = token;
        this.to = to;
        this.probability = new BigFraction(probability.getNumerator(), probability.getDenominator());
        countVisits = 0;
    }

    public Transition(StateType from, AlphabetSymbolType token, StateType to) {
        this(from, token, to, Fraction.ONE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transition<?, ?> that = (Transition<?, ?>) o;

        if (!from.equals(that.from)) return false;
        if (!token.getType().equals(that.token.getType())) return false;
        return to.equals(that.to);
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + token.getType().hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }

    public void visit() {
        countVisits++;
    }

    public void reset() {
        countVisits = 0;
    }

    public int getCountVisits() {
        return countVisits;
    }

    public StateType getFrom() {
        return from;
    }

    public AlphabetSymbolType getToken() {
        return token;
    }

    public StateType getTo() {
        return to;
    }

    public BigFraction getProbability() {
        return probability;
    }

    public void setProbability(BigFraction probability) {
        this.probability = probability;
    }
}
