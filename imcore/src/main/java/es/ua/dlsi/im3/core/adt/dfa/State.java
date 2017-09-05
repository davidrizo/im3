package es.ua.dlsi.im3.core.adt.dfa;

public class State implements Comparable<State> {
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
	public int compareTo(State o) {
		return number - o.number;
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

	
}
