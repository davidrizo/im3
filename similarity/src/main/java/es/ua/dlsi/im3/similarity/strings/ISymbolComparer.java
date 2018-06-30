package es.ua.dlsi.im3.similarity.strings;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * Created by drizo on 18/7/17.
 */
public interface ISymbolComparer<Type> {
    double computeInsertCost(Type a);
    double computeDeleteCost(Type a);
    double computeSymbolDistance(Type a, Type b);

}
