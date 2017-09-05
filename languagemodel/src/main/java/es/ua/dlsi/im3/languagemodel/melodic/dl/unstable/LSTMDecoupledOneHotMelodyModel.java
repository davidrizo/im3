package es.ua.dlsi.im3.languagemodel.melodic.dl.unstable;

import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.languagemodel.Alphabet;
import es.ua.dlsi.im3.languagemodel.melodic.IMelodicModel;
import es.ua.dlsi.im3.languagemodel.sequences.Sequence;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Based on the example in org.deeplearning4j.examples.recurrent.basic.BasicRNNExample.
 * Given a note X, it returns a distribution of the most probable notes after X
 * Note is encoded in two elements, one for pitch, the other for rhythm
 *
 * This code is unstable, it has been used just as a concept proof
 */
public class LSTMDecoupledOneHotMelodyModel<PitchType, RhythmType> implements IMelodicModel<Sequence<Pair<PitchType, RhythmType>>> {
    public static final int EPOCHS = 100;
    public static final int ITERATIONS = 10;
    public static final double LEARNING_RATE = 0.001;
    // RNN dimensions
    public static final int HIDDEN_LAYER_WIDTH = 50;
    public static final int HIDDEN_LAYER_CONT = 2;
    public static final Random r = new Random(7894);
    private MultiLayerNetwork net;
    Alphabet<PitchType> pitchAlphabet;
    Alphabet<RhythmType> rhythmAlphabet;
    private int iterations;
    private int epochs;
    private int hiddenLayerWidth;
    private double learningRate;

    public LSTMDecoupledOneHotMelodyModel(Alphabet<PitchType> pitchAlphabet, Alphabet<RhythmType> rhythmAlphabet, int epochs, int hiddenLayerWidth, int iterations, double learningRate) {
        this.epochs = epochs;
        this.hiddenLayerWidth = hiddenLayerWidth;
        this.iterations = iterations;
        this.learningRate = learningRate;
        this.pitchAlphabet = pitchAlphabet;
        this.rhythmAlphabet = rhythmAlphabet;
        init();
    }

    public LSTMDecoupledOneHotMelodyModel(Alphabet<PitchType> pitchAlphabet, Alphabet<RhythmType> rhythmAlphabet) {
        this.epochs = EPOCHS;
        this.hiddenLayerWidth = HIDDEN_LAYER_WIDTH;
        this.iterations = ITERATIONS;
        this.learningRate = LEARNING_RATE;
        this.pitchAlphabet = pitchAlphabet;
        this.rhythmAlphabet = rhythmAlphabet;
        Logger.getLogger(LSTMDecoupledOneHotMelodyModel.class.getName()).log(Level.INFO, "Building LSTM net with default parameters");
        init();
    }

    private void init() {
        Logger.getLogger(LSTMDecoupledOneHotMelodyModel.class.getName()).log(Level.INFO,
                "Epochs = {0}, iterations = {1}, hidden layer width = {2}, learning rate = {3}, pitch alphabet size = {4}, rhythm alphabet size = {5}",
                new Object[]{epochs, iterations, hiddenLayerWidth, learningRate, pitchAlphabet.getSize(), rhythmAlphabet.getSize()});

        // some common parameters
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
        builder.iterations(iterations);
        builder.learningRate(learningRate);
        builder.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
        builder.seed(123);
        builder.biasInit(0);
        //TODO Usar batches
        builder.miniBatch(false);
        builder.updater(Updater.RMSPROP);
        builder.weightInit(WeightInit.XAVIER);

        NeuralNetConfiguration.ListBuilder listBuilder = builder.list();

        // first difference, for rnns we need to use GravesLSTM.Builder
        for (int i = 0; i < HIDDEN_LAYER_CONT; i++) {
            GravesLSTM.Builder hiddenLayerBuilder = new GravesLSTM.Builder();
            hiddenLayerBuilder.nIn(i == 0 ? (pitchAlphabet.getSize() + rhythmAlphabet.getSize()): HIDDEN_LAYER_WIDTH);
            hiddenLayerBuilder.nOut(HIDDEN_LAYER_WIDTH);
            // adopted activation function from GravesLSTMCharModellingExample
            // seems to work well with RNNs
            hiddenLayerBuilder.activation(Activation.TANH);
            listBuilder.layer(i, hiddenLayerBuilder.build());
        }

        // we need to use RnnOutputLayer for our RNN
        RnnOutputLayer.Builder outputLayerBuilder = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT);
        // softmax normalizes the output neurons, the sum of all outputs is 1
        // this is required for our sampleFromDistribution-function
        outputLayerBuilder.activation(Activation.SOFTMAX);
        outputLayerBuilder.nIn(HIDDEN_LAYER_WIDTH);
        outputLayerBuilder.nOut(pitchAlphabet.getSize() + rhythmAlphabet.getSize());
        listBuilder.layer(HIDDEN_LAYER_CONT, outputLayerBuilder.build());

