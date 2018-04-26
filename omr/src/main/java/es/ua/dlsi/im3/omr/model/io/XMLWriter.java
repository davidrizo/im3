package es.ua.dlsi.im3.omr.model.io;

import com.thoughtworks.xstream.XStream;
import es.ua.dlsi.im3.omr.model.entities.Project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @autor drizo
 */
public class XMLWriter implements IWriter {

    @Override
    public void save(Project project, OutputStream stream) {
        XStream xStream = XStreamFactory.create();
        xStream.toXML(project, stream);
    }

    @Override
    public void save(Project project, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        save(project, fos);
        fos.flush();
        fos.close();
    }
}
