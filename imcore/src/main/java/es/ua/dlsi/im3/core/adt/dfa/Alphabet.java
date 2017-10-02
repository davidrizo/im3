package es.ua.dlsi.im3.core.adt.dfa;

import java.util.Set;

public class Alphabet<AlphabetSymbolType extends Comparable<AlphabetSymbolType>> {
    Set<AlphabetSymbolType> symbols;

    public Alphabet(Set<AlphabetSymbolType> symbols) {
        this.symbols = symbols;
    }

    public Set<AlphabetSymbolType> getSymbols() {
        return symbols;
    }
}
