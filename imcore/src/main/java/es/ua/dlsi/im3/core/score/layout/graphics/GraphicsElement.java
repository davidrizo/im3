package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.IUniqueIDObject;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.NotationSymbol;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.Glyph;
import es.ua.dlsi.im3.gui.javafx.GUIException;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.time.Instant;
import java.util.HashSet;

// TODO: 18/9/17  Create a GraphicsDevice instead of implementing these interfaces
public abstract class GraphicsElement implements IJavaFXGUIElement, IPDFElement, ISVGElement {
    private final InteractionElementType interactionElementType;
    private final String ID;
    private final NotationSymbol notationSymbol;
    private Canvas canvas;
    boolean hidden;
    static long NEXT_ID = 0;
    private long idSequence;
    private RGBA color;
    /**
     * Last instant it has been repainted
     */
    private Instant lastRepaint;

    public GraphicsElement(NotationSymbol notationSymbol, InteractionElementType interactionElementType) {
        if (notationSymbol == null) {
            throw new IM3RuntimeException("notationSymbol cannot be null for class " + this.getClass().getName());
        }
        this.notationSymbol = notationSymbol;
        if (interactionElementType == null) {
            throw new IM3RuntimeException("interactionElementType cannot be null for class " + this.getClass().getName());
        }
        this.interactionElementType = interactionElementType;
        synchronized (GraphicsElement.class) {
            NEXT_ID++;
            this.idSequence = NEXT_ID;
        }
        this.ID = this.interactionElementType.name() + "_" + this.idSequence;
        hidden = false;
    }
    
    public void repaint() throws IM3Exception {
        doRepaint();
        lastRepaint = Instant.now();
    }

    protected abstract void doRepaint() throws IM3Exception;

    public Instant getLastRepaint() {
        return lastRepaint;
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
     * The space between the x of the symbol and its left end and its right end
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

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setRGBColor(RGBA color) {
        this.color = color;
        this.setJavaFXColor(new Color(color.getR(), color.getG(), color.getB(), color.getA()));
    }

    public InteractionElementType getInteractionElementType() {
        return interactionElementType;
    }

    public String getID() {
        return ID;
    }

    public NotationSymbol getNotationSymbol() {
        return notationSymbol;
    }

    public void generateSVG(StringBuilder sb, int tabs, HashSet<Glyph> usedGlyphs) throws ExportException {
        doGenerateSVG(sb, tabs, usedGlyphs);
    }

    public void generatePDF(PDPageContentStream contents, PDFExporter exporter, PDPage page) throws ExportException {
        doGeneratePDF(contents, exporter, page);
    }

    public Node generateJavaFXRoot() throws GUIException, ExportException {
        Node result = doGenerateJavaFXRoot();
        return result;
    }
}
