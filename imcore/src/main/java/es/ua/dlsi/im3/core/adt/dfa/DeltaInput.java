package es.ua.dlsi.im3.core.adt.dfa;

/**
 * A state with an input from the alphabet
 */
public class DeltaInput<StateType extends State, AlphabetSymbolType extends Comparable<AlphabetSymbolType>> {
    StateType from;
    AlphabetSymbolType input;

    public DeltaInput(StateType from, AlphabetSymbolType input) {
        this.from = from;
        this.input = input;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeltaInput<?, ?> that = (DeltaInput<?, ?>) o;

        if (!from.equals(that.from)) return false;
        return input.equals(that.input);
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + input.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DeltaInput{" +
                "from=" + from +
                ", input=" + input +
                '}';
    }
}