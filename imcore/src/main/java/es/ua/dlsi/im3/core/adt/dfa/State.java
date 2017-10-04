package es.ua.dlsi.im3.core.adt.dfa;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.List;

public class State<AlphabetSymbolType extends Comparable<AlphabetSymbolType>, InputTokenType extends Token<AlphabetSymbolType>, TransductionType extends Transduction> implements Comparable<State<AlphabetSymbolType, InputTokenType, TransductionType>> {
	protected int number;
	private String name;
	
	public State(int number, String name) {
		super();
		this.number = number;
		this.setName(name);
	}

	public int getNumber() {
		return number;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + number;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
        return number == other.number;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @Override
    public String toString() {
        return name;
    }

    /**
     * Launched when entering the state. The transduction probability is allowed to be changed by the method
     * No exception can be launched. If a exception happens, the probability must be set to 0.
     * If a implementation exception must be thrown, use IM3RuntimeException
     */
    public void onEnter(InputTokenType token, State previousState, TransductionType transduction)  {
	    // np-op
    }

    /**
     * Launched when exiting the state. The transduction probability is allowed to be changed by the method.
     * No exception can be launched. If a exception happens, the probability must be set to 0
     * If a implementation exception must be thrown, use IM3RuntimeException
     */
    public void onExit(State nextState, boolean isStateChange, TransductionType transduction)  {
        // np-op
    }

    @Override
    public int compareTo(State<AlphabetSymbolType, InputTokenType, TransductionType> o) {
        return number - o.number;
    }
}
