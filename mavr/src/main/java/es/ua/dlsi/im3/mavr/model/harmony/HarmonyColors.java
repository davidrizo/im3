package es.ua.dlsi.im3.mavr.model.harmony;

import edu.stanford.vis.color.LAB;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.graph.DirectedGraph;
import es.ua.dlsi.im3.core.adt.graph.GraphEdge;
import es.ua.dlsi.im3.core.adt.graph.GraphNode;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import es.ua.dlsi.im3.core.utils.Sonority;
import es.ua.dlsi.im3.core.utils.SonoritySegmenter;
import io.jenetics.*;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.util.IntRange;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import org.harmony_analyser.jharmonyanalyser.chord_analyser.TonalPitchSpace;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.engine.Limits.bySteadyFitness;

/**
 * Try to give a different color to each chord such a way the distance between each pair of chord is the same as the one of
 * the distance of each pair of colors representing them.
 * A genetic algorithm is used to fit these variables
 * @autor drizo
 */
public class HarmonyColors {
    private final ScoreSong scoreSong;
    private List<Segment> sonoritySegments;
    private List<Sonority> sonorities;
    /**
     * Must be accessible from fitness that has to be static
     */
    static private DirectedGraph<NodeChordLabel, EdgeChordDistanceLabel> chordGraph;
    /**
     * Does not repeat nodes
     */
    ArrayList<GraphNode<NodeChordLabel, EdgeChordDistanceLabel>> chordNodes;
    /**
     * The sequence in the song
     */
    ArrayList<NodeChordLabel> sonorityNodes;

    /**
     * Between the 1st element (0 has not a previous element), thus sonorityDistances.size() == sonorityNodes.size()-1
     */
    ArrayList<Double> sonorityDistances;

    private int ncolorsToBeAssigned;

    public HarmonyColors(ScoreSong scoreSong) throws IM3Exception {
        this.scoreSong = scoreSong;
        sonorities = new SonoritySegmenter().buildSonorities(scoreSong);
        createColorsGraph();
    }

    /**
     *
     * @param currentColors Colors are changed as the algorithm runs
     * @throws IM3Exception
     */
    public void computeColors(ArrayList<ObjectProperty<Color>> currentColors, DoubleProperty currentFitness, IntegerProperty currentGeneration, ArrayList<DoubleProperty> colorDistances, ArrayList<DoubleProperty> chordDistances) {
        fitBestColors(currentColors, currentFitness, currentGeneration, colorDistances, chordDistances);
    }

    private void createColorsGraph() throws IM3Exception {
        chordGraph = new DirectedGraph<>();
        GraphNode<NodeChordLabel, EdgeChordDistanceLabel> lastGraphNode = null;
        int nodeIndex = 0;
        chordNodes = new ArrayList<>();
        sonorityNodes = new ArrayList<>();
        sonorityDistances = new ArrayList<>();
        for (Sonority sonority: sonorities) {
            Harm harm = scoreSong.getHarmActiveAtTimeOrNull(sonority.getSegment().getFrom());
            Key key;
            if (harm == null ||harm.getKey() == null) {
                key = scoreSong.getUniqueKeyActiveAtTime(sonority.getSegment().getFrom());
            } else {
                key = harm.getKey();
            }

            String nodeID = key.toString() + "_" + sonority.getScientificPitches().toString(); // the set of pitches
            GraphNode<NodeChordLabel, EdgeChordDistanceLabel> graphNode = chordGraph.getNodeOrNull(nodeID);
            if (graphNode == null) {
                graphNode = new GraphNode<>(chordGraph, nodeID, new NodeChordLabel(nodeIndex, key, sonority));
                chordGraph.addNode(graphNode);
                chordNodes.add(graphNode);
                nodeIndex++;
            }

            if (lastGraphNode != null) {
                double chordDistance = computeChordDistance(lastGraphNode.getLabel(), graphNode.getLabel());
                EdgeChordDistanceLabel edgeChordDistanceLabel = new EdgeChordDistanceLabel(chordDistance);
                lastGraphNode.addEdge(new GraphEdge<>(lastGraphNode, graphNode, edgeChordDistanceLabel));
                sonorityDistances.add(chordDistance);
            }
            sonorityNodes.add(graphNode.getLabel());
            lastGraphNode = graphNode;
        }
    }

