package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import org.junit.Test;

import java.io.File;

public class HorizontalLayoutTest {
    // Just test it does not crash
    @Test
    public void layoutBravura() throws Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/simple1.xml");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.bravura,
                new Coordinate(new CoordinateComponent(0), new CoordinateComponent(0)),
                new Coordinate(new CoordinateComponent(960), new CoordinateComponent(700)));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("simple.svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile("simple.pdf");
        pdfExporter.exportLayout(pdfFile, layout);
    }

    // Just test it does not crash
    @Test
    public void layout2StavesVerticallyAligned() throws Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/layout/two_staves_homophonic_vertical_aligned.xml");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.bravura,
                new Coordinate(new CoordinateComponent(0), new CoordinateComponent(0)),
                new Coordinate(new CoordinateComponent(960), new CoordinateComponent(700)));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("two_staves_homophonic_vertical_aligned.svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile("two_staves_homophonic_vertical_aligned.pdf");
        pdfExporter.exportLayout(pdfFile, layout);
    }

    // Just test it does not crash
    @Test
    public void layout2StavesNonVerticallyAligned() throws Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/layout/two_staves_non_homophonic.xml");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.bravura,
                new Coordinate(new CoordinateComponent(0), new CoordinateComponent(0)),
                new Coordinate(new CoordinateComponent(1280), new CoordinateComponent(700)));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("two_staves_non_homophonic.svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile("two_staves_non_homophonic.pdf");
        pdfExporter.exportLayout(pdfFile, layout);
    }


    // Just test it does not crash
    @Test
    public void layoutCapitan() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/nodiviertanllantoninyo_mensural_only.mei");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.capitan,
                new Coordinate(new CoordinateComponent(0), new CoordinateComponent(0)),
                new Coordinate(new CoordinateComponent(960), new CoordinateComponent(700)));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("nodiviertanllantoninyo_mensural_only.svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile("nodiviertanllantoninyo_mensural_only.pdf");
        pdfExporter.exportLayout(pdfFile, layout);
    }


    // Just test it does not crash
    // FIXME: 21/9/17 Commented until M
    /*@Test
    public void layoutCapitanAndBravura() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/nodiviertanllantoninyo.mei");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.capitan,
                new Coordinate(new CoordinateComponent(0), new CoordinateComponent(0)),
                new Coordinate(new CoordinateComponent(960), new CoordinateComponent(700)));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("nodiviertanllantoninyo.svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile("nodiviertanllantoninyo.pdf");
        pdfExporter.exportLayout(pdfFile, layout);



    }*/

}