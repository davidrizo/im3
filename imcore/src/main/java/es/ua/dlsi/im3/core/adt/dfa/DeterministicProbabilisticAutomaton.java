package es.ua.dlsi.im3.core.adt.dfa;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.math3.fraction.Fraction;

import java.util.*;

public class DeterministicProbabilisticAutomaton<StateType extends State, AlphabetSymbolType extends Comparable<AlphabetSymbolType>>  extends ProbabilisticAutomaton<StateType, AlphabetSymbolType> {
    public DeterministicProbabilisticAutomaton(Set<StateType> states, StateType startState, HashMap<StateType, Fraction> endProbabilities, Alphabet<AlphabetSymbolType> alphabet, Collection<Transition<StateType, AlphabetSymbolType>> transitions) throws IM3Exception {
        super(states, new HashMap<>(), endProbabilities, alphabet, transitions); // FIXME: 2/10/17 Start state
        startProbabilities.put(startState, Fraction.ONE);
        checkDeterminism();
    }

    private void checkDeterminism() {
        System.err.println("TO-DO Check determinism"); // TODO: 2/10/17 TO-DO Check determinism
    }

    public Fraction probabilityOf(List<AlphabetSymbolType> sequence) throws IM3Exception {
        Fraction best = Fraction.ZERO;

        for (Map.Entry<StateType, Fraction> entry: startProbabilities.entrySet()) {
            if (entry.getValue().getNumerator() != 0) {
                Fraction p = probabilityOf(sequence, entry.getKey()).multiply(entry.getValue());
                if (p.compareTo(best) > 0) {
                    best = p;
                }
            }
        }
        return best;
    }

    private Fraction probabilityOf(List<AlphabetSymbolType> sequence, StateType startState) throws IM3Exception {
        Fraction p = Fraction.ONE;
        StateType currentState = startState; // start probability = 1 // TODO: 3/10/17 Se puede iniciar de cualquier estado
        for (int i=0; i<sequence.size(); i++) {
            Set<Transition<StateType, AlphabetSymbolType>> transitions = delta(currentState, sequence.get(i));
            if (transitions.size() == 0) {
                return Fraction.ZERO; //TODO smoothing
            } else if (transitions.size() > 1) {
                throw new IM3Exception("This automaton is not deterministic, there are " + transitions.size() +
                        " from state " + currentState + " and token " + sequence.get(i));
            } else {
                Transition<StateType, AlphabetSymbolType> transition = transitions.iterator().next();
                Fraction transitionProb = transition.getProbability();
                if (transitionProb == null) {
                    p = Fraction.ZERO;
                } else {
                    System.out.println(p + " x " + transitionProb);
                    p = p.multiply(transitionProb);
                }
                currentState = transition.getTo();
            }
        }
        Fraction fraction = endProbabilities.get(currentState);
        if (fraction == null) {
            return Fraction.ZERO;
        } else {
            p = p.multiply(endProbabilities.get(currentState));
        }
        return p;
    }
}
