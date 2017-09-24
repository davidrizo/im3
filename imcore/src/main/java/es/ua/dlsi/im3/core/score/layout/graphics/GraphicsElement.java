package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import javafx.scene.Node;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.HashSet;

// TODO: 18/9/17  Create a GraphicsDevice instead of implementing these interfaces
public abstract class GraphicsElement implements IJavaFXGUIElement, IPDFElement, ISVGElement {
    private Canvas canvas;
    private String ID;

    public GraphicsElement(String ID) {
        this.ID = ID;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Helper method used by children
     * @param page
     * @param x
     * @return
     */
    protected float getPFDCoordinateX(PDPage page, double x) {
        return LayoutConstants.PDF_LEFT_MARGIN + (float) x + page.getCropBox().getLowerLeftX();
    }

    /**
     * Helper method used by children
     * @param page
     * @param y
     * @return
     */
    protected float getPFDCoordinateY(PDPage page, double y) {
        return -LayoutConstants.PDF_TOP_MARGIN -(float) y + page.getCropBox().getUpperRightY();
    }


    /**
     * It cannot be based on the x position because the layout algorithm depends on it. It must be based on the physical width
     */
    public abstract double getWidth() throws IM3Exception;

    public abstract Coordinate getPosition() throws IM3Exception;


    /**
     * The space between the x of the symbol and its left end
     * @return
     */
    public BoundingBox computeBoundingBox() throws IM3Exception {
        double displacement = getPosition().getX().getDisplacement();
        BoundingBox boundingBox;
        if (displacement < 0) {
            boundingBox = new BoundingBox(displacement, getWidth());
        } else {
            boundingBox = new BoundingBox(0, displacement+getWidth());
        }
        return boundingBox;
    }

    public String getID() {
        return ID;
    }
}
