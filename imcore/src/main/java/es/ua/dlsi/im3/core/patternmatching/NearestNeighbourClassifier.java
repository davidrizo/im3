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



    /**
     * @param elementToClassify
     * @return An ordered list of elements ordered by descreasing similarity
     */
    public TreeSet<RankingItem<InstanceType>> classify(InstanceType elementToClassify) throws IM3Exception {
        if (trainedSet == null) {
            throw new IM3Exception("The classifier has not been trained yet");
        }

        TreeSet<RankingItem<InstanceType>> result = new TreeSet<>();
        for(InstanceType symbol : trainedSet) {
            double distance = elementToClassify.computeDistance(symbol);
            result.add(new RankingItem(symbol, distance));
        }

        return result;
    }

    public List<InstanceType> getTrainedSet() {
        return trainedSet;
    }
}
