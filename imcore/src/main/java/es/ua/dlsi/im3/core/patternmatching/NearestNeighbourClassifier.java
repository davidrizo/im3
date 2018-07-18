package es.ua.dlsi.im3.core.patternmatching;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.*;

/**
 * Classical simple nearest neigbour classifier
 * @param <InstanceType> The type of the instance objects
 * @param <PrototypeClassType> The type of the class type
 */
public class NearestNeighbourClassifier<PrototypeClassType, InstanceType extends IMetricPrototype<PrototypeClassType>> {
    private List<InstanceType> trainingSet;

    public NearestNeighbourClassifier() {
        trainingSet = null;
    }

    public void addPrototype(InstanceType prototype) {
        if (trainingSet == null) {
            trainingSet = new LinkedList<>();
        }
        trainingSet.add(prototype);
    }

    /**
     * Return a ranking of instances
     * @param elementToClassify
     * @return An ordered list of items from the training set ordered by descreasing similarity
     */
    public TreeSet<RankingItem<InstanceType>> classifyInstances(InstanceType elementToClassify) throws IM3Exception {
        if (trainingSet == null) {
            throw new IM3Exception("The classifier has not a training set yet");
        }

        TreeSet<RankingItem<InstanceType>> result = new TreeSet<>();
        for(InstanceType symbol : trainingSet) {
            double distance = elementToClassify.computeDistance(symbol);
            result.add(new RankingItem(symbol, distance));
        }

        return result;
    }

    /**
     * Return a ranking of classes
     * @param instance
     * @return
     */
    public NearestNeighbourClassesRanking<PrototypeClassType, InstanceType> classify(InstanceType instance, boolean normalizeUsingPseudoProbabilities) throws IM3Exception {
        TreeSet<RankingItem<InstanceType>> sortedInstances = classifyInstances(instance);
        NearestNeighbourClassesRanking<PrototypeClassType, InstanceType> result = new NearestNeighbourClassesRanking<>(sortedInstances, normalizeUsingPseudoProbabilities);
        return result;
    }

    public List<InstanceType> getTrainingSet() {
        return trainingSet;
    }

}
