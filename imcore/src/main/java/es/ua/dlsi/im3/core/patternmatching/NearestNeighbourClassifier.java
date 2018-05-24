package es.ua.dlsi.im3.core.patternmatching;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.*;

/**
 * Classical simple nearest neigbour classifier
 * @param <InstanceType> The type of the instance objects
 * @param <PrototypeClassType> The type of the class type
 */
public abstract class NearestNeighbourClassifier<PrototypeClassType, InstanceType extends IMetricPrototype<PrototypeClassType>> {
    private List<InstanceType> trainedSet;

    public NearestNeighbourClassifier() {
        trainedSet = null;
    }

    protected abstract void train() throws IM3Exception;

    protected void addPrototype(InstanceType prototype) {
        if (trainedSet == null) {
            trainedSet = new LinkedList<>();
        }
        trainedSet.add(prototype);

    }

    class Ranking implements Comparable<Ranking>{
        PrototypeClassType symbol;
        double distance;

        public Ranking(PrototypeClassType symbol, double distance) {
            super();
            this.symbol = symbol;
            this.distance = distance;
        }

        public PrototypeClassType getClassType() {
            return symbol;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public int compareTo(Ranking o) {
            if (distance < o.distance) {
                return -1;
            } else if (distance > o.distance) {
                return 1;
            } else {
                return symbol.hashCode() - o.hashCode();
            }
        }
    }

    /**
     * @param elementToClassify
     * @return An ordered list of elements ordered by descreasing similarity
     */
    public TreeSet<Ranking> rank(InstanceType elementToClassify) throws IM3Exception {
        if (trainedSet == null) {
            throw new IM3Exception("The classifier has not been trained yet");
        }
        TreeMap<PrototypeClassType, Double> symbolDistances = new TreeMap<>();

        for(InstanceType symbol : trainedSet) {
            double distance = elementToClassify.computeDistance(symbol);

            Double prevDist = symbolDistances.get(symbol.getPrototypeClass());
            if (prevDist == null || distance < prevDist) {
                symbolDistances.put(symbol.getPrototypeClass(), distance); // to avoid repeating symbols
            }

            symbolDistances.put(symbol.getPrototypeClass(), distance);
        }
        TreeSet<Ranking> result = new TreeSet<>();
        for (Map.Entry<PrototypeClassType, Double> symbolEntry: symbolDistances.entrySet()) {
            result.add(new Ranking(symbolEntry.getKey(), symbolEntry.getValue()));
        }

        return result;
    }

    public List<PrototypeClassType> classify(InstanceType instanceClassType) throws IM3Exception {
        TreeSet<Ranking> ranking = rank(instanceClassType);

        List<PrototypeClassType> result = new ArrayList<>();
        for (Ranking r: ranking) {
            result.add(r.getClassType());
        }
        return result;
    }

}
