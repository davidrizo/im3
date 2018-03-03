package es.ua.dlsi.im3.omr.muret.model;

import com.thoughtworks.xstream.XStream;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.model.pojo.*;

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

    static String createMEIFilename(File projectFolder) {
        return FileUtils.getFileWithoutPath(projectFolder.getName()) + ".mei";
    }

    public void save(OMRProject project) throws ExportException {
        Project pojoProject = project.createPOJO();
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

        if (project.getScoreSong() != null) {
            File meiFile = new File(projectFolder, createMEIFilename(projectFolder));
            MEISongExporter meiSongExporter = new MEISongExporter();
            meiSongExporter.exportSong(meiFile, project.getScoreSong());
        }
    }

    public OMRProject load(File projectFolder, File trainingFile) throws IM3Exception {
        XStream xStream = new XStream();
        File xmlFile = new File(projectFolder, createXMLFilename(projectFolder));
        if (!xmlFile.exists()) {
            throw new IM3Exception("Cannot open project file '" + xmlFile.getAbsolutePath() + "'");
        }
        Project pojoProject = (Project) xStream.fromXML(xmlFile);

        OMRProject omrProject = new OMRProject(projectFolder, trainingFile);
        omrProject.setNotationType(pojoProject.getNotationType());
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

        for (Instrument pojoInstrument: pojoProject.getInstruments()) {
            omrProject.addInstrument(pojoInstrument.getName());
        }

        for (Page pojoPage: pagesList) {
            OMRPage page = new OMRPage(omrProject, omrProject.getImagesFolder(), pojoPage.getImageRelativeFileName(), omrProject.getScoreSong());
            page.setOrder(pojoPage.getOrder());
            omrProject.addPage(page);

            for (Instrument instrument: pojoPage.getInstruments()) {
                page.addInstrument(omrProject.findInstrument(instrument.getName()));
            }

            for (Region region: pojoPage.getRegions()) {
                page.addRegion(new OMRRegion(region));
            }
            page.loadImageFile();

            for (Staff pojoStaff: pojoPage.getStaves()) {
                OMRStaff staff = new OMRStaff(omrProject, page, pojoStaff.getLeftTopX(), pojoStaff.getLeftTopY(), pojoStaff.getBottomRightX(), pojoStaff.getBottomRightY());
                page.addStaff(staff);
            }
        }

        String meiFilename = createMEIFilename(projectFolder);
        File meiFile = new File(projectFolder, meiFilename);
        if (meiFile.exists()) {
            // just read it if it exists
            MEISongImporter meiSongImporter = new MEISongImporter();
            ScoreSong scoreSong = meiSongImporter.importSong(meiFile);
            omrProject.setScoreSong(scoreSong);
        }
        return omrProject;
    }

}