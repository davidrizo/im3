package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassifier;
import es.ua.dlsi.im3.core.patternmatching.RankingItem;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * This classifier returns the exact symbol most similar to this one in the training dataset, with its position included
 * It is trained from a list of folders contained files with tagged images (extension symbolsimages.txt-see HISPAMUS/trainingsets/catedral_zaragoza/staves_symbols_images)
 * @autor drizo
 */
public class NearestNeighbourSymbolFromImageRecognizer extends NearestNeighbourClassifier<AgnosticSymbol, SymbolImagePrototype>  implements ISymbolFromImageDataRecognizer {
    private File trainingDataFolder;

    public NearestNeighbourSymbolFromImageRecognizer()  {
    }

    public NearestNeighbourSymbolFromImageRecognizer(File trainingDataFolder) throws IM3Exception {
        this.trainingDataFolder = trainingDataFolder;
        train();
    }

    @Override
    public TreeSet<RankingItem<SymbolImagePrototype>> recognize(GrayscaleImageData imageData) throws IM3Exception {
        SymbolImagePrototype prototype = new SymbolImagePrototype(null, imageData);
        TreeSet<RankingItem<SymbolImagePrototype>> orderedValues = this.classify(prototype);
        return orderedValues;
    }

    public void trainWithFile(File trainingFile) throws IM3Exception, IOException {
        loadTrainingFile(trainingFile);
    }
    @Override
    protected void train() throws IM3Exception {
        ArrayList<File> trainingFiles = new ArrayList<>();
        try {
            FileUtils.readFiles(trainingDataFolder, trainingFiles, "symbolsimages.txt", true);
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
                String[] components = line.split(";");
                if (components.length != 5) {
                    throw new IOException("Invalid line, must have 5 components and it has just " + components.length);
                }

                AgnosticSymbol agnosticSymbol = AgnosticSymbol.parseAgnosticString(components[3]);
                ArrayList<Integer> pixels = new ArrayList<>();
                String[] pxs = components[4].split(",");
                for (String px : pxs) {
                    pixels.add(Integer.parseInt(px));
                }
                GrayscaleImageData grayscaleImageData = new GrayscaleImageData(pixels);
                this.addPrototype(new SymbolImagePrototype(agnosticSymbol, grayscaleImageData));
            }
        }
    }
}
