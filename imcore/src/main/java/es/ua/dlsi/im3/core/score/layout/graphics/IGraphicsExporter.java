package es.ua.dlsi.im3.core.score.layout.graphics;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.layout.ScoreLayout;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface IGraphicsExporter {
    void exportLayout(OutputStream os, ScoreLayout layout) throws ExportException;
    void exportLayout(File file, ScoreLayout layout) throws ExportException;
}
