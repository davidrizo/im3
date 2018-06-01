package es.ua.dlsi.im3.core.patternmatching;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.Objects;

/**
 * @autor drizo
 */
public class DoublePrototype implements IMetricPrototype<String> {
    private final String prototypeClass;
    double value;

    public DoublePrototype(String prototypeClass, double value) {
        this.value = value;
        this.prototypeClass = prototypeClass;
    }

    @Override
    public double computeDistance(IMetricPrototype<String> to) throws IM3Exception {
        return Math.abs(value - ((DoublePrototype)to).value);
    }

    @Override
    public String getPrototypeClass() {
        return prototypeClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoublePrototype)) return false;
        DoublePrototype that = (DoublePrototype) o;
        return Double.compare(that.value, value) == 0 &&
                Objects.equals(prototypeClass, that.prototypeClass);
    }

    @Override
    public int hashCode() {

        return Objects.hash(prototypeClass, value);
    }
}
