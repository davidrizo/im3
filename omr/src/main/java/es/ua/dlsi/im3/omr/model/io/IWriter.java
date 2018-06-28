package es.ua.dlsi.im3.omr.model.io;

import es.ua.dlsi.im3.omr.model.entities.Project;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @autor drizo
 */
public interface IWriter {
    void save(Project project, OutputStream stream);
    void save(Project project, File file) throws IOException;
}
