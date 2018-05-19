package es.ua.dlsi.im3.omr.model.io;

import com.thoughtworks.xstream.XStream;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.Project;

import java.io.File;
import java.io.InputStream;

/**
 * @autor drizo
 */
public class XMLReader implements IReader {
    AgnosticVersion agnosticVersion;

    public XMLReader(AgnosticVersion agnosticVersion) {
        this.agnosticVersion = agnosticVersion;
    }

    @Override
    public Project load(InputStream stream) {
        XStream xStream = XStreamFactory.create(agnosticVersion);
        Project project = (Project) xStream.fromXML(stream);
        return project;
    }

    @Override
    public Project load(File file) {
        XStream xStream = XStreamFactory.create(agnosticVersion);
        Project project = (Project) xStream.fromXML(file);
        return project;
    }
}
