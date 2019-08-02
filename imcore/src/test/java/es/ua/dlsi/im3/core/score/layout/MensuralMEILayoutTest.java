package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.conversions.MensuralToModern;
import es.ua.dlsi.im3.core.conversions.ScoreToPlayed;
import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.played.io.MidiSongExporter;
import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.core.score.Intervals;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.clefs.*;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLExporter;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import org.junit.Test;

import java.io.File;

/**
 * Created for MuRET
 */
public class MensuralMEILayoutTest {
    @Test
    public void figuresLayoutTest() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/mei/muret/elements.mei");
        ScoreSong song = importer.importSong(file);

        HorizontalLayout horizontalLayout = new HorizontalLayout(song, new CoordinateComponent(10000), new CoordinateComponent(5000), LayoutFonts.bravura);
        horizontalLayout.layout(true);

        SVGExporter svgExporter = new SVGExporter();
        File svgOutput = TestFileUtils.createTempFile("elements.svg");
        svgExporter.exportLayout(svgOutput, horizontalLayout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfOutput = TestFileUtils.createTempFile("elements.pdf");
        pdfExporter.exportLayout(pdfOutput, horizontalLayout);
    }

    @Test
    public void ligaturesLayoutTest() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/mei/muret/ligature.mei");
        ScoreSong song = importer.importSong(file);

        HorizontalLayout horizontalLayout = new HorizontalLayout(song, new CoordinateComponent(1000), new CoordinateComponent(500), LayoutFonts.bravura);
        horizontalLayout.layout(true);

        SVGExporter svgExporter = new SVGExporter();
        File svgOutput = TestFileUtils.createTempFile("ligature.svg");
        svgExporter.exportLayout(svgOutput, horizontalLayout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfOutput = TestFileUtils.createTempFile("ligature.pdf");
        pdfExporter.exportLayout(pdfOutput, horizontalLayout);
    }

    @Test
    public void muretPartLayoutTest() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/mei/muret/tiple-asperges-me.mei");
        ScoreSong song = importer.importSong(file);
    }

    @Test
    public void muretFullScoreLayoutTest() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/mei/muret/asperges-me.mei");
        ScoreSong mensural = importer.importSong(file);

        HorizontalLayout horizontalLayout = new HorizontalLayout(mensural, new CoordinateComponent(25000), new CoordinateComponent(1000), LayoutFonts.bravura);
        horizontalLayout.layout(true);

        SVGExporter svgExporter = new SVGExporter();
        File svgOutput = TestFileUtils.createTempFile("asperges-me.svg");
        svgExporter.exportLayout(svgOutput, horizontalLayout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfOutput = TestFileUtils.createTempFile("asperges-me.pdf");
        pdfExporter.exportLayout(pdfOutput, horizontalLayout);

        MensuralToModern mensuralToModern = new MensuralToModern(null);
        //TODO Parámetro
        //ScoreSong modern = mensuralToModern.convertIntoNewSong(mensural, Intervals.FOURTH_PERFECT_DESC); // ésta genera más sostenidos
        ScoreSong modern = mensuralToModern.convertIntoNewSong(mensural, Intervals.UNISON_PERFECT);

        MusicXMLExporter musicXMLExporter = new MusicXMLExporter(true);
        File musicXMLFile = TestFileUtils.createTempFile("asperges-me-musicxml.xml");
        musicXMLExporter.exportSong(musicXMLFile, modern);

        mensuralToModern.merge(mensural, modern);

        HorizontalLayout horizontalLayoutMerged = new HorizontalLayout(mensural, new CoordinateComponent(25000), new CoordinateComponent(2000), LayoutFonts.bravura);
        horizontalLayoutMerged.layout(true);

        SVGExporter svgExporterMerged = new SVGExporter();
        File svgOutputMerged = TestFileUtils.createTempFile("asperges-me-mensural-modern.svg");
        svgExporterMerged.exportLayout(svgOutputMerged, horizontalLayoutMerged);

        PDFExporter pdfExporterMerged = new PDFExporter();
        File pdfOutputMerged = TestFileUtils.createTempFile("asperges-me-mensural-modern.pdf");
        pdfExporterMerged.exportLayout(pdfOutputMerged, horizontalLayoutMerged);

    }
}

