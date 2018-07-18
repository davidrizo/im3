package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassesRanking;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassifier;
import es.ua.dlsi.im3.core.patternmatching.RankingItem;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * This classifier returns the exact symbol most similar to this one in the training dataset, with its position included
 * It is trained from a list of folders contained files with tagged images (extension symbolsimages.txt-see HISPAMUS/trainingsets/catedral_zaragoza/staves_symbols_images)
 * @autor drizo
 */
public class NearestNeighbourSymbolFromImageRecognizer extends NearestNeighbourClassifier<AgnosticSymbol, SymbolImagePrototype>  implements ISymbolFromImageDataRecognizer {
    private final AgnosticVersion agnosticVersion;

    public NearestNeighbourSymbolFromImageRecognizer(AgnosticVersion agnosticVersion)  {
        this.agnosticVersion = agnosticVersion;
    }

    public NearestNeighbourSymbolFromImageRecognizer(AgnosticVersion agnosticVersion, File trainingDataFolder) throws IM3Exception {
        this.agnosticVersion = agnosticVersion;
        trainFromFolder(trainingDataFolder);
    }

    @Override
    public NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImagePrototype> recognize(GrayscaleImageData imageData) throws IM3Exception {
        SymbolImagePrototype prototype = new SymbolImagePrototype(null, imageData);
        return this.classify(prototype, true);
    }

    public void trainFromFile(File trainingFile) throws IM3Exception, IOException {
        loadTrainingFile(trainingFile);
    }

    public void trainFromFolder(File trainingDataFolder) throws IM3Exception {
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
                String[] components = line.split(";");
                if (components.length != 5) {
                    throw new IOException("Invalid line, must have 5 components and it has just " + components.length);
                }

                AgnosticSymbol agnosticSymbol = AgnosticSymbol.parseAgnosticString(agnosticVersion, components[3]);
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

    public void traingFromBimodalDataset(List<SymbolImageAndPointsPrototype> prototypeList) throws IM3Exception, IOException {
        for (SymbolImageAndPointsPrototype symbolImageAndPointsPrototype: prototypeList) {
            SymbolImagePrototype prototype = new SymbolImagePrototype(symbolImageAndPointsPrototype.getPrototypeClass(), symbolImageAndPointsPrototype.getImageData());
            addPrototype(prototype);
        }
    }

}
