package es.ua.dlsi.im3.omr.classifiers.segmentation;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.model.entities.Page;
import es.ua.dlsi.im3.omr.model.entities.Region;
import es.ua.dlsi.im3.omr.model.entities.Symbol;

import java.util.List;
import java.util.SortedSet;

/**
 * It clusters the symbols into regions inside it
 * @autor drizo
 */
public interface ISymbolClusterer {
    SortedSet<Region> cluster(List<Symbol> symbolList, int expectedRegions) throws IM3Exception;
}
