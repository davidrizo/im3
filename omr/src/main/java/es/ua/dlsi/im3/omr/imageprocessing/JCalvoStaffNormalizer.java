package es.ua.dlsi.im3.omr.imageprocessing;

import es.grfia.hmm.score.preprocessing.Pre_Normalization_FivePeaks;
import es.grfia.hmm.score.preprocessing.Pre_Straight_StablePaths;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.utils.CommandLine;
import es.ua.dlsi.im3.core.utils.FileUtils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements the scripts by Jorge Calvo to to the staff normalization
 * @autor drizo
 */
public class JCalvoStaffNormalizer implements IStaffNormalizer {
    @Override
    public void normalize(File inputImageFile, File outputImageFile) throws IM3Exception, IOException {
        OpenCVImageReader imageReader = new OpenCVImageReader();
        Mat imageMat = imageReader.readGrayImage(inputImageFile);

        List<Mat> channels = new LinkedList();
        Core.split(imageMat, channels);
        CLAHE clahe = Imgproc.createCLAHE();
        //Mat destImage = new Mat(buffImage.getHeight(),buffImage.getWidth(), CvType.CV_8UC4);
        Mat destImage = new Mat(imageMat.height(), imageMat.width(), CvType.CV_8UC4);
        clahe.apply(channels.get(0), destImage);
        String preprocessedImagePath = "/tmp/_preproc.jpg";
        String binarizedImagePath = "/tmp/_binarized.jpg";
        Imgcodecs.imwrite(preprocessedImagePath, destImage);

        //	$TOOLS/imgtxtenh/build/imgtxtenh -w 100 -k 0.5 -s 0.25 -u pixels $OUTPUT_DIR/tmp/$file"_preproc"$INPUT_EXT $OUTPUT_DIR/tmp/$file"_"out"_bin.jpg"
        File imgTxtEnhCommandFolder = new File("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/software/normalizacionPentagramasJorge/Tools/imgtxtenh/src/build");
        CommandLine.execShellCommand(imgTxtEnhCommandFolder, "imgtxtenh -u pixels -w 100 -k 0.5 -s 0.25 " + preprocessedImagePath + " " + binarizedImagePath);
        Mat binarizedStaff = imageReader.readGrayImage(new File(binarizedImagePath));

        File tmpFolder = new File("/tmp/Corpus");
        tmpFolder.mkdirs();
        String inputFileNameToStaffDetectionEXEBase = tmpFolder + "/" + FileUtils.getFileWithoutPathOrExtension(inputImageFile.getName()) + "_out_bin";
        String inputFileNameToStaffDetectionEXE = inputFileNameToStaffDetectionEXEBase + ".png";
        Imgproc.threshold(binarizedStaff, binarizedStaff, 128, 255.0, Imgproc.THRESH_BINARY_INV);
        Imgproc.erode(binarizedStaff, binarizedStaff, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(50,1)));
        Imgproc.threshold(binarizedStaff, binarizedStaff, 128, 255, Imgproc.THRESH_BINARY_INV);
        Imgcodecs.imwrite(inputFileNameToStaffDetectionEXE, binarizedStaff);

        // wine $TOOLS/stable/staffDetection.exe $OUTPUT_DIR/tmp/$STAFFTMP/
        File wineCommandFolder = new File("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/software/normalizacionPentagramasJorge/Tools/stable");
        CommandLine.execShellCommand(wineCommandFolder, "wine staffDetection.exe " + tmpFolder.getAbsolutePath());

        Pre_Straight_StablePaths pre_straight_stablePaths = new Pre_Straight_StablePaths();
        String stable_paths_file=inputFileNameToStaffDetectionEXEBase+"-staffLines_vs01.txt";
        Mat mat = pre_straight_stablePaths.run(imageMat, stable_paths_file);
        Pre_Normalization_FivePeaks pre_normalization_fivePeaks = new Pre_Normalization_FivePeaks();
        Mat fivePeaks = pre_normalization_fivePeaks.run(mat);

        Imgcodecs.imwrite(outputImageFile.getAbsolutePath(), fivePeaks);

    }
}
