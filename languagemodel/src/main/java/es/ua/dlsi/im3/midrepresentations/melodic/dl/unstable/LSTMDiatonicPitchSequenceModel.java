package es.ua.dlsi.im3.midrepresentations.melodic.dl.unstable;

import es.ua.dlsi.im3.core.score.DiatonicPitch;
import es.ua.dlsi.im3.midrepresentations.Alphabet;

/**
 * Given a diatonic note X, it returns a distribution of the most probable notes after X
 *
 * This code is unstable, it has been used just as a concept proof
 */
public class LSTMDiatonicPitchSequenceModel extends LSTMCoupledMelodyModel<DiatonicPitch> {
    public LSTMDiatonicPitchSequenceModel(Alphabet<DiatonicPitch> alphabet, int epochs, int hiddenLayerWidth, int iterations, double learningRate) {
        super(alphabet, epochs, hiddenLayerWidth, iterations, learningRate);
    }

    public LSTMDiatonicPitchSequenceModel(Alphabet<DiatonicPitch> alphabet) {
        super(alphabet);
    }
    public LSTMDiatonicPitchSequenceModel(Alphabet<DiatonicPitch> alphabet, int epochs, int iterations) {
        super(alphabet, epochs, iterations);
    }
}
