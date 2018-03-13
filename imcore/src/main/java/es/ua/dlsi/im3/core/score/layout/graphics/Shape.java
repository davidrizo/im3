package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * All shapes must be able to generate an SVG path.
 * See https://github.com/JFXtras/jfxtras-labs/blob/2.2/src/main/java/jfxtras/labs/util/ShapeConverter.java
 * for documentation to create new SVG shapes
 */
public abstract class Shape extends GraphicsElement {
    public Shape(InteractionElementType interactionElementType) {
        super(interactionElementType);
    }
}
