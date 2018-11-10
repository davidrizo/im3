package es.ua.dlsi.grfia.im3ws.muret.entity;

public class Scales {
    double x;
    double y;
    double em;

    public Scales(double x, double y, double em) {
        this.x = x;
        this.y = y;
        this.em = em;
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
}
