package es.ua.dlsi.im3.core.adt.dfa;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.Fraction;

import java.util.*;

// TODO: 3/10/17 Llamarle transductor. Quizás esto debería ir en ProbabilisticAutomaton
public class DeterministicProbabilisticAutomaton<StateType extends State, AlphabetSymbolType extends Comparable<AlphabetSymbolType>>  extends ProbabilisticAutomaton<StateType, AlphabetSymbolType> {



    public DeterministicProbabilisticAutomaton(Set<StateType> states, StateType startState, HashMap<StateType, Fraction> endProbabilities, Alphabet<AlphabetSymbolType> alphabet, Collection<Transition<StateType, AlphabetSymbolType>> transitions) throws IM3Exception {
        super(states, new HashMap<>(), endProbabilities, alphabet, transitions); // FIXME: 2/10/17 Start state
        startProbabilities.put(startState, BigFraction.ONE);
        checkDeterminism();
    }

    /**
     *
     * @throws IM3Exception When not detereministic
     */
    private void checkDeterminism() throws IM3Exception {
        for (StateType state: states) {
            for (AlphabetSymbolType alphabetSymbol: alphabet.getSymbols()) {
                Set<Transition<StateType, AlphabetSymbolType>> transitions = delta(state, alphabetSymbol);
                if (transitions.size() > 1) {
                    throw new IM3Exception("This automaton is not deterministic, there are " + transitions.size() +
                            " transitions from state " + state + " with token " + alphabetSymbol);
                }
            }
        }
    }

    /**
     *
     * @param sequence
     * @return
     * @throws IM3Exception
     */
    public Transduction probabilityOf(List<? extends Token<AlphabetSymbolType>> sequence) throws IM3Exception {
        BigFraction best = BigFraction.ZERO;

        Transduction bestTransduction = null;

        for (Map.Entry<StateType, BigFraction> entry: startProbabilities.entrySet()) {
            ArrayList outputTokensFromThisState = new ArrayList();
            if (entry.getValue().getNumeratorAsLong() != 0) {
                Transduction transduction = probabilityOf(sequence, entry.getKey());

                BigFraction p = transduction.getProbability().multiply(entry.getValue());
                transduction.setProbability(p);
                if (best == null || p.compareTo(best) > 0) {
                    best = p;
                    bestTransduction = transduction;
                }
            }
        }

        if (bestTransduction == null) {
            return new Transduction(BigFraction.ZERO, null);
        } else {
            return bestTransduction;
        }
    }

    // TODO: 4/10/17 Output list - tokens
    private Transduction probabilityOf(List<? extends Token<AlphabetSymbolType>> sequence, StateType startState) throws IM3Exception {
        /// Use logarithms to avoid underflows
        List outputTokens = new ArrayList();
        BigFraction p = BigFraction.ONE;
        StateType currentState = startState; // start probability = 1 // TODO: 3/10/17 Se puede iniciar de cualquier estado
        currentState.onEnter(sequence.get(0), outputTokens);
        for (int i=0; i<sequence.size(); i++) {
            Set<Transition<StateType, AlphabetSymbolType>> transitions = delta(currentState, sequence.get(i).getSymbol());
            if (transitions.size() == 0) {
                return new Transduction(BigFraction.ZERO, outputTokens); //TODO smoothing
            } else if (transitions.size() > 1) {
                throw new IM3Exception("This automaton is not deterministic, there are " + transitions.size() +
                        " transitions from state " + currentState + " with token " + sequence.get(i));
            } else {
                Transition<StateType, AlphabetSymbolType> transition = transitions.iterator().next();
                BigFraction transitionProb = transition.getProbability();
                if (transitionProb == null) {
                    p = BigFraction.ZERO;
                } else {
                    p = p.multiply(transitionProb);
                }
                currentState.onExit();
                currentState = transition.getTo();
                currentState.onEnter(sequence.get(i), outputTokens);
            }
        }
        currentState.onExit();
        BigFraction fraction = endProbabilities.get(currentState);
        if (fraction == null) {
            p = BigFraction.ZERO;
        } else {
            p = p.multiply(endProbabilities.get(currentState));
        }

        return new Transduction(p, outputTokens);
    }
}
