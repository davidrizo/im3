package es.ua.dlsi.im3.omr.classifiers.segmentation.staffseparation;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.classifiers.segmentation.IDocumentSegmenter;
import es.ua.dlsi.im3.omr.model.entities.Region;
import es.ua.dlsi.im3.omr.model.entities.RegionType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class CalvoDocumentSegmenter implements IDocumentSegmenter {

    private static boolean libraryLoaded = false;
    private final Mat imageMat;

    public CalvoDocumentSegmenter(File imageFile) throws IM3Exception {
        synchronized (PageSplitting.class) {
            if (!libraryLoaded) {
                // OpenCV 3.2 Resources downloaded from http://www.magicandlove.com/blog/2017/03/02/opencv-3-2-java-build/
                //String libraryPath = this.getClass().getResource("/opencv/").getPath();
                //System.out.println(libraryPath);

                nu.pattern.OpenCV.loadShared(); // it loads the native images
                System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);

                //System.setProperty("java.library.path",libraryPath);
                //System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
                libraryLoaded = true;
            }
        }
        
        imageMat = readImage(imageFile.getAbsolutePath());
    }

    private Mat readImage(String filename) throws IM3Exception {
        //drizo OpenCV 2Mat score = Highgui.imread("/home/jcalvo/Escritorio/Tesis/Investigacion/TracedOMR/ISMIR_DB/12612.JPG_1425128911385/12612.JPG",Highgui.IMREAD_GRAYSCALE);
        // drizo OpenCV 3
        //Mat score = Imgcodecs.imread(filename, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat score = Imgcodecs.imread(filename, Imgcodecs.IMREAD_GRAYSCALE);
        if (score.empty()) {
            throw new IM3Exception("Empty image: " + filename);
        } else {
            Logger.getLogger(CalvoDocumentSegmenter.class.getName()).log(Level.INFO, "Image {0} loaded with {1} columns and {2} rows", new Object[] {filename, score.width(), score.height()});
        }
        return score;
    }

    public int findPagesDivisionPoint() throws IM3Exception {
        return findPagesDivisionPoint(imageMat);
    }

    public int findPagesDivisionPoint(Mat score) throws IM3Exception {
        PageSplitting pageSplitting = new PageSplitting();
        int pageDivisionPoint = pageSplitting.run(score);
        return pageDivisionPoint;
    }

    public List<Region> segment() throws IM3Exception {
        return segment(0, imageMat.cols());
    }
    /**
     * @param fromCol Pixel column
     * @param toCol Pixel column
     * @return
     * @throws IM3Exception
     */
    public List<Region> segment(int fromCol, int toCol) throws IM3Exception {
        // first split into images
        List<Region> result = new ArrayList<>();
        fillRegions(result, imageMat, fromCol, toCol);
        return result;
    }

    int i=0;
    /**
     *
     * @param result
     * @param score
     * @param fromCol
     * @param toCol Not incuded
     */
    private void fillRegions(List<Region> result, Mat score, int fromCol, int toCol) throws IM3Exception {
        StaffSplitting staffSplitting = new StaffSplitting();
        Mat subscore = score.colRange(fromCol, toCol);
        List<Integer> divisionPoints = staffSplitting.run(score);

        int lastDivisionPoint = 0;
        for (Integer divisionPoint: divisionPoints) {
            Region region = new Region(RegionType.staff, fromCol, lastDivisionPoint, toCol, divisionPoint);
            result.add(region);
            lastDivisionPoint = divisionPoint;

            /*Mat write = score.rowRange(divisionPoint)
            Imgcodecs.imwrite("/tmp/region_" + i + ".jpg", write)*/
        }
    }

    public static final void main(String [] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Use: CalvoDocumentSegmenter <input image>");
            return;
        }

        CalvoDocumentSegmenter calvoDocumentSegmenter = new CalvoDocumentSegmenter(new File(args[0]));
        calvoDocumentSegmenter.segment();
    }
}
