package es.ua.dlsi.im3.core.adt.dfa;

import java.util.HashSet;
import java.util.Set;

public class Alphabet<AlphabetSymbolType extends Comparable<AlphabetSymbolType>> {
    protected Set<AlphabetSymbolType> symbols;

    public Alphabet(Set<AlphabetSymbolType> symbols) {
        this.symbols = symbols;
    }

    public Alphabet() {
        this.symbols = new HashSet<>();
    }
    public Set<AlphabetSymbolType> getSymbols() {
        return symbols;
    }

    public boolean contains(AlphabetSymbolType token) {
        return symbols.contains(token);
    }
}
