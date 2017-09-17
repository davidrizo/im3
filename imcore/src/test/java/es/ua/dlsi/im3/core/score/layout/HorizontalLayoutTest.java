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
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.bravura);
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
    public void layoutCapitan() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/nodiviertanllantoninyo.mei");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.capitan);
        layout.layout();

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("nodiviertanllantoninyo.svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile("nodiviertanllantoninyo.pdf");
        pdfExporter.exportLayout(pdfFile, layout);



    }

}