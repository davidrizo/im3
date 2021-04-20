package es.ua.dlsi.im3.core.adt.dfa;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.Fraction;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NonDeterministicProbabilisticAutomaton<StateType extends State, AlphabetSymbolType extends IAlphabetSymbolType, TransductionType extends Transduction>  extends ProbabilisticAutomaton<StateType, AlphabetSymbolType, TransductionType> {
    private boolean debug;
    /**
     * If true, when a transition is not found for a given symbol, it is skipped, the error is reported but the traversal is not stopped
     */
    private boolean skipUnknownSymbols = false;

    public NonDeterministicProbabilisticAutomaton(Set<StateType> states, StateType startState, HashMap<StateType, Fraction> endProbabilities, Alphabet<AlphabetSymbolType> alphabet, Collection<Transition<StateType, AlphabetSymbolType>> transitions) throws IM3Exception {
        super(states, new HashMap<>(), endProbabilities, alphabet, transitions); // FIXME: 2/10/17 Start state
        startProbabilities.put(startState, BigFraction.ONE);
    }

    public boolean isSkipUnknownSymbols() {
        return skipUnknownSymbols;
    }

    public void setSkipUnknownSymbols(boolean skipUnknownSymbols) {
        this.skipUnknownSymbols = skipUnknownSymbols;
    }

    /**
     * Get the probability from the given index
     * @param sequence
     * @param currentIndex
     * @param currentState
     * @param currentTransduction
     * @param transductionFactory
     * @return
     */
    protected Set<TransductionType> probabilityFromItem(List<? extends Token<AlphabetSymbolType>> sequence, int currentIndex, StateType currentState, TransductionType currentTransduction, ITransductionFactory<TransductionType> transductionFactory) {
        HashSet<TransductionType> result = new HashSet<>();
        if (currentIndex >= sequence.size()) {
            result.add(currentTransduction);
        } else {
            Token<AlphabetSymbolType> token = sequence.get(currentIndex);
            Set<Transition<StateType, AlphabetSymbolType>> transitions = null;
            try {
                transitions = delta(currentState, token.getSymbol());
                if (transitions.isEmpty()) {
                    if (skipUnknownSymbols) {
                        //TODO probability? - the number of accepted symbols is smaller
                        result.addAll(probabilityFromItem(sequence, currentIndex + 1, currentState, currentTransduction, transductionFactory));
                    }
                } else {
                    for (Transition<StateType, AlphabetSymbolType> transition : transitions) {
                        BigFraction transitionProb = transition.getProbability();
                        if (debug) {
                            //TODO Poder dibujar el log como un árbol
                            Logger.getLogger(this.getClass().getName()).info("Transition from " + currentState + " with symbol " + token.getSymbol() + " to " + transition.getTo()
                                    + " with probability " + transitionProb);
                        }

                        if (transitionProb != null) {
                            TransductionType transduction = (TransductionType) currentTransduction.clone();
                            transduction.setProbability(transduction.getProbability().multiply(transitionProb));
                            try {
                                if (transduction.getProbability().getNumeratorAsLong() == 0) {
                                    transduction.addErrorMessage("Transition from state " + currentState + " with input " + token.getSymbol().getType() + " is 0 after exiting state" + " at position #" + currentIndex);
                                } else {
                                    currentState.onExit(transition.getTo(), !currentState.equals(transition.getTo()), transduction);
                                    transition.getTo().onEnter(token, currentState, transduction);
                                    Logger.getLogger(this.getClass().getName()).info("Entering state " + currentState + " with transduction probability " + transduction.getProbability());
                                    if (transduction.getProbability().getNumeratorAsLong() == 0) {
                                        transduction.setErrorMessage("Transition from state " + currentState + " with input " + token.getSymbol().getType() + " at position #" + currentIndex + " is 0 after entering again to new state " + currentState);
                                    } else {
                                        transduction.incrementAcceptedTokens();
                                        result.addAll(probabilityFromItem(sequence, currentIndex + 1, transition.getTo(), transduction, transductionFactory));
                                    }
                                }
                            } catch (Throwable e) {
                                Logger.getLogger(this.getClass().getName()).info("Error with transition " + transition + ": " + e.getMessage());
                            }
                        }
                    }
                }
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).info("Cannot compute delta with " + currentState + " with symbol " + token.getSymbol());
                if (skipUnknownSymbols) {
                    //TODO probability? - the number of accepted symbols is smaller
                    result.addAll(probabilityFromItem(sequence, currentIndex + 1, currentState, currentTransduction, transductionFactory));
                }
            }
        }
        return result;
    }

    // TODO: 4/10/17 Output list - tokens
    @Override
    protected Set<TransductionType> probabilityOf(List<? extends Token<AlphabetSymbolType>> sequence, StateType startState, ITransductionFactory<TransductionType> transductionFactory) throws IM3Exception {
        BigFraction p = BigFraction.ONE;
        TransductionType initialTransduction = transductionFactory.create(BigFraction.ONE);

        StateType currentState = startState;
        if (debug) { // TODO: 5/10/17 Mejor niveles de log
            Logger.getLogger(this.getClass().getName()).info("Trying with start state " + startState);
        }

        currentState.onEnter(sequence.get(0), null, initialTransduction);
        if (initialTransduction.getProbability().getNumeratorAsLong() == 0) {
            if (debug) {
                Logger.getLogger(this.getClass().getName()).info("Start state " + startState + " with probability 0");
            }
            initialTransduction.setErrorMessage("Cannot find start state with probability > 0");
            HashSet<TransductionType> result = new HashSet<>();
            result.add(initialTransduction);
            return result; // don't need to go on
        } else {
            return probabilityFromItem(sequence, 0, startState, initialTransduction, transductionFactory);
        }
    }
}
