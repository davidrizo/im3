package es.ua.dlsi.im3.core.score.layout.pdf;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.FontFactory;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.ScoreLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.IGraphicsExporter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class PDFExporter implements IGraphicsExporter {
    PDFont musicFont;
    PDFont textFont;

    public PDFExporter() {
    }

    private void generatePDF(PDDocument document, Canvas canvas) throws ExportException {
        PDPage page = new PDPage();
        //page.setCropBox(new PDRectangle(30, 30, page.getCropBox().getWidth()-30, page.getCropBox().getHeight()-30));
        document.addPage(page);

        try {
            PDPageContentStream contents = new PDPageContentStream(document, page);

            //contents.concatenate2CTM(new AffineTransform(1, 0, 0, -1, xtl, ytl));

            for (GraphicsElement element : canvas.getElements()) {
                if (!element.isHidden()) {
                    element.generatePDF(contents, musicFont, textFont, page);
                }
            }
            contents.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }
    private void generatePDF(PDDocument document, ScoreLayout layout) throws ExportException {
        // TODO: 21/9/17 ¿Hacer que quepa en una página o escalamos...?
        for (Canvas canvas: layout.getCanvases()) {
            generatePDF(document, canvas);
        }
    }
    @Override
    public void exportLayout(OutputStream os, ScoreLayout layout) throws ExportException {
        PDDocument document = createDocument(layout.getLayoutFont());
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
        PDDocument document = createDocument(layout.getLayoutFont());
        try {
            generatePDF(document, layout);
            document.save(file);
            document.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    public void exportLayout(File file, Canvas canvas, LayoutFont layoutFont) throws ExportException {
        PDDocument document = createDocument(layoutFont);
        try {
            generatePDF(document, canvas);
            document.save(file);
            document.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    private PDDocument createDocument(LayoutFont layoutFont) throws ExportException {
        PDDocument document = new PDDocument();
        try {
            musicFont = PDType0Font.load(document, layoutFont.getOTFMusicFont(), false);
            //musicFont = PDTrueTypeFont.load(document, layoutFont.getOTFMusicFont(), StandardEncoding.INSTANCE);
            textFont = PDTrueTypeFont.load(document, layoutFont.getOtfTextFont(), WinAnsiEncoding.INSTANCE); //TODO ¿ñ?
        } catch (IOException e) {
            throw new ExportException(e);
        }
        return document;
    }


}
