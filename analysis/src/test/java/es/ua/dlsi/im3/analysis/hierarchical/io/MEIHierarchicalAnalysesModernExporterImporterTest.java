package es.ua.dlsi.im3.analysis.hierarchical.io;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import es.ua.dlsi.im3.analysis.hierarchical.Analysis;
import es.ua.dlsi.im3.analysis.hierarchical.FreeAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.DummyFormAnalyzer;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.motives.DummyMotivesAnalyzer;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotivesAnalysis;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import org.junit.Before;
import org.junit.Test;

public class MEIHierarchicalAnalysesModernExporterImporterTest {

	@Before
	public void setUp() throws Exception {
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

}
