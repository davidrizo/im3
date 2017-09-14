package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;

import java.io.File;
import java.io.OutputStream;

public class SVGExporter implements IGraphicsExporter {
    @Override
    public String exportCanvas(Canvas canvas) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" standalone=\"no\"?>\n");
        XMLExporterHelper.start(sb, 0, "svg",
                "version", "1.1",
                "baseProfile", "full",
                "width", Double.toString(canvas.getWidth()),
                "height", Double.toString(canvas.getHeight()),
                "xmlns", "http://www.w3.org/2000/svg");

        for (GraphicsElement element: canvas.getElements()) {
            element.generateSVG(sb, 1);
        }

        XMLExporterHelper.end(sb, 0, "svg");
        return sb.toString();
    }

    @Override
    public void exportCanvas(OutputStream os, Canvas canvas) {
    }

    @Override
    public void exportCanvas(File file, Canvas canvas) {

    }
}
