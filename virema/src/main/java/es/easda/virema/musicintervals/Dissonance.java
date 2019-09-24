package es.easda.virema.musicintervals;

import java.util.List;

import static javazoom.jl.decoder.Header.frequencies;

/**
 * Ported from the code in https://github.com/MTG/essentia/blob/master/src/algorithms/tonal/dissonance.cpp
 * [1] R. Plomp and W. J. M. Levelt, \"Tonal Consonance and Critical Bandwidth,\" The Journal of the Acoustical Society of America, vol. 38"
 * "  no. 4, pp. 548–560, 1965"
 */
public class Dissonance {
    private final double[] magnitudes;
    private final double[] frequencies;

    public Dissonance(double[] frequencies, double[] magnitudes) throws Exception {
        this.magnitudes = magnitudes.clone();
        this.frequencies = frequencies.clone();
        if (magnitudes.length != frequencies.length) {
            throw new Exception("Dissonance: frequency and magnitude input vectors are not the same size");
        }

        for (int i=1; i<frequencies.length; i++) {
            if (frequencies[i] < frequencies[i-1]) {
                throw new Exception("Dissonance: spectral peaks must be sorted by frequency");
            }
        }
    }

    public double calcDissonance() {
        double [] loudness = magnitudes.clone();
        double totalLoudness = 0;
        int size = frequencies.length;

        // calculate dissonance
        for (int i = 0; i < size; i++) {
            // dBA-weighting
            // The factor should be applied to the amplitudes,
            // but we receive already the intensities (squared amplitudes),
            // thus, the factor is applied twice
            double aWeightingFactor = aWeighting(frequencies[i]);
            loudness[i] *= aWeightingFactor * aWeightingFactor;
            totalLoudness += loudness[i];
        }


        if (totalLoudness == 0.0) {
            return 0.0;
        }

        //vector<double> loudness(size);
        //for (int i=0; i<size; i++) partialLoudness = loudness[i]/totalLoudness;

        double totalDissonance = 0;
        for (int p1 = 0; p1 < size; p1++) {
            if (frequencies[p1] > 50) { // ignore frequencies below 50 Hz
                double barkFreq = hz2bark(frequencies[p1]);
                double startF = bark2hz(barkFreq - 1.18);
                double endF = bark2hz(barkFreq + 1.18);
                int p2 = 0;
                double peakDissonance = 0;
                while (p2 < size && frequencies[p2] < startF && frequencies[p2] < 50) p2++;
                while (p2 < size && frequencies[p2] < endF && frequencies[p2] < 10000) {
                    double d = 1.0 - consonance(frequencies[p1], frequencies[p2]);
                    // Dissonance from p1 to p2, should be the same as dissonance from p2
                    // to p1, this is the reason for using both peaks' loudness as
                    // weight
                    if (d > 0) peakDissonance += d*(loudness[p2] + loudness[p1])/totalLoudness;
                    p2++;
                }
                double partialLoudness = loudness[p1]/totalLoudness;
                if (peakDissonance > partialLoudness) peakDissonance = partialLoudness;
                totalDissonance += peakDissonance;
            }
        }
        // total dissonance is divided by two, because each peak from a pair
        // contributes to it
        return totalDissonance/2;
    }

    double aWeighting(double f) {
        // from http://www.cross-spectrum.com/audio/weighting.html
        // 1.25893 = 2 dB
        return 1.25893*12200*12200*(f*f*f*f) / (
                (f*f +20.6*20.6) *
                        (f*f +12200*12200) *
                        Math.sqrt(f*f +107.7*107.7) *
                        Math.sqrt(f*f +737.9*737.9)
        );
    }

    double consonance(double f1, double f2) {
        // critical bandwidth between f1, f2:
        // see  http://www.sfu.ca/sonic-studio/handbook/Critical_Band.html for a
        // definition of critical bandwidth between two partials of a complex tone:
        double cbwf1 = barkCriticalBandwidth(hz2bark(f1));
        double cbwf2 = barkCriticalBandwidth(hz2bark(f2));
        double cbw = Math.min(cbwf1, cbwf2 );
        return plompLevelt(Math.abs(f2-f1)/cbw);
    }

    double plompLevelt(double df) {
        // df is the frequency difference on with critical bandwidth as  a unit.
        // the cooeficients were fitted with a polynom
        // to the data from the plomp & Levelt 1965 publication
        // To verify the fit run this and plot with e.g. gnuplot
        //
        //   #include <iostream>
        //   int main() {
        //       for (double i = 0; i <= 1.2; i+=0.01) {
        //           std::cout << plompLevelt(i) << std::endl;
        //       }
        //   }
        if (df < 0) return 1;
        if (df > 1.18) return 1;
        double res =
                -6.58977878 * df*df*df*df*df +
                        28.58224226 * df*df*df*df +
                        -47.36739986 * df*df*df +
                        35.70679761 * df*df +
                        -10.36526344 * df +
                        1.00026609;
        if (res < 0) return 0;
        if (res > 1) return 1;
        return res;
    }
    

    // In https://github.com/MTG/essentia/blob/master/src/essentia/essentiamath.h
    /**
     * Converts a given frequency into its Bark value.
     * This formula is taken from:
     *  H. Traunmüller (1990) "Analytical expressions for the tonotopic sensory scale" J. Acoust. Soc. Am. 88: 97-100.
     * and has been independently verified to be the one that best matches the band
     * frequencies defined by Zwicker in 1961.
     * @param f the input frequency, in Hz
     */
    double hz2bark(double f) {
        double b = ((26.81*f)/(1960 + f)) - 0.53;

        if (b < 2) b += 0.15*(2-b);
        if (b > 20.1) b += 0.22*(b - 20.1);

        return b;
    }

    /**
     * Converts a Bark value into its corresponding frequency.
     * This formula is deduced from:
     *  H. Traunmüller (1990) "Analytical expressions for the tonotopic sensory scale" J. Acoust. Soc. Am. 88: 97-100.
     * and has been independently verified to be the one that best matches the band
     * frequencies defined by Zwicker in 1961.
     * @param z the critical band rate, in Bark
     */
    double bark2hz(double z) {
        // note: these conditions have been deduced by inverting the ones from hz2bark
        if (z < 2) z = (z - 0.3) / 0.85;
        if (z > 20.1) z = (z - 4.422) / 1.22;

        // this part comes from Traunmüller's paper (could have deduced it also by myself... ;-) )
        return 1960.0 * (z + 0.53) / (26.28 - z);
    }

    double barkCriticalBandwidth(double z) {
        return 52548.0 / (z*z - 52.56 * z + 690.39);
    }


}
