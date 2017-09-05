package es.ua.dlsi.im3.similarity.strings;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * Created by drizo on 18/7/17.
 */
public interface ISymbolComparer<Type> {
    double computeInsertCost(Type a) throws IM3Exception;
    double computeDeleteCost(Type a) throws IM3Exception;
    double computeSymbolDistance(Type a, Type b) throws IM3Exception;

}
