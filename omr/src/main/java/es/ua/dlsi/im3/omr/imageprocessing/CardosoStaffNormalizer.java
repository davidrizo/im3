package es.ua.dlsi.im3.omr.imageprocessing;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.utils.CommandLine;
import es.ua.dlsi.im3.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Invokes the Carlos Pérez implementation if Cardoso 2009 staff normalizer at https://github.com/cperezs/staff-detection
 * @autor drizo
 */
public class CardosoStaffNormalizer implements IStaffNormalizer {
    @Override
    public void normalize(File inputImageFile, File outputImageFile) throws IM3Exception {
        //TODO
        File workingFolder = new File("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/software/normalizacionPentagramasCarlos/staff-detection-develop");
        //File input = new File(workingFolder, "input.jpg");
        //input.delete();
        //FileUtils.copy(inputImageFile, input);

        CommandLine.execShellCommand(workingFolder, "normalize.sh " + inputImageFile.getAbsolutePath() + " " + outputImageFile.getAbsolutePath());

        //File output = new File(workingFolder, "output.jpg");
        //TODO Png, jpg...
        //outputImageFile.delete();
        //FileUtils.copy(output, outputImageFile);
    }
}
