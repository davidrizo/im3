package es.ua.dlsi.im3.languagemodel.melodic.dl.unstable;

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
 * Given a note X, it returns a distribution of the most probable notes after X.
 * Note is encoded in an only element
 *
 * This code is unstable, it has been used just as a concept proof
 */
public class LSTMCoupledMelodyModel<NoteItemType> implements IMelodicModel<Sequence<NoteItemType>> {
    public static final int EPOCHS = 100;
    public static final int ITERATIONS = 10;
    public static final double LEARNING_RATE = 0.001;
    // RNN dimensions
    public static final int HIDDEN_LAYER_WIDTH = 10;
    //public static final int HIDDEN_LAYER_WIDTH = 50;
    public static final int HIDDEN_LAYER_CONT = 2;
    public static final Random r = new Random(7894);
    private MultiLayerNetwork net;
    Alphabet<NoteItemType> alphabet;
    private int iterations;
    private int epochs;
    private int hiddenLayerWidth;
    private double learningRate;

    public LSTMCoupledMelodyModel(Alphabet<NoteItemType> alphabet, int epochs, int hiddenLayerWidth, int iterations, double learningRate) {
        this.epochs = epochs;
        this.hiddenLayerWidth = hiddenLayerWidth;
        this.iterations = iterations;
        this.learningRate = learningRate;
        this.alphabet = alphabet;
        init();
    }

    public LSTMCoupledMelodyModel(Alphabet<NoteItemType> alphabet, int epochs, int iterations) {
        this.epochs = epochs;
        this.hiddenLayerWidth = HIDDEN_LAYER_WIDTH;
        this.iterations = iterations;
        this.learningRate = LEARNING_RATE;
        this.alphabet = alphabet;
        init();
    }

    public LSTMCoupledMelodyModel(Alphabet<NoteItemType> alphabet) {
        this.epochs = EPOCHS;
        this.hiddenLayerWidth = HIDDEN_LAYER_WIDTH;
        this.iterations = ITERATIONS;
        this.learningRate = LEARNING_RATE;
        this.alphabet = alphabet;
        Logger.getLogger(LSTMCoupledMelodyModel.class.getName()).log(Level.INFO, "Building LSTM net with default parameters");
        init();
    }

    private void init() {
        Logger.getLogger(LSTMCoupledMelodyModel.class.getName()).log(Level.INFO,
                "Epochs = {0}, iterations = {1}, hidden layer width = {2}, learning rate = {3}, alphabet size = {4}",
                new Object[]{epochs, iterations, hiddenLayerWidth, learningRate, alphabet.getSize()});

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
            hiddenLayerBuilder.nIn(i == 0 ? alphabet.getSize() : HIDDEN_LAYER_WIDTH);
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
        outputLayerBuilder.nOut(alphabet.getSize());
        listBuilder.layer(HIDDEN_LAYER_CONT, outputLayerBuilder.build());

        // finish builder
        listBuilder.pretrain(false);
        listBuilder.backprop(true);

        // create network
        MultiLayerConfiguration conf = listBuilder.build();
        net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));
        Logger.getLogger(LSTMCoupledMelodyModel.class.getName()).log(Level.INFO, net.summary());
    }

    public void save(File file) throws IOException {
        ModelSerializer.writeModel(net, file, true);
    }

    public void load(File file) throws IOException {
        net = ModelSerializer.restoreMultiLayerNetwork(file);
    }

    public void train(List<Sequence<NoteItemType>> sequences) {
        // we concatenate all sequences in one
        ArrayList<NoteItemType> concatenatedSequence = new ArrayList<>();
        for (Sequence sequence: sequences) {
            concatenatedSequence.addAll(sequence.getItems());
            concatenatedSequence.add(alphabet.getEmptySymbol());
        }

		// CREATE OUR TRAINING DATA
        // create input and output arrays: SAMPLE_INDEX, INPUT_NEURON,
        // SEQUENCE_POSITION
        INDArray input = Nd4j.zeros(1, alphabet.getSize(), concatenatedSequence.size());
        INDArray labels = Nd4j.zeros(1, alphabet.getSize(), concatenatedSequence.size());

        // loop through our sample-sentence
        for (int i=0; i<concatenatedSequence.size()-1; i++) {
            NoteItemType currentNote = concatenatedSequence.get(i);
            NoteItemType nextNote = concatenatedSequence.get(i+1);

            // input neuron for current-char is 1 at "samplePos"
            input.putScalar(new int[] { 0, alphabet.getOrder(currentNote), i }, 1);
            // output neuron for next-char is 1 at "samplePos"
            labels.putScalar(new int[] { 0, alphabet.getOrder(nextNote), i }, 1);
        }

        //System.out.println("input = " + input.toString());
        //System.out.println("labels= " + labels.toString());
        DataSet trainingData = new DataSet(input, labels);

        // some epochs
        for (int epoch = 0; epoch < epochs; epoch++) {
            Logger.getLogger(LSTMCoupledMelodyModel.class.getName()).log(Level.INFO, "Epoch {0}", epoch);

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
