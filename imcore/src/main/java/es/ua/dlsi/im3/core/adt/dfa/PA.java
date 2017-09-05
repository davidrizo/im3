package es.ua.dlsi.im3.core.adt.dfa;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * Probabilistic automaton
 * @author drizo
 */
public abstract class PA<AlphabetType extends IAlphabet, StateType extends State> {
	Set<StateType> states;
	Set<AlphabetType> alphabet;
	HashMap<StateType, Double> startProbabilities;  
	Set<StateType> acceptStates;
	
	/**
	 * @param from
	 * @param alphabetSymbol
	 * @return Probabilities
	 * @throws IM3Exception
	 */
	protected abstract Map<StateType, Double> delta(StateType from, AlphabetType alphabetSymbol) throws IM3Exception;
	
	public PA(Set<StateType> states, Map<StateType, Double> startProbabilities, Set<StateType> acceptStates,
			Set<AlphabetType> alphabet) {
		super();
		this.states = states;
		this.startProbabilities = new HashMap<>(startProbabilities);
		this.acceptStates = acceptStates;
		this.alphabet = alphabet;
	}
	
	

}
