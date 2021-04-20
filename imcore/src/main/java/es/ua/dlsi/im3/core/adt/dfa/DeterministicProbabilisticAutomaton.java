package es.ua.dlsi.im3.core.adt.dfa;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.Fraction;

import java.util.*;
import java.util.logging.Logger;

// TODO: 3/10/17 Llamarle transductor. Quizás esto debería ir en ProbabilisticAutomaton
public class DeterministicProbabilisticAutomaton<StateType extends State, AlphabetSymbolType extends IAlphabetSymbolType, TransductionType extends Transduction>  extends ProbabilisticAutomaton<StateType, AlphabetSymbolType, TransductionType> {
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

    // TODO: 4/10/17 Output list - tokens
    /**
     * @param sequence
     * @param startState
     * @param transductionFactory
     * @return As it is deterministic, it will return just one
     * @throws IM3Exception
     */
    protected TransductionType deterministicProbabilityOf(List<? extends Token<AlphabetSymbolType>> sequence, StateType startState, ITransductionFactory<TransductionType> transductionFactory) throws IM3Exception {
        BigFraction p = BigFraction.ONE;
        TransductionType transduction = transductionFactory.create(BigFraction.ONE);
        StateType currentState = startState;

        if (debug) { // TODO: 5/10/17 Mejor niveles de log
            Logger.getLogger(this.getClass().getName()).info("Trying with start state " + startState);
        }

        currentState.onEnter(sequence.get(0), null, transduction);
        if (transduction.getProbability().getNumeratorAsLong() == 0) {
            if (debug) {
                Logger.getLogger(this.getClass().getName()).info("Start state " + startState + " with probability 0");
            }
            transduction.setErrorMessage("Cannot find start state with probability > 0");
            return transduction; // don't need to go on
        }

        for (int i=0; i<sequence.size(); i++) {
            Set<Transition<StateType, AlphabetSymbolType>> transitions = delta(currentState, sequence.get(i).getSymbol());
            if (transitions.size() == 0) {
                if (debug) {
                    Logger.getLogger(this.getClass().getName()).info("No transition from " + currentState + " with token " + sequence.get(i).getSymbol());
                }
                transduction.addErrorMessage(
                        "No transition from state " +
                                currentState + " with input " +
                                sequence.get(i).getSymbol().getType() + " at position #" + i);
                transduction.setProbability(BigFraction.ZERO); //TODO smoothing
                if (!skipUnknownSymbols) {
                    return transduction;
                } else {
                    transduction.incrementAcceptedTokens();
                }
            } else if (transitions.size() > 1) {
                transduction.setErrorMessage("Non deterministic automaton: there are " + transitions.size() + " from state " + currentState + " with input " + sequence.get(i).getSymbol().getType() + " at position #" + i);

                throw new IM3Exception("This automaton is not deterministic, there are " + transitions.size() +
                        " transitions from state " + currentState + " with token " + sequence.get(i));
            } else {
                Transition<StateType, AlphabetSymbolType> transition = transitions.iterator().next();
                BigFraction transitionProb = transition.getProbability();
                if (debug) {
                    Logger.getLogger(this.getClass().getName()).info("Transition from " + currentState + " with token " + sequence.get(i).getSymbol() + " to " + transition.getTo()
                    + " with probability " + transitionProb);
                }

                if (transitionProb == null) {
                    transduction.addErrorMessage("Transition from state " + currentState + " with input " + sequence.get(i).getSymbol().getType() + " is null" + " at position #" + i);
                    transduction.setProbability(BigFraction.ZERO);
                    if (!skipUnknownSymbols) {
                        return transduction; // don't need to go on
                    } else {
                        transduction.incrementAcceptedTokens();
                    }
                } else {
                    p = p.multiply(transitionProb);
                    transduction.setProbability(p);
                }
                currentState.onExit(transition.getTo(), !currentState.equals(transition.getTo()), transduction);
                if (debug) {
                    Logger.getLogger(this.getClass().getName()).info("Exiting state " + currentState + " with transduction probability "+ transduction.getProbability());
                }
                if (transduction.getProbability().getNumeratorAsLong() == 0) {
                    transduction.addErrorMessage("Transition from state " + currentState + " with input " + sequence.get(i).getSymbol().getType() + " is 0 after exiting state" + " at position #" + i);

                    if (!skipUnknownSymbols) {
                        return transduction; // don't need to go on
                    } else {
                        transduction.incrementAcceptedTokens();
                    }
                }
                transition.getTo().onEnter(sequence.get(i), currentState, transduction);
                Logger.getLogger(this.getClass().getName()).info("Entering state " + currentState + " with transduction probability "+ transduction.getProbability());
                if (transduction.getProbability().getNumeratorAsLong() == 0) {
                    transduction.setErrorMessage("Transition from state " + currentState + " with input " + sequence.get(i).getSymbol().getType()  + " at position #" + i + " is 0 after entering again to new state " + currentState);
                    return transduction; // don't need to go on
                }
                currentState = transition.getTo();
            }
            transduction.incrementAcceptedTokens();
        }

        currentState.onExit(null, true, transduction);
        Logger.getLogger(this.getClass().getName()).info("Exiting state " + currentState + " with transduction probability "+ transduction.getProbability());
        if (transduction.getProbability().getNumeratorAsLong() == 0) {
            transduction.setErrorMessage("Transduction probability is 0 when exiting state after the sequence is fully consumed");
            return transduction; // don't need to go on
        }

        BigFraction fraction = endProbabilities.get(currentState);
        Logger.getLogger(this.getClass().getName()).info("End (accept final) probability " + currentState + " with probability "+ fraction);
        if (fraction == null) {
            p = BigFraction.ZERO;
        } else {
            p = p.multiply(endProbabilities.get(currentState));
        }
        transduction.setProbability(p);
        return transduction;
    }

    @Override
    protected Set<TransductionType> probabilityOf(List<? extends Token<AlphabetSymbolType>> sequence, StateType startState, ITransductionFactory<TransductionType> transductionFactory) throws IM3Exception {
        HashSet<TransductionType> result = new HashSet<>();
        result.add(deterministicProbabilityOf(sequence, startState, transductionFactory));
        return result;

    }
}
