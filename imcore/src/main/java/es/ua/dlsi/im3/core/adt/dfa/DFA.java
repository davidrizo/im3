package es.ua.dlsi.im3.core.adt.dfa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * Deterministic finite automaton
 * @author drizo
 */
public abstract class DFA<AlphabetType extends Comparable<AlphabetType>, StateType extends State> {
	Set<StateType> states;
	Set<AlphabetType> alphabet;
	StateType startState;
	Set<StateType> acceptStates;
	StateType currentState;
	
	protected abstract StateType delta(StateType from, AlphabetType alphabetSymbol) throws IM3Exception;

	public DFA(Set<StateType> states, StateType startState, Set<StateType> acceptStates, Set<AlphabetType> alphabet) {
		super();
		this.states = states;
		this.startState = startState;
		this.acceptStates = acceptStates;
		this.alphabet = alphabet;
		reset();
	}
	
	public final void reset() {
		currentState = startState;
	}
	/**
	 * True if the current state is a final state
	 * @return
	 */
	public boolean isAccept() {
		return acceptStates.contains(currentState);
	}
	/**
	 * 
	 * @param inputSymbol
	 * @throws IM3Exception
	 */
	public void input(AlphabetType inputSymbol) throws IM3Exception {
		currentState = delta(currentState, inputSymbol);
	}
	/**
	 * True if it ends in a final state. This method resets the current state 
	 * @param inputSequence
	 * @return
	 * @throws IM3Exception 
	 */
	public boolean evaluate(List<AlphabetType> inputSequence) throws IM3Exception {
		reset();
		for (AlphabetType inputSymbol : inputSequence) {
			input(inputSymbol);
		}
		return isAccept();
	}

	public void writeDot(File file) throws FileNotFoundException, IM3Exception {
		PrintStream os = new PrintStream(new FileOutputStream(file));
		os.println("digraph dfa {");
		os.println("s" + currentState.getNumber() + "[label=\"" + currentState.getName() + "\", shape=point];");
		printDotState(os, currentState);
		
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
		for (AlphabetType symbol : alphabet) {
			StateType to = delta(state, symbol);
			os.println("s" + to.getNumber() + "[label=\"" + to.getName() + "\"];");
			os.println("s" + state.getNumber() + "->s" + to.getNumber() + "[label=\"" + symbol.toString() + "\"];");
			printDotState(os, to);
		}
		
	}
	
	
}
