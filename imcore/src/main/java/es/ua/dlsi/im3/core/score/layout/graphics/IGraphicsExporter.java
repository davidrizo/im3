package es.ua.dlsi.im3.core.score.layout.graphics;

import java.io.File;
import java.io.OutputStream;

public interface IGraphicsExporter {
    String exportCanvas(Canvas canvas);
    void exportCanvas(OutputStream os, Canvas canvas);
    void exportCanvas(File file, Canvas canvas);
}
