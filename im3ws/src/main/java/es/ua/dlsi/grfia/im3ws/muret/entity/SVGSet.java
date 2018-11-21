package es.ua.dlsi.grfia.im3ws.muret.entity;

import java.util.Map;

public class SVGSet {
    double x;
    double y;
    double em;

    /**
     * key = AgnosticTypeString
     * value = SVG d param of SVG path element
     */
    Map<String, String> paths;

    public SVGSet(double x, double y, double em, Map<String, String> paths) {
        this.x = x;
        this.y = y;
        this.em = em;
        this.paths = paths;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getEm() {
        return em;
    }

    public Map<String, String> getPaths() {
        return paths;
    }
}
