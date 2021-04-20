package es.ua.dlsi.im3.core.adt.dfa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.Fraction;

// TODO: 2/10/17 ¿Guardamos las transiciones de salida en cada símbolo? --> Llamarle transductor
/**
 * Probabilistic automaton. Use BigFraction for probabilities to avoid problems with underflows
 * @author drizo
 */
public abstract class ProbabilisticAutomaton<StateType extends State, AlphabetSymbolType extends IAlphabetSymbolType, TransductionType extends Transduction> {
	Set<StateType> states;
    Alphabet<AlphabetSymbolType> alphabet;
	HashMap<StateType, BigFraction> startProbabilities;
    HashMap<StateType, BigFraction> endProbabilities;
    HashSetValuedHashMap<DeltaInput<StateType, AlphabetSymbolType>, Transition<StateType, AlphabetSymbolType>> deltas;

    protected boolean debug;
    /**
     * If true, when a transition is not found for a given symbol, it is skipped, the error is reported but the traversal is not stopped
     */
    protected boolean skipUnknownSymbols = false;


	public ProbabilisticAutomaton(Set<StateType> states, HashMap<StateType, BigFraction> startProbabilities, HashMap<StateType, Fraction> endProbabilities,
                                  Alphabet<AlphabetSymbolType> alphabet, Collection<Transition<StateType, AlphabetSymbolType>> transitions) throws IM3Exception {
		super();
		this.states = states;
		this.startProbabilities = startProbabilities;
		this.endProbabilities = new HashMap<>();
		for (Map.Entry<StateType, Fraction> entry: endProbabilities.entrySet()) {
		    this.endProbabilities.put(entry.getKey(), new BigFraction(entry.getValue().getNumerator(), entry.getValue().getDenominator()));
        }
		this.alphabet = alphabet;
		this.deltas = new HashSetValuedHashMap();
		for (Transition<StateType, AlphabetSymbolType> transition: transitions) {
            DeltaInput<StateType, AlphabetSymbolType> input = new DeltaInput(transition.getFrom(), transition.getToken());
            if (!states.contains(transition.getFrom())) {
                throw new IM3Exception("The state " + transition.getFrom() + " does not belong to the state set");
            }
            if (!states.contains(transition.getTo())) {
                throw new IM3Exception("The state " + transition.getTo() + " does not belong to the state set");
            }
            if (!alphabet.contains(transition.getToken())) {
                throw new IM3Exception("The alphabet does not contain token " + transition.getToken());
            }
            this.deltas.put(input, transition);
        }
        checkProbabilities();
    }

    /**
     * @param from
     * @param alphabetSymbol
     * @return Probabilities
     * @throws IM3Exception
     */
    protected Set<Transition<StateType, AlphabetSymbolType>> delta(StateType from, AlphabetSymbolType alphabetSymbol) throws IM3Exception {
        DeltaInput input = new DeltaInput(from, alphabetSymbol);
        Set<Transition<StateType, AlphabetSymbolType>> result = deltas.get(input);
        if (result == null) {
            throw new IM3Exception("Cannot find this combination: " + input); // TODO: 2/10/17 Deberíamos hacer smoothing al principio
        }
        return result;
    }

    /**
     * Check probabilities are correct
     */
    private void checkProbabilities() {
        System.err.println("TO-DO Comprobar sumas de probabilidades = 1"); // TODO: 2/10/17 Comprobar sumas de probabilidades = 1
    }

    public boolean isSkipUnknownSymbols() {
        return skipUnknownSymbols;
    }

    public void setSkipUnknownSymbols(boolean skipUnknownSymbols) {
        this.skipUnknownSymbols = skipUnknownSymbols;
    }


    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    public void writeDot(File file) throws FileNotFoundException, IM3Exception {
        PrintStream os = new PrintStream(new FileOutputStream(file));
        os.println("digraph PA {");
        for (StateType state: states) {
            //os.println("s" + state.getNumber() + "[label=\"" + state.getName() + "\", shape=point];");
            os.println("s" + state.getNumber() + "[label=\"" + state.getName() + "\"];");
            printDotState(os, state);
        }

        os.println("start [shape=point];");
        for (StateType state: states) {
            BigFraction sp = startProbabilities.get(state);
            if (sp != null && sp.getNumeratorAsLong() != 0) {
                os.println("start -> s" + state.getNumber() + "[label = \"p=" + sp + "\"];");
            }
        }

        os.println("end [shape=doublecircle];");
        for (StateType state: states) {
            BigFraction sp = endProbabilities.get(state);
            if (sp != null && sp.getNumeratorAsLong() != 0) {
                os.println("s" + state.getNumber() + " -> end [label = \"p=" + sp + "\"];");
            }
        }

        os.println("}");
        if (os != null) {
            os.close();
        }
    }

