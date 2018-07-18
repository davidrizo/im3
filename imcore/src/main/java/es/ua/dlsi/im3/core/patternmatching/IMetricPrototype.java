package es.ua.dlsi.im3.core.patternmatching;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * A prototype whose measure to other prototypes can be computed
 * @autor drizo
 */
public interface IMetricPrototype<ClassType> extends IPrototype<ClassType> {
    double computeDistance(IMetricPrototype<ClassType> to) throws IM3Exception;
}
