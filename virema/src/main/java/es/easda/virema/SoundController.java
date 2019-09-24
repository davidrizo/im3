package es.easda.virema;

import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class SoundController implements Initializable {
    private static final int HOP_SIZE = 512;
    //@FXML
    //VBox vboxInformation;
    @FXML
    Canvas spectrogramCanvas;
    @FXML
    Canvas patternsCanvas;
    @FXML
    ScrollPane scrollPane;
    @FXML
    Slider sliderThreshold;
    @FXML
    Label labelThreshold;
    @FXML
    Slider sliderZoomVertical;
    @FXML
    Slider sliderZoomHorizontal;
    @FXML
    StackPane stackPane;
    @FXML
    ToggleButton btnPlay;
    @FXML
    Line playLine;

    private static final double AXIS_SPACE = 20;

    private double minEnergy;
    private double maxEnergy;
    private final float[] midiNoteFrequencies;
    private ArrayList<Float[]> spectrumInTime;
    private GraphicsContext spectrogramGraphicsContext;
    private GraphicsContext patternsGraphicsContext;
    private File file;
    private Task<Void> playTask;
    private DoubleProperty playLinePosition;
    private double xItemWidth;

    public SoundController() {
        // https://newt.phys.unsw.edu.au/jw/notes.html
        midiNoteFrequencies = new float[128];
        for (int m=0; m<128; m++) {
            double fm = Math.pow(2, ((float)m-69.0)/12.0) * 440.0;
            midiNoteFrequencies[m] = (float) fm;
            System.out.println("Note midi=" + m + ", freq=" + fm);
        }


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playLinePosition = new SimpleDoubleProperty(0);
        playLine.setStartY(0);
        playLine.endYProperty().bind(spectrogramCanvas.heightProperty());
        playLine.translateXProperty().bind(playLinePosition.add(AXIS_SPACE));

        stackPane.prefWidthProperty().bind(spectrogramCanvas.widthProperty());
        stackPane.prefHeightProperty().bind(spectrogramCanvas.heightProperty());

        patternsCanvas.widthProperty().bind(spectrogramCanvas.widthProperty());
        spectrogramGraphicsContext = spectrogramCanvas.getGraphicsContext2D();
        patternsGraphicsContext = patternsCanvas.getGraphicsContext2D();
        labelThreshold.textProperty().bind(sliderThreshold.valueProperty().asString());
        ChangeListener<Number> changeListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (spectrogramGraphicsContext != null) {
                    paintCanvas();
                }
            }
        };

        sliderThreshold.valueProperty().addListener(changeListener);
        sliderZoomHorizontal.valueProperty().addListener(changeListener);
        sliderZoomVertical.valueProperty().addListener(changeListener);

        spectrogramGraphicsContext.setFont(Font.font(7));
        //spectrogramGraphicsContext.setTextAlign(TextAlignment.RIGHT);

        btnPlay.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                handlePlay();
            }
        });
    }

    @FXML
    private void handleOpenWav() {
        OpenSaveFileDialog dialog = new OpenSaveFileDialog();
        File file = dialog.openFile("Open wav file", "wav", ".wav");
        if (file != null) {
            try {
                readWavFile(file);
            } catch (Exception e) {
                e.printStackTrace();
                ShowError.show(null, "Cannot open wav file", e);
            }
        }
    }

    /*int nextPowerOf2(int n)
    {
        int p = 1;
        if (n > 0 && (n & (n - 1)) == 0)
            return n;

        while (p < n)
            p <<= 1;

        return p;
    }


    private void readWavFile(File file) throws IOException, UnsupportedAudioFileException {
        InputStream is = AudioSystem.getAudioInputStream(file);
        byte[] bytes = IOUtils.toByteArray(is);

        short[] shorts = new short[bytes.length/2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

        // FastFourierTransformer needs a power of 2 array
        int size = nextPowerOf2(shorts.length);

        double[] vals = new double[size];
        for (int i = 0; i < shorts.length; i++)
            vals[i] = (double) shorts[i];

        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] midiNoteFrequencies = fft.transform(vals, TransformType.FORWARD);
        for (Complex freq: midiNoteFrequencies) {
            System.out.println(freq.getReal() + "\t" + freq.getImaginary());
        }
    }*/

    private void readWavFile(File file) throws Exception {
        /*this.file = file;
        WaveDecoder decoder = new WaveDecoder(new FileInputStream(file));
        SpectrumProvider spectrumProvider = new SpectrumProvider( decoder, 4096, HOP_SIZE, true );

        minEnergy = Double.MAX_VALUE;
        maxEnergy = Double.MIN_VALUE;

        spectrumInTime = new ArrayList<>();
        float[] spectrum = null;
        while ((spectrum = spectrumProvider.nextSpectrum())!=null) {
            Float [] column = new Float[128];
            column[0] = 0.0f;
            for (int i = 0; i< midiNoteFrequencies.length; i++) {
                //float indexFreq = spectrumProvider.getFFT().freqToIndex(midiNoteFrequencies[i]);
                float avg = spectrumProvider.getFFT().getFreq(midiNoteFrequencies[i]);
                //float avg = spectrumProvider.getFFT().calcAvg(midiNoteFrequencies[i]-5, midiNoteFrequencies[i]+5);
                column[i] = avg;

                minEnergy = Math.min(minEnergy, avg);
                maxEnergy = Math.max(maxEnergy, avg);

            }
            spectrumInTime.add(column);
        }

        paintCanvas();*/
    }

    private void paintCanvas() {


        xItemWidth =  (sliderZoomHorizontal.getValue() / 50) * scrollPane.getViewportBounds().getWidth() / spectrumInTime.size();
        double yItemHeight = (sliderZoomVertical.getValue() / 50) * (scrollPane.getViewportBounds().getHeight()-patternsCanvas.getHeight()) / 128;
        spectrogramCanvas.setWidth(AXIS_SPACE + xItemWidth*spectrumInTime.size());
        double canvasHeight = yItemHeight * 128;
        spectrogramCanvas.setHeight(AXIS_SPACE+ canvasHeight );
        //spectrogramGraphicsContext.setFill(Color.BLACK);
        spectrogramGraphicsContext.clearRect(0, 0, spectrogramCanvas.getWidth(), spectrogramCanvas.getHeight());
        //patternsGraphicsContext.setFill(Color.BLACK);
        patternsGraphicsContext.clearRect(0, 0, patternsCanvas.getWidth(), patternsCanvas.getHeight());

        for (int t=0; t<spectrumInTime.size(); t++) {
            Float [] column = spectrumInTime.get(t);

            if (t%100 == 0) {
                spectrogramGraphicsContext.setFill(Color.BLACK);
                spectrogramGraphicsContext.fillText(Integer.toString(t), t * xItemWidth, canvasHeight);
            }

            ArrayList<Color> usedColors = new ArrayList<>();
            double x = t*xItemWidth;
            for (int f=0; f<column.length; f++) { // for each frequency
                //int value = (int) (spectrogramData[t][f] * 255.0);
                //Color color = generateColor(128, midiNoteFrequencies[f], midiNoteFrequencies[127], column[f], minEnergy, maxEnergy);
                Color color = generateColor(f % 12, 11, f / 12, column[f], minEnergy, maxEnergy);
                spectrogramGraphicsContext.setFill(color);
                if (!color.equals(Color.BLACK)) {
                    usedColors.add(color);
                }
                double y = f*yItemHeight;
                spectrogramGraphicsContext.fillRect(AXIS_SPACE+x, canvasHeight-AXIS_SPACE-y, xItemWidth+1, yItemHeight+1); // +1 to avoid white lines

                if (t==0) {
                    if (f%12 == 0) {
                        spectrogramGraphicsContext.setFill(Color.BLACK);
                    } else {
                        spectrogramGraphicsContext.setFill(Color.GRAY);
                    }
                    spectrogramGraphicsContext.fillText(Integer.toString(127-f-1), 0, y);
                }
            }

            // use those colors to fill the pattern canvas
            if (usedColors.size() > 0) {
                Collections.shuffle(usedColors);
                for (int i = 0; i < patternsCanvas.getHeight(); i++) {
                    patternsGraphicsContext.setFill(usedColors.get(i % usedColors.size()));
                    patternsGraphicsContext.fillRect(AXIS_SPACE + x, i, xItemWidth+1, 1);
                }
            } else {
                patternsGraphicsContext.setFill(Color.BLACK);
                patternsGraphicsContext.fillRect(AXIS_SPACE + x, 0, xItemWidth+1, patternsCanvas.getHeight());
            }
        }
    }

    private Color generateColor(double frequency, double maxFrequency, int octave, double energy, double minEnergy, double maxEnergy) {
        double value = (energy - minEnergy) / (maxEnergy - minEnergy);
        if (value < sliderThreshold.getValue()/100.0) {
            return Color.BLACK;
        }

        // energy -> saturation
        double C = ColorUtils.map(energy, minEnergy, maxEnergy, 0, 120);

        // octave -> light
        double L = ColorUtils.map(octave, 0.0, 127/12, 0.0, 100);

        // pitch -> hue
        double H = ColorUtils.map(frequency, 0.0, 12, 0.0, 360.0);

        return ColorUtils.hcl2RGB(L, C, H);

        //int rgbValue = (int) (value * 255.0);
        //return Color.rgb(rgbValue, rgbValue, rgbValue);
    }

    private void handlePlay() {
        if (btnPlay.isSelected()) {
            btnPlay.setText("Stop");

            try {
                doPlay();
            } catch (Exception e) {
                e.printStackTrace();
                ShowError.show(null, "Cannot play", e);
                btnPlay.setSelected(false);
            }
        } else {
            btnPlay.setText("Play");
            if (playTask != null) {
                playTask.cancel();
            }
        }
    }

    private void doPlay()  {
        /*if (file != null) {
            playTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    AudioDevice device = new AudioDevice();
                    float[] samples = new float[1024];
                    WaveDecoder decoder = new WaveDecoder(new FileInputStream(file));
                    long startTime = 0;

                    int i=0;
                    while (!playTask.isCancelled() && decoder.readSamples(samples) > 0) {
                        if (startTime == 0) {
                            startTime = System.nanoTime();
                        }
                        float elapsedTime = (System.nanoTime() - startTime) / 1000000000.0f;
                        int position = (int) (elapsedTime * (44100 / HOP_SIZE)*xItemWidth);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                playLinePosition.setValue(position);
                            }
                        });

                        device.writeSamples(samples);
                        //int position = (int) ((44100 / 1024)*xItemWidth)*i;
                        //int position = (int) ((i-1)*xItemWidth)*1024/HOP_SIZE;
                        i++;


                        //Thread.sleep(15); // this is needed or else swing has no chance repainting the plot!
                    }

                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            btnPlay.setSelected(false);
                        }
                    });
                    return null;
                }
            };

            new Thread(playTask).start();
        }*/
    }

}
