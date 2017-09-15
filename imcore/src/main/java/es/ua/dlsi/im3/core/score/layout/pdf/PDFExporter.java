package es.ua.dlsi.im3.core.score.layout.pdf;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.ScoreLayout;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.IGraphicsExporter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class PDFExporter implements IGraphicsExporter {
    private void generatePDF(PDDocument document, Canvas canvas) throws ExportException {
        PDPage page = new PDPage();
        document.addPage(page);

        try {
            PDPageContentStream contents = new PDPageContentStream(document, page);
            for (GraphicsElement element : canvas.getElements()) {
                element.generatePDF(contents, null); //TODO CARGAR FUENTE COMo SVG EXPORTER SIN REPETIR CODIGO
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
        PDDocument document = new PDDocument();
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
        PDDocument document = new PDDocument();
        try {
            generatePDF(document, layout);
            document.save(file);
            document.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    public void exportLayout(File file, Canvas canvas) throws ExportException {
        PDDocument document = new PDDocument();
        try {
            generatePDF(document, canvas);
            document.save(file);
            document.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }
}
