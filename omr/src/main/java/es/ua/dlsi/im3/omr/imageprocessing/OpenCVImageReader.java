package es.ua.dlsi.im3.omr.imageprocessing;

//import org.opencv.core.Mat;
//import org.opencv.imgcodecs.Imgcodecs;

// Removed for avoiding interactions of this OpenCV with Keras in MuRET
/**
 * It loads OpenCV library and process imagesold
 * @autor drizo
 */
public class OpenCVImageReader {
    private static boolean libraryLoaded = false;

   /* public Mat readGrayImage(File imageFile) throws IM3Exception {
        synchronized (OpenCVImageReader.class) {
            if (!libraryLoaded) {
                // OpenCV 3.2 Resources downloaded from http://www.magicandlove.com/blog/2017/03/02/opencv-3-2-java-build/
                //String libraryPath = this.getClass().getResource("/opencv/").getPath();
                //System.out.println(libraryPath);

                nu.pattern.OpenCV.loadShared(); // it loads the native imagesold
                System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);

                //System.setProperty("java.library.path",libraryPath);
                //System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
                libraryLoaded = true;
            }
        }

        Mat mat = Imgcodecs.imread(imageFile.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
        if (mat.empty()) {
            throw new IM3Exception("Empty image: " + imageFile);
        } else {
            Logger.getLogger(OpenCVImageReader.class.getName()).log(Level.INFO, "Image {0} loaded with {1} columns and {2} rows",
                    new Object[] {imageFile, mat.width(), mat.height()});
        }
        return mat;
    }*/
}
