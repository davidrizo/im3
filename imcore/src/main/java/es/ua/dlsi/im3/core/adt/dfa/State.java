package es.ua.dlsi.im3.core.adt.dfa;

import es.ua.dlsi.im3.core.IM3Exception;

public class State<AlphabetSymbolType extends Comparable<AlphabetSymbolType>, TokenType extends Token<AlphabetSymbolType>> implements Comparable<State<AlphabetSymbolType, TokenType>> {
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
     * Launched when entering the state
     */
    public void onEnter(TokenType token) throws IM3Exception {
	    // np-op
    }

    /**
     * Launched when exiting the state
     */
    public void onExit() {
        // np-op
    }

    @Override
    public int compareTo(State<AlphabetSymbolType, TokenType> o) {
        return number - o.number;
    }
}
