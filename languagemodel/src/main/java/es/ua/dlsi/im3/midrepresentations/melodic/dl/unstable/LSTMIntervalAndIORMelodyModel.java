package es.ua.dlsi.im3.midrepresentations.melodic.dl.unstable;

import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.played.io.MidiSongImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.midrepresentations.sequences.CoupledNoteRepresentation;
import es.ua.dlsi.im3.midrepresentations.sequences.CoupledNoteSequence;
import es.ua.dlsi.im3.midrepresentations.sequences.IntervalAndIORSequenceFromMonophonicPlayedSongEncoder;
import es.ua.dlsi.im3.midrepresentations.sequences.Sequence;
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
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It encodes the interval and the IOR of each note
 *
 * This code is unstable, it has been used just as a concept proof
 */
public class LSTMIntervalAndIORMelodyModel<PitchType> {
    private static final int PITCH_ALPHABET = 25; // -12, 0, and 12
    public static final int EPOCHS = 100;
    public static final int ITERATIONS = 10;
    public static final double LEARNING_RATE = 0.001;
    // RNN dimensions
    public static final int HIDDEN_LAYER_WIDTH = 50;
    public static final int HIDDEN_LAYER_CONT = 2;
    public static final Random r = new Random(7894);
    private MultiLayerNetwork net;
    private int iterations;
    private int epochs;
    private int hiddenLayerWidth;
    private double learningRate;
    private double maxIOR;
    private CoupledNoteRepresentation<Integer, Double> emptySymbol;

    public LSTMIntervalAndIORMelodyModel(int epochs, int hiddenLayerWidth, int iterations, double learningRate) {
        this.epochs = epochs;
        this.hiddenLayerWidth = hiddenLayerWidth;
        this.iterations = iterations;
        this.learningRate = learningRate;
        init();
    }

    public LSTMIntervalAndIORMelodyModel() {
        this.epochs = EPOCHS;
        this.hiddenLayerWidth = HIDDEN_LAYER_WIDTH;
        this.iterations = ITERATIONS;
        this.learningRate = LEARNING_RATE;
        Logger.getLogger(LSTMIntervalAndIORMelodyModel.class.getName()).log(Level.INFO, "Building LSTM net with default parameters");
        init();
    }

