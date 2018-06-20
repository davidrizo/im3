package es.ua.dlsi.im3.analysis.hierarchical.forms;

import static org.junit.Assert.*;

import java.io.File;

import es.ua.dlsi.im3.analysis.analysis.analyzers.tonal.TonalAnalysisException;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import org.junit.Before;
import org.junit.Test;


public class DummyFormAnalyzerTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAnalyze() throws Exception {
		MusicXMLImporter importer = new MusicXMLImporter();
		File file = TestFileUtils.getFile("/testdata/emptyanalyses/bwv0367.xml");
		//File file = TestFileUtils.getFile("/testdata/emptyanalyses/simple.xml");
		if (!file.exists()) {
			throw new TonalAnalysisException("File " + file.getAbsolutePath() + " does not exist");
		}
		ScoreSong song = importer.importSong(file);
		DummyFormAnalyzer analyzer = new DummyFormAnalyzer(new String[]{"A","B","A'"});
		FormAnalysis analysis = analyzer.analyze(song);
		assertEquals(3, analysis.getRootDivisions().size());
		System.out.println(analysis);
	}

}
