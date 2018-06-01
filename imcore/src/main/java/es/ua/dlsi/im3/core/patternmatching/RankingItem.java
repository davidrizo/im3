package es.ua.dlsi.im3.core.patternmatching;

import java.util.Objects;

public class RankingItem<InstanceType> implements Comparable<RankingItem> {
    InstanceType symbol;
    double distance;

    public RankingItem(InstanceType symbol, double distance) {
        super();
        this.symbol = symbol;
        this.distance = distance;
    }

    public InstanceType getClassType() {
        return symbol;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(RankingItem o) {
        if (distance < o.distance) {
            return -1;
        } else if (distance > o.distance) {
            return 1;
        } else {
            return symbol.hashCode() - o.hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        RankingItem ranking = (RankingItem) o;
        return Double.compare(ranking.distance, distance) == 0 &&
                Objects.equals(symbol, ranking.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, distance);
    }

    @Override
    public String toString() {
        return "RankingItem{" +
                "symbol=" + symbol +
                ", distance=" + distance +
                '}';
    }
}