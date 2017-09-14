package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
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
            for (GraphicsElement element: canvas.getElements()) {
               element.generatePDF(contents);
            }
            contents.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }
    @Override
    public void exportCanvas(OutputStream os, Canvas canvas) throws ExportException {
        PDDocument document = new PDDocument();
        try {
            generatePDF(document, canvas);
            document.save(os);
            document.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    @Override
    public void exportCanvas(File file, Canvas canvas) throws ExportException {
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
