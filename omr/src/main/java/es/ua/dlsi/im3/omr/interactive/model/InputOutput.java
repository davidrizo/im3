package es.ua.dlsi.im3.omr.interactive.model;

import com.thoughtworks.xstream.XStream;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.interactive.OMRController;
import es.ua.dlsi.im3.omr.interactive.model.pojo.Page;
import es.ua.dlsi.im3.omr.interactive.model.pojo.Project;
import es.ua.dlsi.im3.omr.interactive.model.pojo.Staff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class InputOutput {
    static String createXMLFilename(File projectFolder) {
        return FileUtils.getFileWithoutPath(projectFolder.getName()) + ".mrt";
    }


    public void save(OMRProject project) throws ExportException {
        Project pojoProject = new Project();
        for (OMRPage page: project.pagesProperty()) {
            Page pojoPage = new Page(page.getImageRelativeFileName());
            pojoProject.getPages().add(pojoPage);
            for (OMRStaff staff: page.getStaves()) {
                Staff pojoStaff = new Staff();
                pojoPage.getStaves().add(pojoStaff);
            }
        }

        File projectFolder = project.getProjectFolder();
        File xmlFile = new File(projectFolder, createXMLFilename(projectFolder));
        XStream xStream = new XStream();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(xmlFile);
        } catch (FileNotFoundException e) {
            throw new ExportException(e);
        }
        xStream.toXML(pojoProject, fos);
    }

    public OMRProject load(OMRController controller, File projectFolder) throws IM3Exception {
        XStream xStream = new XStream();
        File xmlFile = new File(projectFolder, createXMLFilename(projectFolder));
        Project pojoProject = (Project) xStream.fromXML(xmlFile);

        OMRProject omrProject = new OMRProject(projectFolder, controller);
        for (Page pojoPage: pojoProject.getPages()) {
            OMRPage page = new OMRPage(omrProject, omrProject.getImagesFolder(), pojoPage.getImageRelativeFileName(), omrProject.getScoreSong());
            omrProject.addPage(page);
            page.loadImageFile();

            for (Staff pojoStaff: pojoPage.getStaves()) {
                OMRStaff staff = new OMRStaff(page, omrProject.getScoreSong(), pojoStaff.getLeftTopX(), pojoStaff.getLeftTopY(), pojoStaff.getBottomRightX(), pojoStaff.getBottomRightY());
                page.addStaff(staff);
            }
        }
        return omrProject;
    }
}
