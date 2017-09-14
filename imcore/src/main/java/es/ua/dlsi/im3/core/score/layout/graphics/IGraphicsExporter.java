package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface IGraphicsExporter {
    void exportCanvas(OutputStream os, Canvas canvas) throws ExportException;
    void exportCanvas(File file, Canvas canvas) throws ExportException;
}
