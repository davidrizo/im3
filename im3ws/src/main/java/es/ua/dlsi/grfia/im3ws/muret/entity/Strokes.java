package es.ua.dlsi.grfia.im3ws.muret.entity;

import java.util.List;

public abstract class Strokes {
    public abstract List<? extends Stroke> getStrokes();
    public abstract String toDatabaseString();
}
