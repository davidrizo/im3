package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassifier;
import es.ua.dlsi.im3.core.patternmatching.RankingItem;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.Point;
import es.ua.dlsi.im3.omr.model.entities.Strokes;
import es.ua.dlsi.im3.omr.model.exporters.XML2AgnosticSymbolImagesStrokesTextFile;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * This classifier returns the exact symbol most similar to this one in the training dataset, with its position included
 * It is trained from a list of folders contained files with tagged images (extension symbolsimages.txt-see HISPAMUS/trainingsets/catedral_zaragoza/staves_symbols_images)
 * @autor drizo
 */
public class BimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer extends NearestNeighbourClassifier<AgnosticSymbol, SymbolImageAndPointsPrototype>  implements IBimodalSymbolFromImageDataAndStrokesRecognizer {
    private final AgnosticVersion agnosticVersion;
    private File trainingDataFolder;

    public BimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer(AgnosticVersion agnosticVersion)  {
        this.agnosticVersion = agnosticVersion;
    }

    public BimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer(AgnosticVersion agnosticVersion, File trainingDataFolder) throws IM3Exception {
        this.trainingDataFolder = trainingDataFolder;
        this.agnosticVersion = agnosticVersion;
        train();
    }

    @Override
    public TreeSet<RankingItem<SymbolImageAndPointsPrototype>> recognize(GrayscaleImageData imageData, Strokes strokes) throws IM3Exception {
        SymbolImageAndPointsPrototype prototype = new SymbolImageAndPointsPrototype(null, imageData, strokes);
        TreeSet<RankingItem<SymbolImageAndPointsPrototype>> orderedValues = this.classify(prototype);
        return orderedValues;
    }

    public void trainWithFile(File trainingFile) throws IM3Exception, IOException {
        loadTrainingFile(trainingFile);
    }
    @Override
    protected void train() throws IM3Exception {
        ArrayList<File> trainingFiles = new ArrayList<>();
        try {
            FileUtils.readFiles(trainingDataFolder, trainingFiles, "symbolsimages_30x30.txt", true);
            for (File trainingFile: trainingFiles) {
                loadTrainingFile(trainingFile);
            }
        } catch (IOException e) {
            throw new IM3Exception(e);
        }

    }

    private void loadTrainingFile(File trainingFile) throws IOException, IM3Exception {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(trainingFile));
        BufferedReader br = new BufferedReader(isr);
        String line;

        int n=1;
        while ((line=br.readLine())!=null) {
            if (!line.startsWith("#")) {
                String[] components = line.split(XML2AgnosticSymbolImagesStrokesTextFile.FIELD_SEPARATOR);
                if (components.length != 6 && components.length !=5) {
                    throw new IOException("Invalid line, must have 5 components and it has just " + components.length);
                } else {
                    PointsData pointsData = null;
                    AgnosticSymbol agnosticSymbol = AgnosticSymbol.parseAgnosticString(agnosticVersion, components[3]);
                    ArrayList<Integer> pixels = new ArrayList<>();
                    String[] pxs = components[4].split(XML2AgnosticSymbolImagesStrokesTextFile.COMMA);
                    for (String px : pxs) {
                        try {
                            pixels.add(Integer.parseInt(px));
                        } catch (NumberFormatException e) {
                            System.err.println("Error in line # " + n + ", reading pixel '" + px + "' in component '" + components[4] + "'");
                            throw new IM3Exception(e);
                        }
                    }

                    GrayscaleImageData grayscaleImageData = new GrayscaleImageData(pixels);
                    if (components.length == 6 && !components[5].trim().isEmpty()) {
                        String[] strokes = components[5].split(XML2AgnosticSymbolImagesStrokesTextFile.STROKES_SEPARATOR);
                        pointsData = new PointsData();
                        for (String stroke : strokes) {
                            String[] strokeComponents = stroke.split(XML2AgnosticSymbolImagesStrokesTextFile.COMMA);
                            if (strokeComponents.length % 3 != 0) {
                                throw new IM3Exception("Expected length divisible by 3, and found " + strokeComponents.length + " in line #" + n + " stroke '" + stroke + "'");
                            }
                            for (int i = 0; i < strokeComponents.length; i += 3) {
                                pointsData.addPoint(new Point(Long.parseLong(strokeComponents[i]),
                                        Double.parseDouble(strokeComponents[i + 1]),
                                        Double.parseDouble(strokeComponents[i + 2])));
                            }
                        }
                    }
                    this.addPrototype(new SymbolImageAndPointsPrototype(agnosticSymbol, grayscaleImageData, pointsData));
                }
            }
            n++;
        }
    }
}
