package es.ua.dlsi.im3.omr.jazzmus;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.conversions.MusicXML2AgnosticAndSemantic;
import es.ua.dlsi.im3.omr.encoding.Encoder;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticExporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticExporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * It takes a MusicXML with system break tags and exports it to a semantic and agnostic file, where
 * each line contains an staff. It leaves the file in the same folder than the input xml file
 * @autor drizo
 */
public class MusicXML2SemanticAndAgnostic {
    public static final void main(String [] args) throws Exception {
        String inputFolder;
        if (args.length > 1) {
            throw new Exception("Missing folder with MusicXML files");
        } else if (args.length == 0){
            inputFolder = "/Users/drizo/Documents/GCLOUDUA/HISPAMUS/repositorios/jazzmus/dataset";
        } else {
            inputFolder = args[0];
        }

        MusicXML2AgnosticAndSemantic musicXML2AgnosticAndSemantic = new MusicXML2AgnosticAndSemantic();
        musicXML2AgnosticAndSemantic.convertFolder(inputFolder, NotationType.eModern);
    }
}
