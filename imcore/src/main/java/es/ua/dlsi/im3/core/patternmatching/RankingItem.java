package es.ua.dlsi.im3.core.patternmatching;

import java.util.Objects;

/**
 * Ordered ascending
 * @param <InstanceType>
 */
public class RankingItem<InstanceType> implements Comparable<RankingItem> {
    InstanceType symbol;
    /**
     * Either a distance or a probability
     */
    double measure;

    public RankingItem(InstanceType symbol, double distance) {
        super();
        this.symbol = symbol;
        this.measure = distance;
    }

    public InstanceType getClassType() {
        return symbol;
    }

    public double getMeasure() {
        return measure;
    }

    @Override
    public int compareTo(RankingItem o) {
        if (measure < o.measure) {
            return -1;
        } else if (measure > o.measure) {
            return 1;
        } else {
            return symbol.hashCode() - o.hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        RankingItem ranking = (RankingItem) o;
        return Double.compare(ranking.measure, measure) == 0 &&
                Objects.equals(symbol, ranking.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, measure);
    }

    @Override
    public String toString() {
        return "RankingItem{" +
                "symbol=" + symbol +
                ", measure=" + measure +
                '}';
    }

    public void setMeasure(double measure) {
        this.measure = measure;
    }
}