    private double computeChordDistance(NodeChordLabel from, NodeChordLabel to) {
        return TonalPitchSpace.getTPSDistance(from.getChord(), from.getRoot(), from.getKey(), to.getChord(), to.getRoot(), to.getKey(), false);
    }

    public DirectedGraph<NodeChordLabel, EdgeChordDistanceLabel> getChordGraph() {
        return chordGraph;
    }

    public ArrayList<NodeChordLabel> getSonorityNodes() {
        return sonorityNodes;
    }

    private void fitBestColors(ArrayList<ObjectProperty<Color>> currentColors, DoubleProperty currentFitness, IntegerProperty currentGeneration,ArrayList<DoubleProperty> colorDistances, ArrayList<DoubleProperty> chordDistances) {
        ncolorsToBeAssigned = chordGraph.getNodes().size();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Creating # " + ncolorsToBeAssigned + " initial colors");

        IntRange length = IntRange.of(ncolorsToBeAssigned);
        Codec<IntegerHSB[], IntegerGene> codecHSB = Codec.of(
                Genotype.of(
                        IntegerChromosome.of(IntRange.of(0, 355), length),
                        IntegerChromosome.of(IntRange.of(0, 100), length),
                        IntegerChromosome.of(IntRange.of(0, 100), length)
                ),
                gt -> {
                    final int[] h = gt.get(0).as(IntegerChromosome.class).toArray();
                    final int[] s = gt.get(1).as(IntegerChromosome.class).toArray();
                    final int[] b = gt.get(2).as(IntegerChromosome.class).toArray();

                    final int size = Math.min(Math.min(h.length, s.length), b.length);
                    return IntStream.range(0, size)
                            .mapToObj(i -> new IntegerHSB(h[i], s[i], b[i]))
                            .toArray(IntegerHSB[]::new);
                }
        );

        final ExecutorService executor = Executors.newFixedThreadPool (Runtime.getRuntime().availableProcessors());

        Engine<IntegerGene, Double> engine = Engine
                // Create an Engine.Builder with the "pure" fitness function
                // and the appropriate Codec.
                .builder(HarmonyColors::fitness, codecHSB)
                .optimize(Optimize.MINIMUM)
                .populationSize(5000)
                .selector(new RouletteWheelSelector<>())
                .alterers(
                        new Mutator<>(0.55),
                        new SinglePointCrossover<>(0.06))
                .executor (executor)
                .build();

        // Create evolution statistics consumer.

        Consumer<? super EvolutionResult<IntegerGene, Double>> statistics
                = EvolutionStatistics.ofNumber();

        final Phenotype<IntegerGene, Double> best = engine.stream()
                .limit(bySteadyFitness(20))
                //.limit(100000)
                .peek(statistics)
                .peek(new Consumer<EvolutionResult<IntegerGene, Double>>() {
                    @Override
                    public void accept(EvolutionResult<IntegerGene, Double> evolutionResult) {
                        if (currentFitness != null && currentGeneration != null && currentColors != null) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    currentFitness.setValue(evolutionResult.getBestFitness());
                                    currentGeneration.setValue(evolutionResult.getGeneration());
                                    fillColors(currentColors, evolutionResult.getBestPhenotype().getGenotype(), colorDistances, chordDistances);
                                }
                            });
                        }
                        if (evolutionResult.getGeneration() % 10 == 0) {
                            System.out.println("Generation " + evolutionResult.getGeneration() + ", fitness = "+ evolutionResult.getBestFitness());
                        }
                    }
                })
                .collect(toBestPhenotype());

        System.out.println(statistics);

        // get colors
        System.out.println("Colors, fitness: " + best.getFitness());
        for (int i=0; i<ncolorsToBeAssigned; i++) {
            System.out.println("["
                    + best.getGenotype().get(0, i)
                    + ", "
                    + best.getGenotype().get(1, i)
                    + ", "
                    + best.getGenotype().get(2, i)
                    + "]");
        }
    }

    private void fillColors(ArrayList<ObjectProperty<Color>> currentColors, Genotype<IntegerGene> genotype, ArrayList<DoubleProperty> colorDistances, ArrayList<DoubleProperty> chordDistances) {
        Color [] colors = new Color[ncolorsToBeAssigned];
        LAB [] labs = new LAB[ncolorsToBeAssigned];
        for (int i=0; i<ncolorsToBeAssigned; i++) {
            IntegerHSB hsb = new IntegerHSB(genotype.get(0, i).intValue(), genotype.get(1, i).intValue(), genotype.get(2, i).intValue());
            labs[i] = hsb.toLAB();
            Color color = Color.hsb(hsb.getHue(), hsb.getSaturation()/100.0, hsb.getBright()/100.0);
            colors[i] = color;
        }

        if (currentColors.size() != sonorityNodes.size()) {
            throw new IM3RuntimeException("Current colors size (" + currentColors.size() + ") != sonority nodes size (" + sonorityNodes + ")");
        }
        LAB previousColor = null;
        for (int i=0; i<sonorityNodes.size(); i++) {
            NodeChordLabel node = sonorityNodes.get(i);
            int nodeIndex = node.getIndex();
            currentColors.get(i).setValue(colors[nodeIndex]);
            LAB labColor = labs[nodeIndex];

            if (i>0) {
                double chordDistance = sonorityDistances.get(i-1);
                chordDistances.get(i-1).setValue(chordDistance);

                double colorDistance = Math.abs(computeColorDistance(previousColor, labColor));
                colorDistances.get(i-1).setValue(colorDistance);
            }

            previousColor = labColor;
        }
    }

    static double computeColorDistance(LAB from, LAB to) {
        return LAB.ciede2000(from, to);
    }


    /**
     * The sum of the distance differences to be minimized
     * @param chromosomes
     * @return
     */
    static double fitness(IntegerHSB[] chromosomes) {
        LAB[] labColors = new LAB[chromosomes.length];
        for (int i=0; i<chromosomes.length; i++) {
            labColors[i] = chromosomes[i].toLAB();
        }

        ArrayList<Double> chordDistances = new ArrayList<>();
        ArrayList<Double> colorDistances = new ArrayList<>();

        // traverse the graph
        for (GraphNode<NodeChordLabel, EdgeChordDistanceLabel> fromNode: chordGraph.getNodes()) {
            if (fromNode != chordGraph.getStartNode()) {
                LAB fromColor = labColors[fromNode.getLabel().getIndex()];
                if (fromNode.getOutEdges() != null) {
                    for (GraphEdge<EdgeChordDistanceLabel> edge : fromNode.getOutEdges()) {
                        GraphNode<?, EdgeChordDistanceLabel> toNode = edge.getTargetNode();
                        LAB toColor = labColors[((NodeChordLabel) toNode.getLabel()).getIndex()];

                        //add 1 to avoid divisions by 0
                        double chordDistance = 0.001+Math.abs(edge.getLabel().getDistance()); // use abs just in case
                        chordDistances.add(chordDistance);

                        double colorDistance = 0.001+Math.abs(computeColorDistance(fromColor, toColor));
                        colorDistances.add(colorDistance);
                    }
                }
            }
        }

        if (chordDistances.size() != colorDistances.size()) {
            throw new IM3RuntimeException("Chord distances (" + chordDistances.size() + ") != of color distances (" + colorDistances.size() + ")");
        }

        // now compute the relative distances
        double sumDistance = 0;
        for (int i=1; i<chordDistances.size(); i++) {
            double relativeColorDistance = colorDistances.get(i) / colorDistances.get(i-1);
            double relativeChordDistance = chordDistances.get(i) / chordDistances.get(i-1);

            sumDistance += Math.abs(relativeColorDistance-relativeChordDistance);
        }

        return sumDistance;
    }
}
