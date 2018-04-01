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
     * 0 to 1
     */
    float a;

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
}
