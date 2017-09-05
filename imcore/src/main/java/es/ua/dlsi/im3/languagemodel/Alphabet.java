package es.ua.dlsi.im3.languagemodel;

import es.ua.dlsi.im3.core.IM3RuntimeException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Alphabet<AphabetElementType> {
    private final AphabetElementType emptySymbol;
    HashMap<AphabetElementType, Integer> elements;

    public Alphabet(Collection<AphabetElementType> elements, AphabetElementType emptySymbol) {
        this.elements = new HashMap<>();
        int i=0;
        for (AphabetElementType e: elements) {
            this.elements.put(e, i);
            i++;
        }
        this.emptySymbol = emptySymbol;
    }

    public Alphabet(AphabetElementType [] elements, AphabetElementType emptySymbol) {
        this.elements = new HashMap<>();
        int i=0;
        for (AphabetElementType a: elements) {
            this.elements.put(a, i);
            i++;
        }
        this.emptySymbol = emptySymbol;
    }

    public int getSize() {
        return elements.size();
    }

    public Set<AphabetElementType> getElements() {
        return elements.keySet();
    }

    public AphabetElementType getEmptySymbol() {
        return emptySymbol;
    }

    /**
     * It returns a position, from 0, of the element type in the alphabet. If the alphabet where ABC...Z, the element
     * A would return a 0, B a 1...
     * @param element
     * @return
     */
    public int getOrder(AphabetElementType element) {
        Integer result = elements.get(element);
        if (result == null) {
            throw new IM3RuntimeException("The element '" + element + "' is not found in the alphabet");
        }
        return result;
    }
}
