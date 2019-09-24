package es.easda.virema;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SoundControllerOLD implements Initializable {
    @FXML
    VBox vboxInformation;
    @FXML
    Canvas canvas;
    @FXML
    ScrollPane scrollPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
        Complex[] freqs = fft.transform(vals, TransformType.FORWARD);
        for (Complex freq: freqs) {
            System.out.println(freq.getReal() + "\t" + freq.getImaginary());
        }
    }*/

    private void readWavFile(File file) throws IOException, IM3Exception {
        // see https://knowm.org/exploring-bird-song-with-a-spectrogram-in-java/
       /* Wave wave = new Wave(file.getAbsolutePath());
        Spectrogram spectrogram = wave.getSpectrogram(1024*128, 0);

        vboxInformation.getChildren().clear();
        vboxInformation.getChildren().add(new Label("Frames per second: " + spectrogram.getFramesPerSecond()));
        vboxInformation.getChildren().add(new Label("FFT sample size: "+ spectrogram.getFftSampleSize()));
        vboxInformation.getChildren().add(new Label( "Num frequency unit: " + spectrogram.getNumFrequencyUnit()));
        vboxInformation.getChildren().add(new Label("Overlap factor: " + spectrogram.getOverlapFactor()));

        //double[][] spectrogramData = spectrogram.getNormalizedSpectrogramData();
        double[][] spectrogramData = spectrogram.getAbsoluteSpectrogramData();
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int t=0; t<spectrogramData.length; t++) {
            for (int f = 0; f < spectrogramData[t].length; f++) {
                min = Math.min(min, spectrogramData[t][f]);
                max = Math.max(max, spectrogramData[t][f]);
            }
        }

        double xItemWidth =  scrollPane.getViewportBounds().getWidth() / spectrogramData.length;
        double yItemHeight = scrollPane.getViewportBounds().getHeight() / spectrogram.getNumFrequencyUnit();
        canvas.setWidth(xItemWidth*spectrogramData.length);
        double canvasHeight = yItemHeight * spectrogram.getNumFrequencyUnit();
        canvas.setHeight(canvasHeight );
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        for (int t=0; t<spectrogramData.length; t++) {
            if (spectrogramData[t].length != spectrogram.getNumFrequencyUnit()) {
                throw new IM3Exception("Invalid spectrogram lengths");
            }

            for (int f=0; f<spectrogramData[t].length; f++) {
                //int value = (int) (spectrogramData[t][f] * 255.0);
                Color color = generateColor(spectrogram.getNumFrequencyUnit(), f, spectrogramData[t][f], min, max);
                graphicsContext.setFill(color);
                double x = t*xItemWidth;
                double y = f*yItemHeight;
                graphicsContext.fillRect(x, canvasHeight-y, xItemWidth+1, yItemHeight+1); // +1 to avoid white lines
            }
        }*/
    }

    private Color generateColor(int numFrequencies, int frequency, double energy, double minEnergy, double maxEnergy) {
        double value = (energy - minEnergy) / (maxEnergy - minEnergy);
        if (value < 0.1) {
            return Color.BLACK;
        }

        // energy -> saturation
        double C = ColorUtils.map(energy, minEnergy, maxEnergy, 0, 120);

        // octave -> light
        double L = ColorUtils.map(frequency / 12, 0.0, numFrequencies / 12, 0.0, 100);

        // pitch -> hue
        double H = ColorUtils.map(frequency % 12, 0.0, 12, 0.0, 360.0);

        //return ColorUtils.hcl2RGB(L, C, H);

        int rgbValue = (int) (value * 255.0);
        return Color.rgb(rgbValue, rgbValue, rgbValue);
    }
}
