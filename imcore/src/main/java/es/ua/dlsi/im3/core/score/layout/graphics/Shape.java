package es.ua.dlsi.im3.core.score.layout.graphics;

/**
 * All shapes must be able to generate an SVG path.
 * See https://github.com/JFXtras/jfxtras-labs/blob/2.2/src/main/java/jfxtras/labs/util/ShapeConverter.java
 * for documentation to create new SVG shapes
 */
public abstract class Shape extends GraphicsElement{
    private Canvas canvas;
    //TODO AÑADIR ID

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
}
