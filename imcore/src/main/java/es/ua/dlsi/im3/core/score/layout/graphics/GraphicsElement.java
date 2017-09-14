package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public abstract class GraphicsElement {
    public abstract void generateSVG(StringBuilder sb, int tabs);

    /**
     * In the future we could decouple this PDPageContentStream using an intermediate interface
     * @param contents
     */
    public abstract void generatePDF(PDPageContentStream contents) throws ExportException;
}
