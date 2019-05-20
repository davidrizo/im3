package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.kern.HumdrumMatrix;
import es.ua.dlsi.im3.core.score.io.kern.HumdrumMatrixItem;
import es.ua.dlsi.im3.core.score.io.kern.MensImporter;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;
import es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.*;

import java.io.File;

public class MensSemanticImporter implements ISemanticImporter {

    @Override
    public SemanticEncoding importString(NotationType notationType, String string) throws IM3Exception {
        MensImporter mensImporter = new MensImporter();
        HumdrumMatrix humdrumMatrix = mensImporter.importMens(string);
        return humdrumMatrix2SemanticEncoding(notationType, humdrumMatrix);
    }

    @Override
    public SemanticEncoding importFile(NotationType notationType, File file) throws IM3Exception {
        MensImporter mensImporter = new MensImporter();
        HumdrumMatrix humdrumMatrix = mensImporter.importMens(file);
        return humdrumMatrix2SemanticEncoding(notationType, humdrumMatrix);
    }

    //TODO See HumdrumMatrix2ScoreSong (it contains several spines - poly) - we should create first a poly semantic encoding
    private SemanticEncoding humdrumMatrix2SemanticEncoding(NotationType notationType, HumdrumMatrix humdrumMatrix) throws IM3Exception {
        SemanticEncoding semanticEncoding = new SemanticEncoding();

        for (int row=0; row<humdrumMatrix.getRowCount(); row++) {
            if (humdrumMatrix.getSpineCount(row) != 1) {
                throw new ImportException("Currently only monodies are supported");
            }
            //for (int spine=0; spine < humdrumMatrix.getSpineCount(row); spine++) {
            int spine = 0;
            HumdrumMatrixItem item = humdrumMatrix.get(row, spine);
            if (item.getHumdrumEncoding().equals(".")) {
                    // continuation, skip
            } else if (item.getParsedObject() instanceof Clef) { // TODO algo más elegante
                semanticEncoding.add(new SemanticClef((Clef) item.getParsedObject()));
            } else if (item.getParsedObject() instanceof Key) {
                KeySignature keySignature = new KeySignature(notationType, (Key) item.getParsedObject());
                semanticEncoding.add(new SemanticKeySignature(keySignature));
            } else if (item.getParsedObject() instanceof FractionalTimeSignature) {
                semanticEncoding.add(new SemanticFractionalTimeSignature((FractionalTimeSignature) item.getParsedObject()));
            } else if (item.getParsedObject() instanceof SignTimeSignature) {
                semanticEncoding.add(new SemanticMeterSignTimeSignature((SignTimeSignature) item.getParsedObject()));
            } else if (item.getParsedObject() instanceof MarkBarline) {
                semanticEncoding.add(new SemanticBarline());
            } else if (item.getParsedObject() instanceof SimpleNote) {
                semanticEncoding.add(new SemanticNote((SimpleNote) item.getParsedObject()));
            } else if (item.getParsedObject() instanceof SimpleRest) {
                semanticEncoding.add(new SemanticRest((SimpleRest) item.getParsedObject()));
            } else if (item.getParsedObject() instanceof SimpleMultiMeasureRest) {
                semanticEncoding.add(new SemanticMultirest((SimpleMultiMeasureRest) item.getParsedObject()));
            } else if (!item.getHumdrumEncoding().equals("!")) {
                //TODO Acabar
                System.err.println("TO-DO: " + item.getHumdrumEncoding());
            }
        }

        return semanticEncoding;
    }


}
