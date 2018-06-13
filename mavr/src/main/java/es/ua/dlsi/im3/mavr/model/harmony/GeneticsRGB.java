package es.ua.dlsi.im3.mavr.model.harmony;

import io.jenetics.*;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.util.IntRange;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.engine.Limits.bySteadyFitness;

/**
 * @autor drizo. From https://github.com/jenetics/jenetics/issues/330
 */
public class GeneticsRGB {
    // Strawman for your tuple data structure.
    static class RGB {
        int r, g, b;
        RGB(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        @Override
        public String toString() {
            return "[" + r + ", " + g + ", " + b + "]";
        }
    }

    static double fitness(final RGB[] values) {
        double sum=0;
        for (int i=0; i<values.length; i++) {
            //System.out.println("R=" + values[i].r + ", G=" + values[i].g + ", B=" + values[i].b);
            Color color = Color.hsb(values[i].r, values[i].g/100.0, values[i].b/100.0);
            sum += color.getSaturation();
        }
        return sum;
    }

    // The valid integer range of your tuple elements.
    static final IntRange range = IntRange.of(0, 255);

    // The length range of your IntTuple3[] array.
    static int colorsToFind = 10;
    static final IntRange length = IntRange.of(colorsToFind);

    // I am using two different int ranges for the three int values of the tuple
    static final Codec<RGB[], IntegerGene> codecT3 = Codec.of(
            Genotype.of(
                    IntegerChromosome.of(IntRange.of(0, 355), length),
                    IntegerChromosome.of(IntRange.of(0, 100), length),
                    IntegerChromosome.of(IntRange.of(0, 100), length)
            ),
            gt -> {
                final int[] r = gt.get(0).as(IntegerChromosome.class).toArray();
                final int[] g = gt.get(1).as(IntegerChromosome.class).toArray();
                final int[] b = gt.get(2).as(IntegerChromosome.class).toArray();

                final int size = Math.min(Math.min(r.length, g.length), b.length);
                return IntStream.range(0, size)
                        .mapToObj(i -> new RGB(r[i], g[i], b[i]))
                        .toArray(RGB[]::new);
            }
    );
    public static void main(String[] args) {

        final ExecutorService executor = Executors.newFixedThreadPool (32);

        Engine<IntegerGene, Double> engine = Engine
                // Create an Engine.Builder with the "pure" fitness function
                // and the appropriate Codec.
                .builder(GeneticsRGB::fitness, codecT3)
                .optimize(Optimize.MAXIMUM)
                .populationSize(5000)
                //.selector(new RouletteWheelSelector<>())
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
                    public void accept(EvolutionResult<IntegerGene, Double> integerGeneDoubleEvolutionResult) {
                        if (integerGeneDoubleEvolutionResult.getGeneration() % 100 == 0) {
                            System.out.println("Generation " + integerGeneDoubleEvolutionResult.getGeneration() + ", fitness = "+ integerGeneDoubleEvolutionResult.getBestFitness());
                        }
                    }
                })
                .collect(toBestPhenotype());

        System.out.println(statistics);

        // get colors
        System.out.println("Colors, fitness: " + best.getFitness());
        for (int i=0; i<colorsToFind; i++) {
            System.out.println("["
                    + best.getGenotype().get(0, i)
                    + ", "
                    + best.getGenotype().get(1, i)
                    + ", "
                    + best.getGenotype().get(2, i)
                    + "]");
        }
    }
}
