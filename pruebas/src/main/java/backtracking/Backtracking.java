package backtracking;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IProgressObserver;
import es.ua.dlsi.im3.core.algorithms.CombinationGenerator;
import es.ua.dlsi.im3.core.algorithms.ICombinationGeneratorPrunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ProgressObserver implements IProgressObserver {

    @Override
    public void logText(String text) {

    }

    @Override
    public void setCurrentProgress(long workDone, long totalWork) {
        if (workDone % 1000 == 0) {
            System.out.println(workDone + " / " + totalWork);
        }
    }

    @Override
    public void onEnd() {

    }
}
public class Backtracking {
    public static final void main(String [] args) throws IM3Exception, IOException {
        List<String> linesA = Files.readAllLines(Paths.get("/Users/drizo/Desktop/backtracking/notasA.txt"));
        List<String> linesB = Files.readAllLines(Paths.get("/Users/drizo/Desktop/backtracking/notasB.txt"));

        double [] notasA = new double[linesA.size()];
        int i=0;
        for (String a: linesA) {
            String [] as = a.split("\t");
            notasA[i] = Double.parseDouble(as[1].replace(",", "."));
            i++;
        }

        double [] notasB = new double[linesB.size()];
        i=0;
        for (String b: linesB) {
            String [] bs = b.split("\t");
            notasB[i] = Double.parseDouble(bs[1].replace(",", ".")) / 10.0;
            i++;
        }


        int [] maxValues = new int[linesA.size()];
        for (i=0; i<maxValues.length; i++) {
            maxValues[i] = linesB.size();
        }

        CombinationGenerator combinationGenerator = new CombinationGenerator(new ProgressObserver());

        // cada posición de  combinacion[i] es una posición de las notas de B
        ArrayList<int[]> combinations = combinationGenerator.generateAllNonRepeatedCombinations(maxValues, new ICombinationGeneratorPrunner() {

            int [] aprobados = new int[notasA.length];
            @Override
            public boolean isValid(int[] currentSolution, int ielement) {
                double a = notasA[ielement];
                double b = notasB[currentSolution[ielement]];
                double nota;
                if (a < 2.5 || b < 2.5) {
                    nota = 0;
                } else {
                    nota = a*0.4 + b*0.6;
                }

                if (ielement > 1) {

                }
                System.out.println("a=" + a + ", b=" + b + ", nota=" + nota);
                return true;
            }
        });

        for (int [] combination: combinations) {
            System.out.println(Arrays.toString(combination));
        }
    }
}
