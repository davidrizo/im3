package es.ua.dlsi.im3.analysis.hierarchical.io;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import es.ua.dlsi.im3.analysis.hierarchical.Analysis;
import es.ua.dlsi.im3.analysis.hierarchical.FreeAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.DummyFormAnalyzer;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysisTreeNodeLabel;
import es.ua.dlsi.im3.analysis.hierarchical.gui.FormAndMotivesAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.layout.FormTreeAnalysisLayout;
import es.ua.dlsi.im3.analysis.hierarchical.motives.DummyMotivesAnalyzer;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotivesAnalysis;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.fonts.BravuraFont;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.RGBA;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import org.junit.Before;
import org.junit.Test;

public class MEIHierarchicalAnalysesModernExporterImporterTest {

	@Before
	public void setUp() {
	}

	@Test
	public void testExportSongFileScoreSong() throws Exception {
		File f = TestFileUtils.getFile("/testdata/io/mei_3.0.0/81.xml");
		MusicXMLImporter xmlImporter = new MusicXMLImporter();
		ScoreSong song = xmlImporter.importSong(f);
		
		FreeAnalysis dummyFormsAnalysis = new FreeAnalysis();
		dummyFormsAnalysis.setAuthor("drizo");
		dummyFormsAnalysis.setDate(new Date());
		dummyFormsAnalysis.setName("Dummy");
		dummyFormsAnalysis.setScoreSong(song);
		DummyFormAnalyzer analyzer = new DummyFormAnalyzer(new String[] {"A", "B", "A'"});
		FormAnalysis formAnalysis = analyzer.analyze(song);
		dummyFormsAnalysis.addAnalysis(formAnalysis);
		
		DummyMotivesAnalyzer dma = new DummyMotivesAnalyzer();
		MelodicMotivesAnalysis ma = dma.analyze(song);
		dummyFormsAnalysis.addAnalysis(ma);
		
		MEIHierarchicalAnalysesModernExporter exporter = new MEIHierarchicalAnalysesModernExporter(dummyFormsAnalysis);
		
		File exportedFile = new File("/tmp/analyses81.mei");
		exporter.exportSong(exportedFile, song);
		
		MEIHierarchicalAnalysesModernImporter importer = new MEIHierarchicalAnalysesModernImporter();
		importer.importSongAndAnalyses(exportedFile);
		ArrayList<Analysis> importedAnalysis = importer.getAnalyses();
		assertEquals(1, importedAnalysis.size());
	}

    @Test
    public void hierarchicalFormAnalysis() throws Exception {
        MEIHierarchicalAnalysesModernImporter importer = new MEIHierarchicalAnalysesModernImporter();
        File f = TestFileUtils.getFile("/testdata/hierarchical/ultima_licion_zayas.mei");
        importer.importSongAndAnalyses(f);
        ScoreSong scoreSong = importer.getScoreSong();

        FormAndMotivesAnalysis formAndMotivesAnalysis = (FormAndMotivesAnalysis) importer.getAnalyses().get(0);
        FormAnalysis formAnalysis = formAndMotivesAnalysis.getFormAnalysis();

        HorizontalLayout layout = new HorizontalLayout(scoreSong, LayoutFonts.bravura,
                new CoordinateComponent(38000), new CoordinateComponent(700));

        double separation = 100;
        layout.setTopMargin(formAnalysis.getTree().getDepth() * separation);
        layout.layout(true);

        Tree<FormAnalysisTreeNodeLabel> tree = formAnalysis.getTree();
        LayoutFont layoutFont = new BravuraFont();
        FormTreeAnalysisLayout analysisLayout = new FormTreeAnalysisLayout(layoutFont, tree, importer.getScoreSong());
        analysisLayout.drawAnalysisInCanvas(layout, separation, new RGBA(0, 0, 1, 1));

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("licion.svg");
        svgExporter.exportLayout(svgFile, layout);

        PDFExporter pdfExporter = new PDFExporter();
        File pdfFile = TestFileUtils.createTempFile("licion.pdf");
        pdfExporter.exportLayout(pdfFile, layout);
    }

}
