package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.utils.CommandLine;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO Generalizarlo -> Plugins
public class DLSymbolAndPositionClassifier {

    File commandFolder;
    public DLSymbolAndPositionClassifier(File commandFolder) {
        this.commandFolder = commandFolder;
    }

    public AgnosticSymbol recognize(File inputImage, BoundingBox boundingBox) throws IM3Exception {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Recognizing symbol in image {0} in bounding box {1}",
        new Object[] {inputImage.getAbsolutePath(), boundingBox.toString()});
        String output = CommandLine.execShellCommand(commandFolder, "./predict.sh"
                + " " + inputImage.getAbsolutePath()
                + " " + (int)Math.floor(boundingBox.getFromX())
                + " " + (int)Math.floor(boundingBox.getFromY())
                + " " + (int)Math.ceil(boundingBox.getToX())
                + " " + (int)Math.ceil(boundingBox.getToY())
        ).trim();

        AgnosticSymbol agnosticSymbol = AgnosticSymbol.parseAgnosticString(AgnosticVersion.v2, output);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Recognized {0} from {1}", new Object[]{
                output, agnosticSymbol
        });
        return agnosticSymbol;
    }

}
