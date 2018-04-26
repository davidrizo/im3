package es.ua.dlsi.im3.omr.model.io;

import com.thoughtworks.xstream.XStream;
import es.ua.dlsi.im3.omr.model.entities.Project;

import java.io.File;
import java.io.InputStream;

/**
 * @autor drizo
 */
public class XMLReader implements IReader {
    @Override
    public Project load(InputStream stream) {
        XStream xStream = XStreamFactory.create();
        Project project = (Project) xStream.fromXML(stream);
        return project;
    }

    @Override
    public Project load(File file) {
        XStream xStream = XStreamFactory.create();
        Project project = (Project) xStream.fromXML(file);
        return project;
    }
}
