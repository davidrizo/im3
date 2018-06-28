package es.ua.dlsi.im3.core.patternmatching;

public interface IEditDistanceOperations<ItemType> {
    double insertCost(ItemType item);
    double deleteCost(ItemType item);
    double substitutionCost(ItemType a, ItemType b);
}
