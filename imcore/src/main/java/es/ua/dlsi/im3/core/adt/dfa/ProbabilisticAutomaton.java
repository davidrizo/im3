package es.ua.dlsi.im3.core.adt.dfa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.math3.fraction.Fraction;

/**
 * Probabilistic automaton. Use Fraction for probabilities to avoid problems with underflows
 * @author drizo
 */
public class ProbabilisticAutomaton<StateType extends State, AlphabetSymbolType extends Comparable<AlphabetSymbolType>> {
	Set<StateType> states;
    Alphabet<AlphabetSymbolType> alphabet;
	HashMap<StateType, Fraction> startProbabilities;
	Set<StateType> acceptStates;
    HashSetValuedHashMap<DeltaInput, Transition> deltas;

	/**
	 * @param from
	 * @param alphabetSymbol
	 * @return Probabilities
	 * @throws IM3Exception
	 */
	protected Set<Transition> delta(StateType from, AlphabetSymbolType alphabetSymbol) throws IM3Exception {
        DeltaInput input = new DeltaInput(from, alphabetSymbol);
        Set<Transition> result = deltas.get(input);
        if (result == null) {
            throw new IM3Exception("Cannot find this combination: " + input); // TODO: 2/10/17 Deberíamos hacer smoothing al principio
        }
        return result;
    }
	
	public ProbabilisticAutomaton(Set<StateType> states, HashMap<StateType, Fraction> startProbabilities, Set<StateType> acceptStates,
                                  Alphabet<AlphabetSymbolType> alphabet, Set<Transition<StateType, AlphabetSymbolType>> transitions) {
		super();
		this.states = states;
		this.startProbabilities = startProbabilities;
		this.acceptStates = acceptStates;
		this.alphabet = alphabet;
		this.deltas = new HashSetValuedHashMap();
		for (Transition<StateType, AlphabetSymbolType> transition: transitions) {
            DeltaInput<StateType, AlphabetSymbolType> input = new DeltaInput(transition.getFrom(), transition.getToken());
            this.deltas.put(input, transition);
        }
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
            os.println("start -> s" + state.getNumber() + "[label = \"p=" + startProbabilities.get(state) + "\"];");
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
            Set<Transition> transitions = delta(state, symbol);
            for (Transition transition: transitions) {
                State to = transition.getTo();
                os.println("s" + to.getNumber() + "[label=\"" + to.getName() + "\"];");
                os.println("s" + state.getNumber() + "->s" + to.getNumber() + "[label=\"" + symbol.toString() + ", p=" + transition.getProbability() + "\"];");
            }
        }

    }

}
