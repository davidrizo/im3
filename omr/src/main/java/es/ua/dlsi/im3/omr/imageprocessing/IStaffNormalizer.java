package es.ua.dlsi.im3.omr.imageprocessing;

import es.ua.dlsi.im3.core.IM3Exception;

import java.io.File;
import java.io.IOException;

public interface IStaffNormalizer {
    void normalize(File inputImageFile, File outputImageFile) throws IM3Exception, IOException;

}
