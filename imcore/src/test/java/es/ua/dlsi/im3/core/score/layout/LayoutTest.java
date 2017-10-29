package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class LayoutTest {
    // Just test it does not crash
    @Test
    public void horizontalLayoutBravura() throws Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/simple1.xml");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.bravura,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("simple.svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile("simple.pdf");
        pdfExporter.exportLayout(pdfFile, layout);
    }

    private void systemBreaks(ScoreSong scoreSong, ScoreLayout layout, String name) throws IM3Exception {
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile(name + ".svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile(name + ".pdf");
        pdfExporter.exportLayout(pdfFile, layout);

    }

    @Test
    public void layoutsBravura() throws Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/layout/manual_system_break.xml");
        ScoreSong song = importer.importSong(file);
        PageLayout pageLayout = new PageLayout(song, LayoutFonts.bravura,
                new CoordinateComponent(1500), new CoordinateComponent(1000));
        systemBreaks(song, pageLayout, "manual_system_break_page");

        HorizontalLayout horizontalLayout = new HorizontalLayout(song, LayoutFonts.bravura,
                new CoordinateComponent(1500), new CoordinateComponent(1000));

        systemBreaks(song, horizontalLayout, "manual_system_break_horizontal");
    }


    // Just test it does not crash
    @Test
    public void horizontalLayout2StavesVerticallyAligned() throws Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/layout/two_staves_homophonic_vertical_aligned.xml");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.bravura,
                new CoordinateComponent(960), new CoordinateComponent(700));
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
    public void horizontalLayout2StavesNonVerticallyAligned() throws Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/layout/two_staves_non_homophonic.xml");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.bravura,
                new CoordinateComponent(1280), new CoordinateComponent(700));
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
    public void horizontalLayoutCapitan() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/nodiviertanllantoninyo_mensural_only.mei");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.capitan,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("nodiviertanllantoninyo_mensural_only.svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile("nodiviertanllantoninyo_mensural_only.pdf");
        pdfExporter.exportLayout(pdfFile, layout);
    }

    // Just test it does not crash
    @Test
    public void pagelLayoutPatriarca() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/layout/patriarca/16-1544_ES-VC_1-3-1_00003.mei");
        ScoreSong song = importer.importSong(file);


        PageLayout layout = new PageLayout(song, LayoutFonts.capitan,
                new CoordinateComponent(3000), new CoordinateComponent(1700));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("patriarca1_pages.svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile("patriarca1_pages.pdf");
        pdfExporter.exportLayout(pdfFile, layout);

        HorizontalLayout hlayout = new HorizontalLayout(song, LayoutFonts.capitan,
                new CoordinateComponent(3000), new CoordinateComponent(1700));
        hlayout.layout();

        SVGExporter svgExporter2 = new SVGExporter();
        File svgFile2 = TestFileUtils.createTempFile("patriarca1_horizontal.svg");
        svgExporter.exportLayout(svgFile2, hlayout);

        PDFExporter pdfExporter2 = new PDFExporter();
        File pdfFile2 = TestFileUtils.createTempFile("patriarca1_horizontal.pdf");
        pdfExporter2.exportLayout(pdfFile2, hlayout);

    }

    @Test
    public void horizontalLayoutSimpleTie() throws Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/simple_tie.xml");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.bravura,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("simple_tie.svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile("simple_tie.pdf");
        pdfExporter.exportLayout(pdfFile, layout);
    }


    @Test
    public void horizontalLayoutSimpleBeam() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/simple_beam.mei");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.bravura,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("simple_beam.svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile("simple_beam.pdf");
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