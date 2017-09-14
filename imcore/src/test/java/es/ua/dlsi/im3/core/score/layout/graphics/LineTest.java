package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.utils.FileUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class LineTest {
    @Test
    public void testGenerateSVG() throws ExportException, IM3Exception {
        Canvas canvas = new Canvas(500, 700);
        Line line = new Line(1, 10, 50, 75, 10, StrokeType.eDashed);
        Line line2 = new Line(20, 50, 100, 200, 1, StrokeType.eSolid);
        canvas.add(line);
        canvas.add(line2);

        SVGExporter exporter = new SVGExporter();
        System.out.println(exporter.exportCanvas(canvas));
    }

}