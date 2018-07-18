package es.ua.dlsi.im3.core.patternmatching;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * @autor drizo
 */
public class NearestNeighbourClassesRanking<PrototypeClassType, InstanceType extends IPrototype<PrototypeClassType>> {
    LinkedList<RankingItem<PrototypeClassType>> rankingItemLinkedList;

    public NearestNeighbourClassesRanking(LinkedList<RankingItem<PrototypeClassType>> rankingItemLinkedList) {
        this.rankingItemLinkedList = rankingItemLinkedList;

    }
    public NearestNeighbourClassesRanking(TreeSet<RankingItem<InstanceType>> rankingItems, boolean normalizeUsingPseudoProbabilities) {
        rankingItemLinkedList = new LinkedList<>();
        TreeSet<String> usedClasses = new TreeSet<>();
        double sum = 0;
        for (RankingItem<InstanceType> rankingItem: rankingItems) {
            String className = rankingItem.getClassType().getPrototypeClass().toString();
            if (!usedClasses.contains(className)) {
                usedClasses.add(className);
                double measure;
                if (normalizeUsingPseudoProbabilities) {
                    if (rankingItem.getMeasure() == 0) {
                        measure = Double.MAX_VALUE;
                    } else {
                        measure = 1.0 / rankingItem.getMeasure();
                    }
                } else {
                    measure = rankingItem.getMeasure();
                }

                RankingItem<PrototypeClassType> item = new RankingItem<PrototypeClassType>(rankingItem.getClassType().getPrototypeClass(), measure);
                if (normalizeUsingPseudoProbabilities) {
                    rankingItemLinkedList.add(0, item); // descending probability
                } else {
                    rankingItemLinkedList.add(item); // ascending distance
                }

                sum += measure;
            }
        }

        if (normalizeUsingPseudoProbabilities) {
            for (RankingItem<PrototypeClassType> rankingItem: rankingItemLinkedList) {
                rankingItem.setMeasure(rankingItem.getMeasure() / sum);
            }
        }
    }

    public List<RankingItem<PrototypeClassType>> getRankingItems() {
        return rankingItemLinkedList;
    }

    public PrototypeClassType first() {
        return rankingItemLinkedList.get(0).getClassType();
    }

    public int size() {
        return rankingItemLinkedList.size();
    }
}
