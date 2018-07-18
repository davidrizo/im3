package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassesRanking;
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
import java.util.List;
import java.util.TreeSet;

/**
 * This classifier returns the exact symbol most similar to this one in the training dataset, with its position included
 * It is trained from a list of folders contained files with tagged images (extension symbolsimages.txt-see HISPAMUS/trainingsets/catedral_zaragoza/staves_symbols_images)
 * @autor drizo
 */
public class NearestNeighbourSymbolFromStrokesRecognizer extends NearestNeighbourClassifier<AgnosticSymbol, SymbolPointsPrototype>  implements ISymbolFromStrokesRecognizer {
    private final AgnosticVersion agnosticVersion;

    public NearestNeighbourSymbolFromStrokesRecognizer(AgnosticVersion agnosticVersion)  {
        this.agnosticVersion = agnosticVersion;
    }

    @Override
    public NearestNeighbourClassesRanking<AgnosticSymbol, SymbolPointsPrototype> recognize(Strokes strokes) throws IM3Exception {
        SymbolPointsPrototype prototype = new SymbolPointsPrototype(null, strokes);
        return this.classify(prototype, true);
    }

    public void traingFromBimodalDataset(List<SymbolImageAndPointsPrototype> prototypeList) throws IM3Exception, IOException {
        for (SymbolImageAndPointsPrototype symbolImageAndPointsPrototype: prototypeList) {
            if (symbolImageAndPointsPrototype.getPointsData() != null && !symbolImageAndPointsPrototype.getPointsData().isEmpty()) {
                SymbolPointsPrototype prototype = new SymbolPointsPrototype(symbolImageAndPointsPrototype.getPrototypeClass(), symbolImageAndPointsPrototype.getPointsData());
                addPrototype(prototype);
            }
        }
    }

}
