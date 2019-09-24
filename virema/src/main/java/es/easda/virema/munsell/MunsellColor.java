package es.easda.virema.munsell;

import javafx.scene.paint.Color;

import java.util.Objects;

/**
 * Data loaded from www.cis.rit.edu/research/mcsl/online/munsell.php
 */
public class MunsellColor implements Comparable<MunsellColor> {
    /**
     * Hue name, e.g. 10RP
     */
    String hName;

    double hDegrees;

    int v;
    int c;

    /**
     * From -1 to 1, sRGB
     */
    double r;
    /**
     * From -1 to 1, sRGB
     */
    double g;
    /**
     * From -1 to 1, SRGB
     */
    double b;
    /**
     * From 0 to 255, sRGB
     */
    int dr;
    /**
     * From 0 to 255, sRGB
     */
    int dg;
    /**
     * From 0 to 255, SRGB
     */
    int db;


    public String gethName() {
        return hName;
    }

    public void sethName(String hName) {
        this.hName = hName;
    }

    public double gethDegrees() {
        return hDegrees;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public int getDr() {
        return dr;
    }

    public void setDr(int dr) {
        this.dr = dr;
    }

    public int getDg() {
        return dg;
    }

    public void setDg(int dg) {
        this.dg = dg;
    }

    public int getDb() {
        return db;
    }

    public void setDb(int db) {
        this.db = db;
    }

    public void sethDegrees(double hDegrees) {
        this.hDegrees = hDegrees;
    }


    public Color toColor() {
        try {
            return Color.rgb(this.dr, this.dg, this.db);
        } catch (Throwable t) {
            return Color.WHITE;
        }
    }

    // see eq. (1) at page 9 of Stephen Westland et al. (2007). Colour: Design & Creativity, 1–15.
    public double computeBalanceWith(MunsellColor other) {
        return (double)v*c / (double)(other.v * other.c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MunsellColor that = (MunsellColor) o;
        return Double.compare(that.hDegrees, hDegrees) == 0 &&
                v == that.v &&
                c == that.c;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hDegrees, v, c);
    }

    @Override
    public int compareTo(MunsellColor o) {
        if (hDegrees < o.hDegrees) {
            return -1;
        } else if (hDegrees > o.hDegrees) {
            return 1;
        } else {
            int diff = c - o.c;
            if (diff == 0) {
                diff = v - o.v;
            }
            return diff;
        }
    }

    public double getDistance(int r, int g, int b) {
        return Math.sqrt((this.dr - r)*(this.dr - r)
                + (this.dg - g)*(this.dg - g)
                + (this.db - b)*(this.db - b));
    }
}
