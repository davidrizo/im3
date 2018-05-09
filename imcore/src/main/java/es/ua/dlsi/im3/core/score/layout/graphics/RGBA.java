package es.ua.dlsi.im3.core.score.layout.graphics;

/**
 * Red, green, blue, alpha
 * @autor drizo
 */
public class RGBA {
    /**
     * 0 to 1
     */
    float r;
    /**
     * 0 to 1
     */
    float g;
    /**
     * 0 to 1
     */
    float b;
    /**
     * 0 to 1, 1 means visible
     */
    float a;

    /**
     *
     * @param r
     * @param g
     * @param b
     * @param a 1 means visible, 0 transparent
     */
    public RGBA(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public float getA() {
        return a;
    }

    @Override
    public String toString() {
        return "RGBA{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                ", a=" + a +
                '}';
    }

    /**
     * Valid in SVG
     * @return
     */
    public String getHexadecimalString() {
        return String.format("#%02x%02x%02x%02x", (int)r*255, (int)g*255, (int)b*255, (int)a*255);
    }
}
