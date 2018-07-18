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
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This classifier uses the method explained in ISMIR'2016 Two heads are better than one Calvo, Rizo & Iñesta paper.
 * This version does not stores all the training set distances (as BimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer), but only the best one
 * in each moment.
 */
public class FastBimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer implements IBimodalSymbolFromImageDataAndStrokesRecognizer {
    private final AgnosticVersion agnosticVersion;
    HashMap<AgnosticSymbol, Double> bestPointsDistanceForClass;
    LinkedList<SymbolImagePrototype> imagesTrainingSet;
    LinkedList<SymbolPointsPrototype> strokesTrainingSet;
    public static double IMAGES_CLASSIFIER_WEIGHT = 0.7;
    double imagesClassifierWeight;

    public FastBimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer(AgnosticVersion agnosticVersion, double imagesClassifierWeight)  {
        this.agnosticVersion = agnosticVersion;
        this.imagesClassifierWeight = imagesClassifierWeight;
    }

    public FastBimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer(AgnosticVersion agnosticVersion)  {
        this(agnosticVersion, IMAGES_CLASSIFIER_WEIGHT);
    }

    /**
     *
     * @param imageData
     * @return Normalized probabilities
     * @throws IM3Exception
     */
    private HashMap<AgnosticSymbol, Double> classify(GrayscaleImageData imageData) throws IM3Exception {
        HashMap<AgnosticSymbol, Double> bestImagesDistanceForClass = new HashMap<>();

        SymbolImagePrototype query = new SymbolImagePrototype(null, imageData);
        int n=0;
        for (SymbolImagePrototype prototype: imagesTrainingSet) {
            Double bestDistanceForClass = bestImagesDistanceForClass.get(prototype.getPrototypeClass());
            if (bestDistanceForClass == null) {
                bestDistanceForClass = Double.MAX_VALUE;
            }
            Double distance = prototype.computeDistance(query, bestDistanceForClass);
            if (distance != null && distance < bestDistanceForClass) {
                bestImagesDistanceForClass.put(prototype.getPrototypeClass(), distance);
            }

            if (n%1000 == 0) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "{0} image samples compared", n);
            }
            n++;
        }

        // now replace distances for pseudo-probabilities
        double sum = 0;
        for (Map.Entry<AgnosticSymbol, Double> entry: bestImagesDistanceForClass.entrySet()) {
            double distance = entry.getValue();
            double p;
            if (distance == 0) {
                p = Double.MAX_VALUE;
            } else {
                p = 1.0 / distance;
            }
        }

        // now normalize
        for (Map.Entry<AgnosticSymbol, Double> entry: bestImagesDistanceForClass.entrySet()) {
            entry.setValue(entry.getValue() / sum);
        }

        return bestImagesDistanceForClass;
    }

    /**
     *
     * @param strokes
     * @return Normalized probabilities
     * @throws IM3Exception
     */
    private HashMap<AgnosticSymbol, Double> classify(Strokes strokes) {
        HashMap<AgnosticSymbol, Integer> bestPointsDistancesForClasses = new HashMap<>();

        SymbolPointsPrototype query = new SymbolPointsPrototype(null, strokes);
        int n=0;
        
        for (SymbolPointsPrototype prototype: strokesTrainingSet) {
            Integer bestDistanceForClass = bestPointsDistancesForClasses.get(prototype.getPrototypeClass());
            if (bestDistanceForClass == null) {
                bestDistanceForClass = Integer.MAX_VALUE;
            }
            //Double distance = prototype.computeDistance(query);
            //TODO Comprobar esto
            LevenshteinDistance levenshteinDistance = new LevenshteinDistance(bestDistanceForClass);
            Integer distance = levenshteinDistance.apply(prototype.getFCCCharSequence(), query.getFCCCharSequence());

            if (distance != null && distance < bestDistanceForClass) {
                bestPointsDistancesForClasses.put(prototype.getPrototypeClass(), distance);
            }
            if (n%1000 == 0) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "{0} strokes samples compared", n);
            }
            n++;

        }

        // now replace distances for pseudo-probabilities
        double sum = 0;
        for (Map.Entry<AgnosticSymbol, Integer> entry: bestPointsDistancesForClasses.entrySet()) {
            double distance = entry.getValue();
            double p;
            if (distance == 0) {
                p = Double.MAX_VALUE;
            } else {
                p = 1.0 / (double) distance;
            }
        }

        // now normalize
        HashMap<AgnosticSymbol, Double> result = new HashMap<>();
        for (Map.Entry<AgnosticSymbol, Integer> entry: bestPointsDistancesForClasses.entrySet()) {
            result.put(entry.getKey(), (double)entry.getValue() / sum);
        }

        return result;
    }

    /**
     * It just returns the best one
     * @param imageData
     * @param strokes
     * @return
     * @throws IM3Exception
     */
    @Override
    public NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImageAndPointsPrototype> recognize(GrayscaleImageData imageData, Strokes strokes) throws IM3Exception {
        HashMap<AgnosticSymbol, Double> bestPointsProbabilitiesForClasses = classify(strokes);
        HashMap<AgnosticSymbol, Double> bestImagesProbabilitiesForClass = classify(imageData);
        HashMap<AgnosticSymbol, Double> mergedProbabilites = new HashMap<>();
        // now for each class, merge values
        for (Map.Entry<AgnosticSymbol, Double> entry: bestImagesProbabilitiesForClass.entrySet()) {
            Double pointsP = bestPointsProbabilitiesForClasses.get(entry.getKey());
            double mergedP;
            if (pointsP == null) {
                mergedP = entry.getValue();
            } else {
                mergedP = entry.getValue() * imagesClassifierWeight * (1.0 - imagesClassifierWeight) * pointsP;
            }
            mergedProbabilites.put(entry.getKey(), mergedP);
        }

        // now, if some symbol is not found in images, set the points probabilites
        for (Map.Entry<AgnosticSymbol, Double> entry: bestPointsProbabilitiesForClasses.entrySet()) {
            if (!mergedProbabilites.containsKey(entry.getKey())) {
                mergedProbabilites.put(entry.getKey(), entry.getValue());
            }
        }

        LinkedList<RankingItem<AgnosticSymbol>> resultList = new LinkedList<>();
        for (Map.Entry<AgnosticSymbol, Double> entry: mergedProbabilites.entrySet()) {
            resultList.add(new RankingItem<>(entry.getKey(), entry.getValue()));
        }

        NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImageAndPointsPrototype> result = new NearestNeighbourClassesRanking<>(resultList);
        return result;

    }

    @Override
    public int getTrainingSetSize() {
        return this.imagesTrainingSet.size();
    }

    private void initTrainingSets() {
        imagesTrainingSet = new LinkedList<>();
        strokesTrainingSet = new LinkedList<>();
    }

    public void trainFromFile(File trainingFile) throws IM3Exception, IOException {
        initTrainingSets();
        loadTrainingFile(trainingFile);
    }

    public void trainFromFolder(File trainingDataFolder) throws IM3Exception {
        initTrainingSets();
        ArrayList<File> trainingFiles = new ArrayList<>();
        try {
            FileUtils.readFiles(trainingDataFolder, trainingFiles, "symbolsimages_30x30_strokes.txt", true);
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
                    throw new IOException("Invalid line # " + n  + " , must have 5 components and it has just " + components.length + ", in file " + trainingFile.getName());
                } else {
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
                    imagesTrainingSet.add(new SymbolImagePrototype(agnosticSymbol, grayscaleImageData));


                    PointsData pointsData;
                    if (components.length == 6 && !components[5].trim().isEmpty()) {
                        String[] strokes = components[5].split(XML2AgnosticSymbolImagesStrokesTextFile.STROKES_SEPARATOR);
                        pointsData = new PointsData();
                        for (String stroke : strokes) {
                            String[] strokeComponents = stroke.split(XML2AgnosticSymbolImagesStrokesTextFile.COMMA);
                            if (strokeComponents.length % 3 != 0) {
                                //throw new IM3Exception("File " + trainingFile.getName() + ", expected length divisible by 3, and found " + strokeComponents.length + " in line #" + n + " stroke '" + stroke + "'");
                                //TODO
                                System.err.println("File " + trainingFile.getName() + ", expected length divisible by 3, and found " + strokeComponents.length + " in line #" + n + " stroke '" + stroke + "'");
                            } else {
                                for (int i = 0; i < strokeComponents.length; i += 3) {
                                    pointsData.addPoint(new Point(Long.parseLong(strokeComponents[i]),
                                            Double.parseDouble(strokeComponents[i + 1]),
                                            Double.parseDouble(strokeComponents[i + 2])));
                                }
                            }
                        }
                        if (pointsData != null && !pointsData.isEmpty()) {
                            strokesTrainingSet.add(new SymbolPointsPrototype(agnosticSymbol, pointsData));
                        }
                    }
                }
            }
            n++;
        }
    }
}
