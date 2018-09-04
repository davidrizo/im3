package es.ua.dlsi.im3.omr.model.io;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.core.score.StemDirection;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.model.entities.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class XMLReaderWriterTest {
    AgnosticVersion agnosticVersion = AgnosticVersion.v2;

    private Project generateTestProject() throws IM3Exception {
        Project project = new Project(ProjectVersion.v1, NotationType.eMensural);
        project.setName("Test");
        Instrument instrument1 = new Instrument("Soprano");
        project.addInstrument(instrument1);
        project.addInstrument(new Instrument("Bass"));

        Image image1 = new Image("image1.jpg");
        image1.setOrder(0);
        project.addImage(image1);

        Page page1 = new Page(0, 0, 1000, 500);
        image1.addPage(page1);

        Image image2 = new Image("image2.jpg");
        image2.setOrder(1);
        project.addImage(image2);
        Page page2_1 = new Page(0, 0, 650, 400);
        image2.addPage(page2_1);

        Page page2_2 = new Page(670, 450, 1250, 800);
        image2.addPage(page2_2);
        page2_2.addInstrument(instrument1);

        Region region1 = new Region(RegionType.all, 20, 30, 40, 50);
        page2_2.add(region1);

        Symbol symbol1 = new Symbol(new AgnosticSymbol(agnosticVersion, new Note(NoteFigures.eighth, Directions.up), PositionsInStaff.LINE_1), 2, 4, 24, 37);
        region1.addSymbol(symbol1);
        Symbol symbol2 = new Symbol(new AgnosticSymbol(agnosticVersion, new Note(new Beam(BeamType.right, 2), Directions.up), PositionsInStaff.LINE_1), 3, 3, 25, 39);
        region1.addSymbol(symbol2);
        return project;
    }

    @Test
    public void loadSave() throws IOException, IM3Exception {
        Project project = generateTestProject();
        // first save it
        File out = TestFileUtils.createTempFile("project.xml");
        XMLWriter writer = new XMLWriter(AgnosticVersion.v2);
        writer.save(project, out);

        XMLReader reader = new XMLReader(AgnosticVersion.v2);
        Project readProject = reader.load(out);

        assertEquals("2 symbols", 2, readProject.getImages().last().getPages().last().getRegions().last().getSymbols().size());

        //checkEquals(project, readProject);
        assertEquals("Project", project, readProject);
        
    }

    /*private void checkEquals(Project project, Project readProject) {
        assertEquals("Version", project.getVersion(), readProject.getVersion());
        assertEquals("Name", project.getName(), readProject.getName());

        assertEquals("# instruments", project.getInstruments().size(), readProject.getInstruments().size());
        for (Instrument instrument: project.getInstruments()) {
            assertNotNull("Instrument " + instrument.getName(), readProject.findInstrumentByName(instrument.getName()));
        }

        assertEquals("# imagesold", project.getImages().size(), readProject.getImages().size());
        for (Image image: project.getImages()) {
            Image readImage = readProject.findImageByFileName(image.getImageRelativeFileName());
            assertEquals("Order", image.getOrder(), readImage.getOrder());

            assertEquals("# pages", image.getPages().size(), readImage.getPages().size());

            Iterator<Page> iterator = image.getPages().iterator();
            Iterator<Page> readIterator = readImage.getPages().iterator();
            while (iterator.hasNext()) {
                Page page = iterator.next();
                Page readPage = readIterator.next();

                assertEquals("Bounding box", page.getBoundingBox(), readPage.getBoundingBox());
                page.getInstruments()
            }


            assertNotNull("Image" + image.getImageRelativeFileName(), readImage);
        }


        //TODO Resto
    }*/
}