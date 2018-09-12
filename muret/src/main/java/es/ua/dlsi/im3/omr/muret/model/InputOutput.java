package es.ua.dlsi.im3.omr.muret.model;

import com.thoughtworks.xstream.XStream;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.*;
import es.ua.dlsi.im3.omr.model.io.XMLReader;
import es.ua.dlsi.im3.omr.model.io.XMLWriter;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class InputOutput {
    public static final AgnosticVersion AGNOSTIC_VERSION = AgnosticVersion.v2;
    static String createXMLFilename(File projectFolder) {
        return FileUtils.getFileWithoutPath(projectFolder.getName()) + ".mrt";
    }

    static String createMEIFilename(File projectFolder) {
        return FileUtils.getFileWithoutPath(projectFolder.getName()) + ".mei";
    }

    public void save(OMRProject project) throws IM3Exception {
        Project pojoProject = project.createPOJO();
        File projectFolder = project.getProjectFolder();
        File xmlFile = new File(projectFolder, createXMLFilename(projectFolder));

        XMLWriter writer = new XMLWriter(AGNOSTIC_VERSION);
        try {
            writer.save(pojoProject, xmlFile);
        } catch (IOException e) {
            throw new ExportException(e);
        }

        // TODO: 21/4/18
        if (project.getScoreSong() != null) {
            File meiFile = new File(projectFolder, createMEIFilename(projectFolder));
            MEISongExporter meiSongExporter = new MEISongExporter();
            meiSongExporter.exportSong(meiFile, project.getScoreSong());
        }
    }

    public OMRProject load(File projectFolder) throws IM3Exception {
        File xmlFile = new File(projectFolder, createXMLFilename(projectFolder));
        if (!xmlFile.exists()) {
            throw new IM3Exception("Cannot open project file '" + xmlFile.getAbsolutePath() + "'");
        }

        XMLReader reader = new XMLReader(AGNOSTIC_VERSION);
        Project pojoProject = reader.load(xmlFile);

        OMRProject omrProject = new OMRProject(projectFolder);
        omrProject.setName(pojoProject.getName());
        omrProject.setComposer(pojoProject.getComposer());
        omrProject.setChangedBy(pojoProject.getChangedBy());
        omrProject.setLastChangedDate(pojoProject.getLastChangedDate());
        omrProject.setNotationType(pojoProject.getNotationType());
        omrProject.setComments(pojoProject.getComments());

        /*ArrayList<Image> pagesList = new ArrayList<>(pojoProject.getImages());
        pagesList.sort(new Comparator<Image>() {
            @Override
            public int compare(Image o1, Image o2) {
                int diff = o1.getOrder() - o2.getOrder();
                if (diff == 0) {
                    diff = o1.getImageRelativeFileName().compareTo(o2.getImageRelativeFileName());
                }
                return diff;
            }
        });*/
        int nregion = 1;
        for (Image image: pojoProject.getImages()) {
            OMRImage omrImage = new OMRImage(omrProject, new File(omrProject.getImagesFolder(), image.getImageRelativeFileName()));
            if (image.getInstrument() != null) {
                omrImage.instrumentProperty().setValue(omrProject.getInstruments().getInstrument(image.getInstrument().getName()));
            }
            omrImage.setOrder(image.getOrder());
            omrImage.setComments(image.getComments());
            omrProject.addImage(omrImage);

            for (Page page: image.getPages()) {
                BoundingBox boundingBox = page.getBoundingBox();
                OMRPage omrPage = new OMRPage(omrImage, boundingBox.getFromX(), boundingBox.getFromY(), boundingBox.getToX(), boundingBox.getToY());
                omrPage.commentsProperty().setValue(page.getComments());
                if (page.getInstrument() != null) {
                    omrPage.instrumentProperty().setValue(omrProject.getInstruments().getInstrument(page.getInstrument().getName()));
                }
                omrImage.addPage(omrPage);

                for (Region region: page.getRegions()) {
                    OMRRegion omrRegion = new OMRRegion(omrPage, nregion++, region);
                    if (region.getInstrument() != null) {
                        omrRegion.instrumentProperty().setValue(omrProject.getInstruments().getInstrument(region.getInstrument().getName()));
                    }
                    omrPage.addRegion(omrRegion);
                }
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
