import org.apache.commons.io.FileUtils;
import org.tensorflow.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Carga un modelo PB generado desde python
 * https://medium.com/google-cloud/how-to-invoke-a-trained-tensorflow-model-from-java-programs-27ed5f4f502d
 * @autor drizo
 */
public class CargaModeloPB {
    public static void printPixelARGB(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        System.out.println("argb: " + alpha + ", " + red + ", " + green + ", " + blue);
    }

    //TODO Comprobarlo en IMCore (y si funciona quitar opencv)
    /**
     * Equivalent to OpenCV cv2.imread
     * @param img
     * @return matrix (width*height) of RGB
     */
    int [][][] cv2imread(BufferedImage img) {
        int [][][] result = new int[img.getWidth()][img.getHeight()][3];
        for (int i=0; i<img.getWidth(); i++) {
            for (int j=0; j<img.getHeight(); j++) { // this is the same order as the opencv
                int pixel = img.getRGB(i, j);

                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                result[i][j][0] = red;
                result[i][j][1] = green;
                result[i][j][2] = blue;
            }
        }
        return result;
    }

    /**
     * Equivalent to OpenCV cv2.imread
     * @param img
     * @return matrix (width*height) of grayscale
     */
    byte [][] cv2imreadGray(BufferedImage img) {
        byte [][] result = new byte[img.getWidth()][img.getHeight()];
        for (int i=0; i<img.getWidth(); i++) {
            for (int j=0; j<img.getHeight(); j++) { // this is the same order as the opencv
                int rgb = img.getRGB(i, j);

                int gray = rgb & 0xFF;

                result[i][j] = (byte) gray;
            }
        }
        return result;
    }
    public static final void main(String [] args) throws IOException {
        /*for (int i=0; i<img.getWidth(); i++) {
            for (int j=0; j<img.getHeight(); j++) { // this is the same order as the opencv
                int pixel = img.getRGB(j, i);
                printPixelARGB(pixel);
            }
            return;
        }
        if (true) {
            return;
        }*/

        CargaModeloPB cargaModeloPB = new CargaModeloPB();
        cargaModeloPB.run();
    }

    private static float[][][][] convertImageToArray(BufferedImage bf) {
        int width = bf.getWidth();
        int height = bf.getHeight();
        int[] data = new int[width * height];
        bf.getRGB(0, 0, width, height, data, 0, width);
        float[][][][] rgbArray = new float[1][1][height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rgbArray[0][0][i][j] = data[i * width + j];
            }
        }
        return rgbArray;
    }

    private void run() throws IOException {
        // load image
        BufferedImage img = ImageIO.read(new File("/Users/drizo/Documents/investigacion/mensural/OMR-jorge/00531.JPG"));

        byte [][] image_np = cv2imreadGray(img);

        //float[][][][] image_np = convertImageToArray(img);


        String pb = "/Users/drizo/Documents/investigacion/mensural/OMR-jorge/frozen_inference_graph.pb";
        Graph graph = new Graph();
        graph.importGraphDef(FileUtils.readFileToByteArray(new File(pb)));
        System.out.println("Graph: " + graph.toString());

        Session s = new Session(graph);

        String [] keys = {"num_detections", "detection_boxes", "detection_scores",
                "detection_classes", "detection_masks"};

        HashMap<String, ?> tensor_dict = new HashMap<>();

        Session.Runner runner = s.runner();

        for (String key: keys) {
            Operation operation = graph.operation(key);
            if (operation == null) {
                System.out.println("Not found: " + key);
            } else {
                System.out.println("Op -> " + operation.toString());
                System.out.println("\tType: " + operation.type());
                System.out.println("\tOutputs: " + operation.numOutputs());

                runner = runner.fetch(key);
                System.out.println("Runner: " + runner);
                if (key.equals("detection_masks")) {
                    // The following processing is only for single image
                    //detection_boxes = tf.squeeze(tensor_dict['detection_boxes'], [0])
                }
            }
        }

        String imageData = "image_tensor";
        runner = runner.feed(imageData, Tensor.create(image_np));
        List<Tensor<?>> output = runner.run();
        for (Tensor t: output) {
            System.out.println(t.toString());
        }
        //image_tensor = tf.get_default_graph().get_tensor_by_name('image_tensor:0')


        // Construct the computation graph with a single operation, a constant
        // named "MyConst" with a value "value".
        final String value = "Hello from " + TensorFlow.version();

        try (Tensor t = Tensor.create(value.getBytes("UTF-8"))) {
            // The Java API doesn't yet include convenience functions for adding operations.
            graph.opBuilder("Const", "MyConst").setAttr("dtype", t.dataType()).setAttr("value", t).build();
        }

        //Tensor output = s.runner().fetch("MyConst").run().get(0)) {

    }
}
