package es.easda.virema.musicintervals;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DissonanceTest {

    class FreqMagn implements Comparable<FreqMagn> {
        double frequency;
        double magnitude;

        public FreqMagn(double frequency, double magnitude) {
            this.frequency = frequency;
            this.magnitude = magnitude;
        }

        public double getFrequency() {
            return frequency;
        }

        public double getMagnitude() {
            return magnitude;
        }


        // ascending with freq, descending with magnitude
        @Override
        public int compareTo(FreqMagn o) {
            if (frequency < o.frequency) {
                return -1;
            } else if (frequency > o.frequency) {
                return 1;
            } else if (magnitude > o.magnitude) {
                return -1;
            } else if (magnitude < o.magnitude) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public String toString() {
            return "f=" + frequency + "\tm=" + magnitude;
        }
    }
    /**
     *
     * @param f0
     */
    private FreqMagn[] getFrequencies(double f0) {
        // basic ones (see https://dalemcgowan.com/every-note-is-a-chord/)
        double [] overtones = new double[] {0, 12, 12+7, 12+7+5, 12+7+5+4, 12+7+5+4+3};

        FreqMagn [] result = new FreqMagn[overtones.length];
        /*for (int i=0; i<=24; i++) {
            double relation = Math.pow(Math.pow(2, i), 1.0/12.0);
            System.out.println(i + "->" + relation);
        }*/

        System.out.println("Frequency f0: " + f0);
        double magnitude = 1; //TODO hecho a ojo - lo disminuyo 1/2 por cada armónico
        for (int i=0; i<overtones.length; i++) {
            double relation = Math.pow(Math.pow(2, overtones[i]), 1.0/12.0);
            double newFreq = f0 * relation;
            FreqMagn freqMagn = new FreqMagn(newFreq, magnitude);
            System.out.println("\t" + freqMagn.toString());
            result[i] = freqMagn;
            magnitude /= 2.0;
        }
        return result;
    }


    private FreqMagn[] merge(FreqMagn[] a, FreqMagn[] b) {
        FreqMagn[] result = new FreqMagn[a.length + b.length];
        int i=0;
        for (i=0; i<a.length; i++) {
            result[i] = a[i];
        }
        for (int j=0; j<b.length; j++) {
            result[i++] = b[j];
        }
        Arrays.sort(result);
        return result;
    }

    /**
     *
     * @param v
     * @return result[0] = freqs, result[1] = magnitudes
     */
    private double[][] freqsMagnitudes2Arrays(FreqMagn [] v) {
        double[][] resullt = new double[2][v.length];
        for (int i=0; i<v.length; i++) {
            resullt[0][i] = v[i].getFrequency();
            resullt[1][i] = v[i].getMagnitude();
        }
        return resullt;
    }

    @Test
    public void calcDissonance() throws Exception {
        FreqMagn [] freqsMagnitudesA4 = getFrequencies(261.63);
        FreqMagn [] freqsMagnitudesA5 = getFrequencies(466.16);
        FreqMagn [] allFreqs = merge(freqsMagnitudesA4, freqsMagnitudesA5);
        System.out.println("Merged:");
        for (int i=0; i<allFreqs.length; i++) {
            System.out.println("\t" + allFreqs[i].toString());
        }

        double [][] freqsMagnitudes = freqsMagnitudes2Arrays(allFreqs);
        Dissonance dissonance = new Dissonance(freqsMagnitudes[0], freqsMagnitudes[1]);
        System.out.println("Dissonance: " + dissonance.calcDissonance());

        //Dissonance dissonance = new Dissonance(new double[] {261.63, 349.23}, new double[] {1, 1});
        //System.out.println("Dissonance: " + dissonance.calcDissonance());
    }
}
