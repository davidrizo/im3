package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;

import java.io.*;

public class SVGExporter implements IGraphicsExporter {
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
    public void exportCanvas(OutputStream os, Canvas canvas) throws ExportException {
        try (Writer w = new OutputStreamWriter(os, "UTF-8")) {
            w.write(exportCanvas(canvas));
        } // or w.close(); //close will auto-flush    }
        catch (Exception e) {
            throw new ExportException(e);
        }
    }

        @Override
    public void exportCanvas(File file, Canvas canvas)  throws ExportException {
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
                out.write(exportCanvas(canvas));
                out.close();
            } catch (Exception e) {
                throw new ExportException(e);
            }
    }
}
