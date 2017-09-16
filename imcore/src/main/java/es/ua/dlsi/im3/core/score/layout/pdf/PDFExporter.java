package es.ua.dlsi.im3.core.score.layout.pdf;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.FontFactory;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.ScoreLayout;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.IGraphicsExporter;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.StandardEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.SymbolEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class PDFExporter implements IGraphicsExporter {
    private LayoutFont bravura;
    PDFont bravuraMusicFont;
    PDFont bravuraTextFont;

    public PDFExporter() {
        bravura = FontFactory.getInstance().getBravuraFont();
    }

    private void generatePDF(PDDocument document, Canvas canvas) throws ExportException {
        PDPage page = new PDPage();
        //page.setCropBox(new PDRectangle(30, 30, page.getCropBox().getWidth()-30, page.getCropBox().getHeight()-30));
        document.addPage(page);

        try {
            PDPageContentStream contents = new PDPageContentStream(document, page);

            //contents.concatenate2CTM(new AffineTransform(1, 0, 0, -1, xtl, ytl));

            for (GraphicsElement element : canvas.getElements()) {
                element.generatePDF(contents, bravura, bravuraMusicFont, bravuraTextFont, page);
            }
            contents.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }
    private void generatePDF(PDDocument document, ScoreLayout layout) throws ExportException {
        for (Canvas canvas: layout.getCanvases()) {
            generatePDF(document, canvas);
        }
    }
    @Override
    public void exportLayout(OutputStream os, ScoreLayout layout) throws ExportException {
        PDDocument document = createDocument();
        try {
            generatePDF(document, layout);
            document.save(os);
            document.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    @Override
    public void exportLayout(File file, ScoreLayout layout) throws ExportException {
        PDDocument document = createDocument();
        try {
            generatePDF(document, layout);
            document.save(file);
            document.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    public void exportLayout(File file, Canvas canvas) throws ExportException {
        PDDocument document = createDocument();
        try {
            generatePDF(document, canvas);
            document.save(file);
            document.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    private PDDocument createDocument() throws ExportException {
        PDDocument document = new PDDocument();
        try {
            bravuraMusicFont = PDType0Font.load(document, bravura.getOTFMusicFont(), false);
            //bravuraMusicFont = PDTrueTypeFont.load(document, bravura.getOTFMusicFont(), StandardEncoding.INSTANCE); //TODO BuiltInEncoding??
            bravuraTextFont = PDTrueTypeFont.load(document, bravura.getOTFMusicFont(), WinAnsiEncoding.INSTANCE);
        } catch (IOException e) {
            throw new ExportException(e);
        }
        return document;
    }


}
