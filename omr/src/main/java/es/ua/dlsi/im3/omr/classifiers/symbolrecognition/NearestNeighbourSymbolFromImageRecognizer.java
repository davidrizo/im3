package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassifier;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.model.entities.Symbol;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This classifier does not recognizes position in staff, always return L3.
 * It is trained from a list of folders contained files with tagged images (extension symbolsimages.txt-see HISPAMUS/trainingsets/catedral_zaragoza/staves_symbols_images)
 * @autor drizo
 */
public class NearestNeighbourSymbolFromImageRecognizer extends NearestNeighbourClassifier<AgnosticSymbolType, SymbolImagePrototype>  implements ISymbolFromImageDataRecognizer {
    private final File trainingDataFolder;

    public NearestNeighbourSymbolFromImageRecognizer(File trainingDataFolder) throws IM3Exception {
        this.trainingDataFolder = trainingDataFolder;
        train();
    }

    @Override
    public List<AgnosticSymbol> recognize(GrayscaleImageData imageData) throws IM3Exception {
        SymbolImagePrototype prototype = new SymbolImagePrototype(null, imageData);
        List<AgnosticSymbolType> orderedValues = this.classify(prototype);
        List<AgnosticSymbol> result = new LinkedList<>();
        for (AgnosticSymbolType agnosticSymbolType: orderedValues) {
            AgnosticSymbol agnosticSymbol = new AgnosticSymbol(agnosticSymbolType, PositionsInStaff.LINE_3);
            result.add(agnosticSymbol);
        }
        return result;
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

                AgnosticSymbol agnosticSymbol = AgnosticSymbol.parseString(components[3]);
                ArrayList<Integer> pixels = new ArrayList<>();
                String[] pxs = components[4].split(",");
                for (String px : pxs) {
                    pixels.add(Integer.parseInt(px));
                }
                GrayscaleImageData grayscaleImageData = new GrayscaleImageData(pixels);
                this.addPrototype(new SymbolImagePrototype(agnosticSymbol.getSymbol(), grayscaleImageData));
            }
        }
    }
}
