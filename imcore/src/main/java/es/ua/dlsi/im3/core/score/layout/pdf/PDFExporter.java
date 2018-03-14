package es.ua.dlsi.im3.core.score.layout.pdf;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.ScoreLayout;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.IGraphicsExporter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class PDFExporter implements IGraphicsExporter {
    HashMap<LayoutFont, PDType0Font> musicFonts;
    HashMap<LayoutFont, PDTrueTypeFont> textFonts;

    public PDFExporter() {
    }

    private void generatePDF(PDDocument document, Canvas canvas) throws ExportException {
        //PDPage page = new PDPage();
        // TODO: 1/10/17 PDF SIZE en función de lo que recibimos
        //PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));

        PDPage page = null;
        try {
            page = new PDPage(new PDRectangle((float)canvas.getWidth(), (float)canvas.getHeight()));
        } catch (IM3Exception e) {
            throw new ExportException(e);
        }

        document.addPage(page);

        try {
            PDPageContentStream contents = new PDPageContentStream(document, page);

            //contents.concatenate2CTM(new AffineTransform(1, 0, 0, -1, xtl, ytl));

            for (GraphicsElement element : canvas.getElements()) {
                if (!element.isHidden()) {
                    element.generatePDF(contents, this, page);
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
        PDDocument document = createDocument(layout);
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
        PDDocument document = createDocument(layout);
        try {
            generatePDF(document, layout);
            document.save(file);
            document.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    public void exportLayout(File file, Canvas canvas, ScoreLayout layout) throws ExportException {
        PDDocument document = createDocument(layout);
        try {
            generatePDF(document, canvas);
            document.save(file);
            document.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    private PDDocument createDocument(ScoreLayout layout) throws ExportException {
        musicFonts = new HashMap<>();
        textFonts = new HashMap<>();
        PDDocument document = new PDDocument();
        try {
            for (LayoutFont layoutFont: layout.getLayoutFonts()) {
                if (!musicFonts.containsKey(layoutFont)) {
                    PDType0Font musicFont = PDType0Font.load(document, layoutFont.getOTFMusicFont(), false);
                    musicFonts.put(layoutFont, musicFont);

                    PDTrueTypeFont textFont = PDTrueTypeFont.load(document, layoutFont.getOtfTextFont(), WinAnsiEncoding.INSTANCE); //TODO ¿ñ?
                    textFonts.put(layoutFont, textFont);
                }
            }
        } catch (IOException e) {
            throw new ExportException(e);
        }
        return document;
    }


    public PDType0Font getMusicFont(LayoutFont layoutFont) throws ExportException {
        PDType0Font musicFont = musicFonts.get(layoutFont);
        if (musicFont == null) {
            throw new ExportException("Cannot find music font for layout " + layoutFont);
        }
        return musicFont;
    }

    public PDTrueTypeFont getTextFont(LayoutFont layoutFont) throws ExportException {
        PDTrueTypeFont textFont = textFonts.get(layoutFont);
        if (textFont == null) {
            throw new ExportException("Cannot find text font for layout " + layoutFont);
        }
        return textFont;
    }

}
