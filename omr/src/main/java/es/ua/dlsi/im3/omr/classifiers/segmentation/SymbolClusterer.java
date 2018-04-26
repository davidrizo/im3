package es.ua.dlsi.im3.omr.classifiers.segmentation;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.model.entities.Region;
import es.ua.dlsi.im3.omr.model.entities.RegionType;
import es.ua.dlsi.im3.omr.model.entities.Symbol;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.MultiKMeansPlusPlusClusterer;

import java.util.*;

//TODO Pages - ahora sólo hace una página
/**
 * It creates pages and regions from the list of symbols
 * @autor drizo
 */
public class SymbolClusterer implements ISymbolClusterer {
    class ClusterableSymbol implements Clusterable {
        Symbol symbol;

        public ClusterableSymbol(Symbol symbol) {
            this.symbol = symbol;
        }

        @Override
        public double[] getPoint() {
            // just use the y position
            double y = (symbol.getBoundingBox().getFromY() + symbol.getBoundingBox().getToY()) / 2.0;
            double [] point = {0, y};
            return point;
        }

        public Symbol getSymbol() {
            return symbol;
        }
    }


    @Override
    public SortedSet<Region> cluster(List<Symbol> symbolList, int expectedStaves) throws IM3Exception {
        List<ClusterableSymbol> centroids = new LinkedList<>();
        for (Symbol symbol: symbolList) {
            centroids.add(new ClusterableSymbol(symbol));
        }

        //TODO Trials, iterations....
        MultiKMeansPlusPlusClusterer<ClusterableSymbol> transformer = new MultiKMeansPlusPlusClusterer<>(new KMeansPlusPlusClusterer<>(expectedStaves, 10), 5);

        List<CentroidCluster<ClusterableSymbol>> clusters = transformer.cluster(centroids);

        SortedSet<Region> result = new TreeSet<>();
        for (CentroidCluster<ClusterableSymbol> cluster: clusters) {
            List<Symbol> clusterSymbols = new LinkedList<>();
            List<ClusterableSymbol> points = cluster.getPoints();
            for (ClusterableSymbol clusterableSymbol: points) {
                clusterSymbols.add(clusterableSymbol.getSymbol());
            }
            Region region = new Region(RegionType.staff, clusterSymbols);
            result.add(region);
        }

        return result;
    }
}
