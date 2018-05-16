package es.ua.dlsi.im3.core.adt.dfa;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Alphabet<AlphabetSymbolType extends IAlphabetSymbolType> {
    /**
     * Use the symbol type rather than the hashCode of the alphabet for allowing other constructions
     */
    protected HashMap<String, AlphabetSymbolType> symbols;

    public Alphabet(Set<AlphabetSymbolType> symbols) {
        this.symbols = new HashMap<>();
        for (AlphabetSymbolType alphabetSymbolType: symbols) {
            this.symbols.put(alphabetSymbolType.getType(), alphabetSymbolType);
        }
    }

    public Alphabet() {
        this.symbols = new HashMap<>();
    }
    public Collection<AlphabetSymbolType> getSymbols() {
        return symbols.values();
    }

    public void add(AlphabetSymbolType token) {
        this.symbols.put(token.getType(), token);
    }

    public boolean contains(AlphabetSymbolType token) {
        return symbols.containsKey(token.getType());
    }

}