        // finish builder
        listBuilder.pretrain(false);
        listBuilder.backprop(true);

        // create network
        MultiLayerConfiguration conf = listBuilder.build();
        net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));
        Logger.getLogger(LSTMDecoupledOneHotMelodyModel.class.getName()).log(Level.INFO, net.summary());
    }

    public void save(File file) throws IOException {
        ModelSerializer.writeModel(net, file, true);
    }

    public void load(File file) throws IOException {
        net = ModelSerializer.restoreMultiLayerNetwork(file);
    }

    public void train(List<Sequence<PitchType>> sequences) {
        // we contatenate all sequences in one
        ArrayList<Pair<PitchType, RhythmType>> concatenatedSequence = new ArrayList<>();
        Pair<PitchType, RhythmType> emptySymbol = new Pair<>(pitchAlphabet.getEmptySymbol(), rhythmAlphabet.getEmptySymbol());
        for (Sequence sequence: sequences) {
            concatenatedSequence.addAll(sequence.getItems());
            concatenatedSequence.add(emptySymbol);
        }

		// CREATE OUR TRAINING DATA
        // create input and output arrays: SAMPLE_INDEX, INPUT_NEURON,
        // SEQUENCE_POSITION
        INDArray input = Nd4j.zeros(1, pitchAlphabet.getSize()+rhythmAlphabet.getSize(), concatenatedSequence.size());
        INDArray labels = Nd4j.zeros(1, pitchAlphabet.getSize()+rhythmAlphabet.getSize(), concatenatedSequence.size());

        // loop through our sample-sentence
        for (int i=0; i<concatenatedSequence.size()-1; i++) {
            Pair<PitchType, RhythmType> currentNote = concatenatedSequence.get(i);
            Pair<PitchType, RhythmType> nextNote = concatenatedSequence.get(i+1);

            // input neurons for pitch and rhythm are set to 1
            int notePitchPos = pitchAlphabet.getOrder(currentNote.getX());
            int noteRhythmPos = rhythmAlphabet.getOrder(currentNote.getY());
            input.putScalar(new int[] { 0, notePitchPos, i }, 1);
            input.putScalar(new int[] { 0, pitchAlphabet.getSize()+noteRhythmPos, i }, 1);

            //TODO Comprobar que la segunda llamada no machaca la primera

            // output neurons for next pitch and rhythm are set to 1
            int nextNotePitchPos = pitchAlphabet.getOrder(nextNote.getX());
            int nextNoteRhythmPos = rhythmAlphabet.getOrder(nextNote.getY());
            //labels.putScalar(new int[] { 0, alphabet.getOrder(nextNote), i }, 1);
            labels.putScalar(new int[] { 0, nextNotePitchPos, i }, 1);
            labels.putScalar(new int[] { 0, pitchAlphabet.getSize()+nextNoteRhythmPos, i }, 1);
        }

        //System.out.println("input = " + input.toString());
        //System.out.println("labels= " + labels.toString());
        DataSet trainingData = new DataSet(input, labels);

        // some epochs
        for (int epoch = 0; epoch < EPOCHS; epoch++) {
            Logger.getLogger(LSTMDecoupledOneHotMelodyModel.class.getName()).log(Level.INFO, "Epoch {0}", epoch);

            // train the data
            net.fit(trainingData);

            // clear current stance from the last example
            net.rnnClearPreviousState();

            /*// put the first caracter into the rrn as an initialisation
            INDArray testInit = Nd4j.zeros(LEARNSTRING_CHARS_LIST.size());
            testInit.putScalar(LEARNSTRING_CHARS_LIST.indexOf(LEARNSTRING[0]), 1);

            // run one step -> IMPORTANT: rnnTimeStep() must be called, not
            // output()
            // the output shows what the net thinks what should come next
            INDArray output = net.rnnTimeStep(testInit);
            // now the net should guess LEARNSTRING.length mor characters
            for (int j = 0; j < LEARNSTRING.length; j++) {

                // first process the last output of the network to a concrete
                // neuron, the neuron with the highest output cas the highest
                // cance to get chosen
                double[] outputProbDistribution = new double[LEARNSTRING_CHARS.size()];
                for (int k = 0; k < outputProbDistribution.length; k++) {
                    outputProbDistribution[k] = output.getDouble(k);
                }
                int sampledCharacterIdx = findIndexOfHighestValue(outputProbDistribution);

                // print the chosen output
                System.out.print(LEARNSTRING_CHARS_LIST.get(sampledCharacterIdx));

                // use the last output as input
                INDArray nextInput = Nd4j.zeros(LEARNSTRING_CHARS_LIST.size());
                nextInput.putScalar(sampledCharacterIdx, 1);
                output = net.rnnTimeStep(nextInput);

            }
            System.out.print("\n");*/
        }
    }


}