    public LSTMIntervalAndIORMelodyModel(int epochs, int iterations) {
        this.epochs = epochs;
        this.hiddenLayerWidth = HIDDEN_LAYER_WIDTH;
        this.iterations = iterations;
        this.learningRate = LEARNING_RATE;
        Logger.getLogger(LSTMIntervalAndIORMelodyModel.class.getName()).log(Level.INFO, "Building LSTM net with default parameters");
        init();
    }
    private void init() {
        Logger.getLogger(LSTMIntervalAndIORMelodyModel.class.getName()).log(Level.INFO,
                "Epochs = {0}, iterations = {1}, hidden layer width = {2}, learning rate = {3}, pitch alphabet size = {4}",
                new Object[]{epochs, iterations, hiddenLayerWidth, learningRate, PITCH_ALPHABET});

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
            hiddenLayerBuilder.nIn(i == 0 ? (1 + PITCH_ALPHABET): HIDDEN_LAYER_WIDTH); // 1 for the IOR
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
        outputLayerBuilder.nOut(1 + PITCH_ALPHABET); // 1 for the rhythm
        listBuilder.layer(HIDDEN_LAYER_CONT, outputLayerBuilder.build());

        // finish builder
        listBuilder.pretrain(false);
        listBuilder.backprop(true);

        // create network
        MultiLayerConfiguration conf = listBuilder.build();
        net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));
        Logger.getLogger(LSTMIntervalAndIORMelodyModel.class.getName()).log(Level.INFO, net.summary());
    }

    public void save(File file) throws IOException {
        ModelSerializer.writeModel(net, file, true);
    }

    public void load(File file) throws IOException {
        net = ModelSerializer.restoreMultiLayerNetwork(file);
    }

    public void train(List<CoupledNoteSequence<Integer, Double>> sequences) {
        // we contatenate all sequences in one
        ArrayList<CoupledNoteRepresentation<Integer, Double>> concatenatedSequence = new ArrayList<>();
        emptySymbol = new CoupledNoteRepresentation<Integer, Double>(0,0.0);
        for (Sequence sequence: sequences) {
            concatenatedSequence.add(emptySymbol); // we begin each sequence with the empty symbol
            concatenatedSequence.addAll(sequence.getItems());
        }

        // get values for normalizing
        maxIOR = 0;
        for (CoupledNoteRepresentation<Integer, Double> note: concatenatedSequence) {
            maxIOR = Math.max(maxIOR, note.getRhythmType());
        }

        // CREATE OUR TRAINING DATA
        // create input and output arrays: SAMPLE_INDEX, INPUT_NEURON,
        // SEQUENCE_POSITION
        INDArray input = createInput(concatenatedSequence);
        INDArray labels = Nd4j.zeros(1, 1+PITCH_ALPHABET, concatenatedSequence.size()); // 1+ for the rhythm

        // loop through our sample-sentence
        for (int i=0; i<concatenatedSequence.size()-1; i++) {
            CoupledNoteRepresentation<Integer, Double> currentNote = concatenatedSequence.get(i);
            CoupledNoteRepresentation<Integer, Double> nextNote = concatenatedSequence.get(i+1);

            // input neurons for pitch and rhythm are set to 1
            setInputNeurons(input, currentNote, i);

            // output neurons for next pitch and rhythm are set to 1
            int nextNotePitchPos = encodePitchInterval(nextNote.getPitch());
            double nextNoteRhythm = currentNote.getRhythmType()  / maxIOR;
            //labels.putScalar(new int[] { 0, alphabet.getOrder(nextNote), i }, 1);
            labels.putScalar(new int[] { 0, 1+nextNotePitchPos, i }, 1);
            labels.putScalar(new int[] { 0, 0, i }, nextNoteRhythm);
        }

        //System.out.println("RAW input=" + concatenatedSequence);

        //System.out.println("input = " + input.toString());
        //System.out.println("labels= " + labels.toString());

        DataSet trainingData = new DataSet(input, labels);

        // some epochs
        for (int epoch = 0; epoch < epochs; epoch++) {
            Logger.getLogger(LSTMIntervalAndIORMelodyModel.class.getName()).log(Level.INFO, "Epoch {0}", epoch);

            // train the data
            net.fit(trainingData);

            // clear current stance from the last example
            net.rnnClearPreviousState();

            double p = evaluateSequence(concatenatedSequence);
            System.out.println("Probability of sequence: " + p);
        }
    }

    /**
     *
     * @param input
     * @param note
     * @param iexample Index of the vector that represents the example
     */
    private void setInputNeurons(INDArray input, CoupledNoteRepresentation<Integer, Double> note, int iexample) {
        int notePitchPos = encodePitchInterval(note.getPitch());
        double noteRhythm = note.getRhythmType() / maxIOR;
        input.putScalar(new int[] { 0, 1+notePitchPos, iexample }, 1); // 1+ for the rhythm
        input.putScalar(new int[] { 0, 0, iexample }, noteRhythm); // 0 for the rhythm neuron

        //TODO Comprobar que la segunda no machaca la primera
    }

    private INDArray createInput(ArrayList<CoupledNoteRepresentation<Integer, Double>> concatenatedSequence) {
        return Nd4j.zeros(1, 1+PITCH_ALPHABET, concatenatedSequence.size()); // 1+ for the rhythm
    }

    private double evaluateSequence(ArrayList<CoupledNoteRepresentation<Integer, Double>> sequence) {
            double p = 1;
            for (int j = 0; j < sequence.size()-1; j++) {
                INDArray testInit = createInput(sequence);
                setInputNeurons(testInit, sequence.get(j), j);

                // run one step -> IMPORTANT: rnnTimeStep() must be called, not
                // output()
                // the output shows what the net thinks what should come next
                INDArray output = net.rnnTimeStep(testInit);


                // the output of the network is the next note it supposes comes next
                int expectedInterval = encodePitchInterval(sequence.get(j+1).getPitch());
                double expectedIOR = sequence.get(j+1).getRhythmType() / maxIOR;

                double generatedIOR = output.getDouble(0);
                double pExpectedInterval = output.getDouble(1+expectedInterval);

                //System.out.println("Generated IOR " + generatedIOR + ", p(interval)=" + pExpectedInterval);
                double localp = generatedIOR * pExpectedInterval;
                p *= localp;
            }
            return p;
    }


    private int encodePitchInterval(Integer pitch) {
        if (pitch < 0) {
            pitch = -pitch;
            pitch = pitch % 12;
            return 12-pitch;
        } else {
            return 12+(pitch % 12);
        }
    }

    public static final void main(String [] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Use: <midi files folder> <epochs> <iterations>");
            return;
        }

        String folder = args[0];
        int epochs = Integer.parseInt(args[1]);
        int iterations = Integer.parseInt(args[2]);

        LSTMIntervalAndIORMelodyModel model = new LSTMIntervalAndIORMelodyModel(epochs, iterations);

        MidiSongImporter importer = new MidiSongImporter();

        ArrayList<File> files = new ArrayList<>();
        FileUtils.readFiles(new File(folder), files, "mid");

        List<CoupledNoteSequence<Integer, Double>> sequences = new ArrayList<>();
        for (File file: files) {
            PlayedSong playedSong = importer.importSong(file);

            IntervalAndIORSequenceFromMonophonicPlayedSongEncoder encoder = new IntervalAndIORSequenceFromMonophonicPlayedSongEncoder();
            CoupledNoteSequence<Integer, Double> encodedSong = encoder.encode(playedSong);
            sequences.add(encodedSong);
        }
        Instant t0 = Instant.now();
        model.train(sequences);
        Instant t1 = Instant.now();
        double seconds = Duration.between(t0, t1).toMillis() / 1000.0;
        System.out.println("Seconds used for training " + files.size() + " files in " + epochs + " epochs and " + iterations + " iterations: " + seconds);
    }
}
