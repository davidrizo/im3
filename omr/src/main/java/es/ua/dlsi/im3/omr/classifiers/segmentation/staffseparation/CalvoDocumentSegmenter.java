package es.ua.dlsi.im3.omr.classifiers.segmentation.staffseparation;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.model.entities.Region;

import java.io.File;
import java.util.List;

//import org.opencv.core.Mat;
// Removed for avoiding interactions of this OpenCV with Keras in MuRET
/**
 * @autor drizo
 */
public class CalvoDocumentSegmenter { // implements IDocumentSegmenter {

    /*private final Mat imageMat;*/

    public CalvoDocumentSegmenter(File imageFile) throws IM3Exception {
        /*OpenCVImageReader imageReader = new OpenCVImageReader();
        imageMat = imageReader.readGrayImage(imageFile);*/
    }

    public int findPagesDivisionPoint() throws IM3Exception {
        /*return findPagesDivisionPoint(imageMat);*/
        return 0;
    }

    /*public int findPagesDivisionPoint(Mat score) {
        PageSplitting pageSplitting = new PageSplitting();
        int pageDivisionPoint = pageSplitting.run(score);
        return pageDivisionPoint;
    }
*/
    public List<Region> segment() throws IM3Exception {
        /*return segment(0, imageMat.cols());*/
        return null;
    }
    /**
     * @param fromCol Pixel column
     * @param toCol Pixel column
     * @return
     * @throws IM3Exception
     */
   public List<Region> segment(int fromCol, int toCol) throws IM3Exception {
        /*// first split into imagesold
        List<Region> result = new ArrayList<>();
        fillRegions(result, imageMat, fromCol, toCol);
        return result;*/
        return null;
    }

    int i=0;
    /**
     *
     * @param result
     * @param score
     * @param fromCol
     * @param toCol Not incuded
     */
    /*private void fillRegions(List<Region> result, Mat score, int fromCol, int toCol) throws IM3Exception {
        StaffSplitting staffSplitting = new StaffSplitting();
        Mat subscore = score.colRange(fromCol, toCol);
        List<Integer> divisionPoints = staffSplitting.run(score);

        int lastDivisionPoint = 0;
        for (Integer divisionPoint: divisionPoints) {
            Region region = new Region(RegionType.staff, fromCol, lastDivisionPoint, toCol, divisionPoint);
            result.add(region);
            lastDivisionPoint = divisionPoint;

            //Mat write = score.rowRange(divisionPoint)
            //Imgcodecs.imwrite("/tmp/region_" + i + ".jpg", write)
        }
    }*/

    /*public static final void main(String [] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Use: CalvoDocumentSegmenter <input image>");
            return;
        }

        CalvoDocumentSegmenter calvoDocumentSegmenter = new CalvoDocumentSegmenter(new File(args[0]));
        calvoDocumentSegmenter.segment();
    }*/
}
