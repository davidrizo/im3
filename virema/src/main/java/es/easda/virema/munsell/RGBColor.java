package es.easda.virema.munsell;

import javafx.scene.paint.Color;

import java.util.Objects;

public class RGBColor implements Comparable<RGBColor> {
    int r;
    int g;
    int b;

    public RGBColor(int bufferedImagePixelColor) {
        r   = (bufferedImagePixelColor & 0x00ff0000) >> 16;
        g = (bufferedImagePixelColor & 0x0000ff00) >> 8;
        b =  bufferedImagePixelColor & 0x000000ff;
    }
    public RGBColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RGBColor rgbColor = (RGBColor) o;
        return r == rgbColor.r &&
                g == rgbColor.g &&
                b == rgbColor.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }

    @Override
    public int compareTo(RGBColor o) {
        int diff = r - o.r;
        if (diff == 0) {
            diff = g - o.g;
            if (diff == 0) {
                diff = b - o.b;
            }
        }
        return diff;
    }

    public Color toColor() {
        return Color.rgb(r, g, b);
    }

    @Override
    public String toString() {
        return "RGBColor{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                '}';
    }
}
