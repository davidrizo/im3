package es.easda.virema;

import javafx.scene.paint.Color;

import java.util.Arrays;

public class ColorUtils {

    /**
     * Taken from processing
     * @param value
     * @param inputRangeMin
     * @param inputRangeMax
     * @param outputRangeMin
     * @param outputRangeMax
     * @return
     */
    static final double map(double value,
                            double inputRangeMin, double inputRangeMax,
                            double outputRangeMin, double outputRangeMax) {
        double outgoing =
                outputRangeMin + (outputRangeMax - outputRangeMin) * ((value - inputRangeMin) / (inputRangeMax - inputRangeMin));
        String badness = null;
        if (outgoing != outgoing) {
            throw new RuntimeException("Map returns NaN");

        } else if (outgoing == Float.NEGATIVE_INFINITY ||
                outgoing == Float.POSITIVE_INFINITY) {
            throw new RuntimeException("Map returns infinity");
        }
        return outgoing;
    }

    /**
     *
     * @param L 0..100
     * @param C 0..120
     * @param H 0..400
     * @return
     */
    static Color hcl2RGB(double L, double C, double H) {
        /*Chroma colorGenerator = new Chroma(ColorSpace.LCH, L, C, H);
        double r = colorGenerator.getRGB(Channel.R);
        double g = colorGenerator.getRGB(Channel.G);
        double b = colorGenerator.getRGB(Channel.B);

        if (r<0.0 || r>=255.0 || g<0.0 || g>=255.0 || b<0.0 || b>=255.0) {
            return Color.BLACK;
        } else {
            return new Color(r/255.0, g/255.0, b/255.0, 1.0);
        }*/

        /*int [] rgb = HCL2.hcl2rgb(H, C, L, 0.5);
        return new Color((double)rgb[0]/255.0, (double)rgb[1]/255.0, (double)rgb[2]/255.0,1);*/
        //H = H/360.0 * 400.0;
        //System.out.println(H);
        Colour colour = new Colour(L/100.0, C/120.0, H);
        int [] rgb = colour.getStandardRGB();
        return Color.color(rgb[0]/255.0, rgb[1]/255.0, rgb[2]/255.0);

    }


    public static double foldRGBValue(double value) {
        double valueFolded = Math.min(Math.max(value,0), 255);
        return valueFolded / 255.0;
    }

}
