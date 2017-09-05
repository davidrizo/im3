package es.ua.dlsi.im3.similarity;

/**
 * Created by drizo on 19/7/17.
 */
public class Match<Type> {
   Type element;
   double value;

    public Match(Type element, double value) {
        this.element = element;
        this.value = value;
    }

    public Type getElement() {
        return element;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Match{" +
                "element=" + element +
                ", value=" + value +
                '}';
    }
}
