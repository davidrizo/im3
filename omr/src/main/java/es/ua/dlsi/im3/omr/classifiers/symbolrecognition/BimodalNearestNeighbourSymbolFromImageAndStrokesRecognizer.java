package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassesRanking;
import es.ua.dlsi.im3.core.patternmatching.RankingItem;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.Point;
import es.ua.dlsi.im3.omr.model.entities.Strokes;
import es.ua.dlsi.im3.omr.model.exporters.XML2AgnosticSymbolImagesStrokesTextFile;

import java.io.*;
import java.util.*;

/**
 * This classifier uses the method explained in ISMIR'2016 Two heads are better than one Calvo, Rizo & Iñesta paper
 */
public class BimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer implements IBimodalSymbolFromImageDataAndStrokesRecognizer {
    private final AgnosticVersion agnosticVersion;
    NearestNeighbourSymbolFromImageRecognizer imagesRecognizer;
    NearestNeighbourSymbolFromStrokesRecognizer strokesRecognizer;
    LinkedList<SymbolImageAndPointsPrototype> trainingSet;
    public static double IMAGES_CLASSIFIER_WEIGHT = 0.7;
    double imagesClassifierWeight;

    public BimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer(AgnosticVersion agnosticVersion, double imagesClassifierWeight)  {
        this.agnosticVersion = agnosticVersion;
        this.imagesClassifierWeight = imagesClassifierWeight;
        init();
    }

    public BimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer(AgnosticVersion agnosticVersion)  {
        this(agnosticVersion, IMAGES_CLASSIFIER_WEIGHT);
    }

    private void init() {
        imagesRecognizer = new NearestNeighbourSymbolFromImageRecognizer(agnosticVersion);
        strokesRecognizer = new NearestNeighbourSymbolFromStrokesRecognizer(agnosticVersion);
    }

    @Override
    public NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImageAndPointsPrototype> recognize(GrayscaleImageData imageData, Strokes strokes) throws IM3Exception {
        SymbolImagePrototype imagePrototype = new SymbolImagePrototype(null, imageData);
        NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImagePrototype> imagesRanking = imagesRecognizer.classify(imagePrototype, true);

        SymbolPointsPrototype strokesPrototype = new SymbolPointsPrototype(null, strokes);
        NearestNeighbourClassesRanking<AgnosticSymbol, SymbolPointsPrototype> pointsRanking = strokesRecognizer.classify(strokesPrototype, true);

        // Now, for each returned class combine results
        // insert results from images
        HashMap<AgnosticSymbol, Double> classesProbabilities = new HashMap<>();
        for (RankingItem<AgnosticSymbol> rankingItem: imagesRanking.getRankingItems()) {
            classesProbabilities.put(rankingItem.getClassType(), rankingItem.getMeasure());
        }
        // insert results from strokes
        for (RankingItem<AgnosticSymbol> rankingItem: pointsRanking.getRankingItems()) {
            Double prevValue = classesProbabilities.get(rankingItem.getClassType());
            if (prevValue == null) {
                // just insert new value because we don't have images data
                classesProbabilities.put(rankingItem.getClassType(), rankingItem.getMeasure());
            } else {
                double combinedValue = prevValue * IMAGES_CLASSIFIER_WEIGHT + (1.0 - IMAGES_CLASSIFIER_WEIGHT) * rankingItem.getMeasure();
                classesProbabilities.put(rankingItem.getClassType(), combinedValue);
            }
        }

        TreeSet<RankingItem<AgnosticSymbol>> newRanking = new TreeSet<>();
        for (Map.Entry<AgnosticSymbol, Double> entry: classesProbabilities.entrySet()) {
            newRanking.add(new RankingItem<>(entry.getKey(), entry.getValue()));
        }

        // now put in a ranking - reverse traversal because treeset RankingItem orders ascending
        LinkedList<RankingItem<AgnosticSymbol>> rankingItemLinkedList = new LinkedList<>();
        for (RankingItem<AgnosticSymbol> rankingItem: newRanking) {
            rankingItemLinkedList.add(0, rankingItem);
        }
        return new NearestNeighbourClassesRanking<>(rankingItemLinkedList);

    }

    @Override
    public int getTrainingSetSize() {
        return this.trainingSet.size();
    }

    public void trainFromFile(File trainingFile) throws IM3Exception, IOException {
        trainingSet = new LinkedList<>();
        loadTrainingFile(trainingFile);
        imagesRecognizer.traingFromBimodalDataset(trainingSet);
        strokesRecognizer.traingFromBimodalDataset(trainingSet);
    }
    public void trainFromFolder(File trainingDataFolder) throws IM3Exception, IOException {
        trainingSet = new LinkedList<>();
        ArrayList<File> trainingFiles = new ArrayList<>();
        try {
            FileUtils.readFiles(trainingDataFolder, trainingFiles, "symbolsimages_30x30_strokes.txt", true);
            for (File trainingFile: trainingFiles) {
                loadTrainingFile(trainingFile);
            }
        } catch (IOException e) {
            throw new IM3Exception(e);
        }
        imagesRecognizer.traingFromBimodalDataset(trainingSet);
        strokesRecognizer.traingFromBimodalDataset(trainingSet);
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
                    throw new IOException("Invalid line # " + n  + " , must have 5 components and it has just " + components.length + ", in file " + trainingFile.getName());
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
                                throw new IM3Exception("File " + trainingFile.getName() + ", expected length divisible by 3, and found " + strokeComponents.length + " in line #" + n + " stroke '" + stroke + "'");
                            }
                            for (int i = 0; i < strokeComponents.length; i += 3) {
                                pointsData.addPoint(new Point(Long.parseLong(strokeComponents[i]),
                                        Double.parseDouble(strokeComponents[i + 1]),
                                        Double.parseDouble(strokeComponents[i + 2])));
                            }
                        }
                    }
                    trainingSet.add(new SymbolImageAndPointsPrototype(agnosticSymbol, grayscaleImageData, pointsData));
                }
            }
            n++;
        }
    }

    public List<SymbolImageAndPointsPrototype> getTrainingSet() {
        return trainingSet;
    }
}
