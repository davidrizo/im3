package es.ua.dlsi.im3.languagemodel.sequences;

import es.ua.dlsi.im3.languagemodel.ISequenceRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by drizo on 18/7/17.
 */
public class Sequence<Type> implements ISequenceRepresentation<Type> {
    private final String name;
    ArrayList<Type> sequence;

    public Sequence(String name, Type [] seq) {
        this.name = name;
        this.sequence = new ArrayList<>();
        for (Type s: seq) {
            this.sequence.add(s);
        }
    }

    public Sequence(String name, List<Type> seq) {
        this.name = name;
        this.sequence = new ArrayList<>();
        this.sequence.addAll(seq);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int size() {
        return sequence.size();
    }

    @Override
    public List<Type> getItems() {
        return sequence;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append('[');
        for (Type item : sequence) {
            if (!first) {
                sb.append(',');
            } else {
                first = false;
            }
            sb.append(item);
        }
        sb.append(']');
        return sb.toString();
    }


    public Type[] getItemsAsArray() {
        return (Type[]) sequence.toArray();
    }
}
