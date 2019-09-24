package es.easda.virema.HCL;

import es.easda.virema.HCL1;
import javafx.scene.paint.Color;

public class HCLColor {
    public static final int MAX_C = 140;
    public static final int MAX_L = 100;

    double h; // [0..360[
    double c; // [0..120
    double l; // [0..100[

    public HCLColor(double h, double c, double l) {
        this.h = h;
        this.c = c;
        this.l = l;
    }

    public double getH() {
        return h;
    }

    public double getC() {
        return c;
    }

    public double getL() {
        return l;
    }

    public Color toColor() {
        double [] rgb = hcl2rgb(h, c, l);
        try {
            return Color.rgb((int) (rgb[0] * 255.0), (int) (rgb[1] * 255.0), (int) (rgb[2] * 255.0));
        } catch (Throwable t) {
            return Color.WHITE;
        }
    }

    // https://github.com/bedatadriven/renjin-statet/blob/master/org.renjin.core/src/org/renjin/primitives/graphics/RgbHsv.java
    private final static double DEG2RAD = 0.01745329251994329576;
    private final static double WHITE_X = 95.047;
    private final static double WHITE_Y = 100.000;
    private final static double WHITE_Z = 108.883;
    private final static double WHITE_u = 0.1978398;
    private final static double WHITE_v = 0.4683363;
    private final static double GAMMA = 2.4;

    private static double[] hcl2rgb(double h, double c, double l) {
        double L, U, V;
        double u, v;
        double X, Y, Z;
        double R, G, B;

        /* Step 1 : Convert to CIE-LUV */

        h = DEG2RAD * h;
        L = l;
        U = c * Math.cos(h);
        V = c * Math.sin(h);

        /* Step 2 : Convert to CIE-XYZ */

        if (L <= 0 && U == 0 && V == 0) {
            X = 0;
            Y = 0;
            Z = 0;
        } else {
            Y = WHITE_Y
                    * ((L > 7.999592) ? Math.pow((L + 16) / 116, 3) : L / 903.3);
            u = U / (13 * L) + WHITE_u;
            v = V / (13 * L) + WHITE_v;
            X = 9.0 * Y * u / (4 * v);
            Z = -X / 3 - 5 * Y + 3 * Y / v;
        }

        /* Step 4 : CIE-XYZ to sRGB */

        R = gtrans((3.240479 * X - 1.537150 * Y - 0.498535 * Z) / WHITE_Y);
        G = gtrans((-0.969256 * X + 1.875992 * Y + 0.041556 * Z) / WHITE_Y);
        B = gtrans((0.055648 * X - 0.204043 * Y + 1.057311 * Z) / WHITE_Y);

        return (new double[] { R, G, B });
    }
    private static double gtrans(double u) {
        if (u > 0.00304) {
            return 1.055 * Math.pow(u, (1 / GAMMA)) - 0.055;
        } else {
            return 12.92 * u;
        }
    }

    double[] toHCL() {
        return new double[] {h, c, l};
    }
    public double computeDistance(HCLColor other) {
        HCL1 hcl1 = new HCL1();
        return hcl1.distance_hcl(toHCL(), other.toHCL());
    }
}