    private void printDotState(PrintStream os, StateType state) throws IM3Exception {
        //String shape = "";
        //if (this.acceptStates.contains(state)) {
        //	shape = ",shape=doublecircle";
        //}

        for (AlphabetSymbolType symbol : alphabet.getSymbols()) {
            Set<Transition<StateType, AlphabetSymbolType>> transitions = delta(state, symbol);
            for (Transition transition: transitions) {
                if (transition.getProbability().getNumeratorAsLong() > 0) {
                    State to = transition.getTo();
                    os.println("s" + to.getNumber() + "[label=\"" + to.getName() + "\"];");
                    os.println("s" + state.getNumber() + "->s" + to.getNumber() + "[label=\"" + symbol.toString() + ", p=" + transition.getProbability() + "\"];");
                }
            }
        }

    }

    /**
     * If normalizes the probabilities to sum up 1 for all the output transitions of each state
     */
    public void normalizeProbabilities() throws IM3Exception {
        BigFraction sum = BigFraction.ZERO;
        for (State state: states) {
            BigFraction startProb = startProbabilities.get(state);
            if (startProb != null) {
                sum = sum.add(startProb);
            }
        }
        for (StateType state: states) {
            BigFraction startProb = startProbabilities.get(state);
            if (startProb != null) {
                startProbabilities.put(state, startProb.divide(sum));
            } else {
                startProbabilities.put(state, BigFraction.ZERO);
            }
        }

        for (StateType state: states) {
            sum = BigFraction.ZERO;
            ArrayList<Transition<StateType, AlphabetSymbolType>> stateOutputTransitions = new ArrayList<>();
            for (AlphabetSymbolType symbol : alphabet.getSymbols()) {
                Set<Transition<StateType, AlphabetSymbolType>> dtransitions = delta(state, symbol);
                if (dtransitions != null) {
                    for (Transition<StateType, AlphabetSymbolType> transition: dtransitions) {
                        sum = sum.add(transition.getProbability());
                        stateOutputTransitions.add(transition);
                    }
                }
            }
            for (Transition<StateType, AlphabetSymbolType> transition: stateOutputTransitions) {
                transition.setProbability(transition.getProbability().divide(sum));
            }
        }
    }

    /**
     *
     * @param sequence
     * @return
     * @throws IM3Exception
     */
    public TransductionType probabilityOf(List<? extends Token<AlphabetSymbolType>> sequence, ITransductionFactory<TransductionType> transductionFactory) throws IM3Exception {
        BigFraction best = null;
        int bestNumberOfAcceptedSymbols = -1; // first we order by the number of accepted symbols, then by probability

        TransductionType bestTransduction = null;

        for (Map.Entry<StateType, BigFraction> entry: startProbabilities.entrySet()) {
            if (debug) { // TODO: 5/10/17 Mejor niveles de log
                Logger.getLogger(this.getClass().getName()).info("----- New automaton traversal ----");
            }

            //ArrayList outputTokensFromThisState = new ArrayList();
            if (entry.getValue().getNumeratorAsLong() != 0) {
                Set<TransductionType> possibleTransductions = probabilityOf(sequence, entry.getKey(), transductionFactory);

                for (TransductionType transduction: possibleTransductions) {
                    BigFraction p = transduction.getProbability().multiply(entry.getValue());
                    transduction.setProbability(p);
                    if (transduction.getAcceptedTokensCount() > bestNumberOfAcceptedSymbols) {
                        bestNumberOfAcceptedSymbols = transduction.getAcceptedTokensCount();
                        bestTransduction = transduction;
                        best = p;
                    } else if (transduction.getAcceptedTokensCount() == bestNumberOfAcceptedSymbols) {
                        if (best == null || p.compareTo(best) > 0) {
                            best = p;
                            bestTransduction = transduction;
                        }
                    }
                }
            }
        }

        if (bestTransduction == null) {
            return transductionFactory.create(BigFraction.ZERO);
        } else {
            return bestTransduction;
        }
    }

    protected abstract Set<TransductionType> probabilityOf(List<? extends Token<AlphabetSymbolType>> sequence, StateType startState, ITransductionFactory<TransductionType> transductionFactory) throws IM3Exception;
}
