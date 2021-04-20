package es.ua.dlsi.im3.omr.encoding;

import java.util.LinkedList;
import java.util.List;

/**
 * @author drizo
 * @param <SymbolType>
 */
public class Sequence<SymbolType> implements Cloneable {
    protected List<SymbolType> symbols;

    public Sequence() {
        symbols = new LinkedList<>();
    }

    public void add(SymbolType symbol) {
        symbols.add(symbol);
    }

    public void remove(SymbolType symbol) {
        symbols.remove(symbol);
    }

    public int size() {
        return symbols.size();
    }

    public List<SymbolType> getSymbols() {
        return symbols;
    }

    @Override
    protected Sequence<SymbolType> clone() throws CloneNotSupportedException {
        Sequence<SymbolType> result = new Sequence<>();
        symbols.forEach(s -> result.add(s));
        return result;
    }
}
