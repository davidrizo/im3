package es.ua.dlsi.im3.core.adt.dfa;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.math3.fraction.Fraction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeterministicProbabilisticAutomaton<StateType extends State, AlphabetSymbolType extends Comparable<AlphabetSymbolType>>  extends ProbabilisticAutomaton<StateType, AlphabetSymbolType> {
    StateType startState;
    
    public DeterministicProbabilisticAutomaton(Set<StateType> states, StateType startState, Set<StateType> acceptStates, Alphabet<AlphabetSymbolType> alphabet, Set<Transition<StateType, AlphabetSymbolType>> transitions) {
        super(states, new HashMap<>(), acceptStates, alphabet, transitions); // FIXME: 2/10/17 Start state
        startProbabilities.put(startState, Fraction.ONE);
        this.startState = startState;
        checkDeterminism();
    }

    private void checkDeterminism() {
        System.err.println("TO-DO Check determinism"); // TODO: 2/10/17 TO-DO Check determinism
    }

    public Fraction probabilityOf(List<AlphabetSymbolType> sequence) throws IM3Exception {
        Fraction p = Fraction.ONE;
        StateType currentState = startState;
        for (int i=0; i<sequence.size(); i++) {
            Set<Transition> transitions = delta(currentState, sequence.get(i));
            if (transitions.size() == 0) {
                return Fraction.ZERO; //TODO smoothing
            } else if (transitions.size() > 1) {
                throw new IM3Exception("This automaton is not deterministic, there are " + transitions.size() +
                        " from state " + currentState + " and token " + sequence.get(i));
            } else {
                Transition<StateType, AlphabetSymbolType> transition = transitions.iterator().next();
                Fraction transitionProb = transition.getProbability();
                p = p.multiply(transitionProb);
                currentState = transition.getTo();
            }
        }
        return p;
    }
}
