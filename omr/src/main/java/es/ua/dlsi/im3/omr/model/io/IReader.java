package es.ua.dlsi.im3.omr.model.io;

import es.ua.dlsi.im3.omr.model.entities.Project;

import java.io.File;
import java.io.InputStream;

/**
 * @autor drizo
 */
public interface IReader {
    Project load(InputStream stream);
    Project load(File file);
}
