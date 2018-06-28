package es.ua.dlsi.im3.omr.jazzmus;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class MusicXML2SemanticAndAgnosticTest {
    @Test
    public void run() throws IOException, IM3Exception {
        File file = TestFileUtils.getFile("/testdata/jazzmus/milesdavis_dig.xml");

        MusicXML2SemanticAndAgnostic musicXML2SemanticAndAgnostic = new MusicXML2SemanticAndAgnostic();
        File agnosticFile = TestFileUtils.createTempFile("jazzmus_agnostic.txt");
        File semanticFile = TestFileUtils.createTempFile("jazzmus_semantic.txt");
        File kernFile = TestFileUtils.createTempFile("jazzmus_kern.txt");
        musicXML2SemanticAndAgnostic.run(file, semanticFile, agnosticFile, kernFile);
    }
}