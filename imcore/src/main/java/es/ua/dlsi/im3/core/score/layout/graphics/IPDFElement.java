package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

public interface IPDFElement {
    void generatePDF(PDPageContentStream contents, LayoutFont layoutFont, PDFont musicFont, PDFont textFont, PDPage page) throws ExportException;
}
