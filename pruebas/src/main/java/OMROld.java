import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * @autor drizo
 */
public class OMROld {
    public static void main() {
        SavedModelBundle smb = SavedModelBundle.load("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/software/CRNN-antiguo/OMR-CRNN/Models", "serve");
        Session s = smb.session();
        FloatBuffer fb = FloatBuffer.allocate(784);
        byte [] imgData = null; //TODO leer imagen aqui
        for (byte b : imgData) {
            fb.put((float)(b & 0xFF)/255.0f); // normalize image pixels to [-0.5, 0.5]
        }
        fb.rewind();

        float [] keep_prob_arr = new float[1024];
        Arrays.fill(keep_prob_arr, 1f);

        Tensor inputTensor = Tensor.create(new long[] {784}, fb); // tf.placeholder (tf.float32, shape [None, 784], name = input_tensor
        Tensor keep_prob = Tensor.create(new long[] {1, 1024}, FloatBuffer.wrap(keep_prob_arr)); // tf.placeholder (tf.float32, name="keep_prob"

        Tensor result = s.runner()
                .feed("input_tensor", inputTensor)
                .feed("keep_prob", keep_prob)
                .fetch("ouput_tensor") // y_conv = tf.identity(y_conv, name="output_tensor"
                .run().get(0);

        float [][] m = new float[1][10];
        m[0] = new float[10];
        Arrays.fill(m[0], 0);

        float[][] matrix = (float[][]) result.copyTo(m);
        float maxVal = 0;
        int inc = 0;
        int predict = -1;
        for (float val : matrix[0]) {
            if (val > maxVal) {
                predict = inc;
                maxVal = val;
            }
            inc++;
        }
        System.out.println("Preduction = " + predict);

    }
}
