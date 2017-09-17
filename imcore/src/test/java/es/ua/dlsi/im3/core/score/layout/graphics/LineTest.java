package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import org.junit.Test;

import java.io.File;

public class LineTest {
    // simply test it does not crash
    @Test
    public void testGenerateSVG() throws ExportException, IM3Exception {
        Canvas canvas = new Canvas(500, 700);
        Line line = new Line(1, 10, 50, 75, 10, StrokeType.eDashed);
        Line line2 = new Line(20, 50, 100, 2000, 1, StrokeType.eSolid);
        canvas.add(line);
        canvas.add(line2);

        SVGExporter exporter = new SVGExporter(LayoutFonts.bravura);
        System.out.println(exporter.exportLayout(canvas));

        PDFExporter pdfExporter = new PDFExporter(LayoutFonts.bravura);
        File file = TestFileUtils.createTempFile("lines.pdf");
        pdfExporter.exportLayout(file, canvas);
    }


}