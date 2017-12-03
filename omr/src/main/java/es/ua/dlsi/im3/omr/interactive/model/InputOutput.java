package es.ua.dlsi.im3.omr.interactive.model;

import com.thoughtworks.xstream.XStream;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.interactive.OMRMainController;
import es.ua.dlsi.im3.omr.interactive.model.pojo.Page;
import es.ua.dlsi.im3.omr.interactive.model.pojo.Project;
import es.ua.dlsi.im3.omr.interactive.model.pojo.Staff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputOutput {
    static String createXMLFilename(File projectFolder) {
        return FileUtils.getFileWithoutPath(projectFolder.getName()) + ".mrt";
    }

    public void save(OMRProject project) throws ExportException {
        Project pojoProject = new Project();
        for (OMRPage page: project.pagesProperty()) {
            Page pojoPage = new Page(page.getImageRelativeFileName());
            pojoPage.setInstrumentList(page.getInstrumentList());
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
        Logger.getLogger(InputOutput.class.getName()).log(Level.INFO, "Writing file " + xmlFile.getAbsolutePath());
        xStream.toXML(pojoProject, fos);
        try {
            fos.close();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    public OMRProject load(File projectFolder, File trainingFile) throws IM3Exception {
        XStream xStream = new XStream();
        File xmlFile = new File(projectFolder, createXMLFilename(projectFolder));
        Project pojoProject = (Project) xStream.fromXML(xmlFile);

        OMRProject omrProject = new OMRProject(projectFolder, trainingFile);
        ArrayList<Page> pagesList = new ArrayList<>(pojoProject.getPages());
        pagesList.sort(new Comparator<Page>() {
            @Override
            public int compare(Page o1, Page o2) {
                int diff = o1.getOrder() - o2.getOrder();
                if (diff == 0) {
                    diff = o1.getImageRelativeFileName().compareTo(o2.getImageRelativeFileName());
                }
                return diff;
            }
        });

        for (Page pojoPage: pagesList) {
            OMRPage page = new OMRPage(omrProject, omrProject.getImagesFolder(), pojoPage.getImageRelativeFileName(), omrProject.getScoreSong());
            page.setOrder(pojoPage.getOrder());
            omrProject.addPage(page);
            page.loadImageFile();

            for (Staff pojoStaff: pojoPage.getStaves()) {
                OMRStaff staff = new OMRStaff(omrProject, page, pojoStaff.getLeftTopX(), pojoStaff.getLeftTopY(), pojoStaff.getBottomRightX(), pojoStaff.getBottomRightY());
                page.addStaff(staff);
            }
        }
        return omrProject;
    }
}
