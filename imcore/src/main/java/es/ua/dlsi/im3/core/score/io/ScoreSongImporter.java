package es.ua.dlsi.im3.core.score.io;

import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.kern.KernImporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.io.FileType;
import es.ua.dlsi.im3.core.io.ImportException;

import java.io.File;

/**
 * Created by drizo on 9/6/17.
 */
public class ScoreSongImporter {
    public ScoreSong importSong(NotationType notationType, FileType fileType, File folder, String filename) throws ImportException {
        File file = new File(folder, filename);
        return importSong(notationType, fileType, file);
    }

    public ScoreSong importSong(NotationType notationType, FileType fileType, File file) throws ImportException {
        if (!file.exists()) {
            throw new ImportException("Input file '" + file.getAbsolutePath() + "' does not exist");
        }
        switch (fileType) {
            case musicxml:
                if (notationType == NotationType.eModern) {
                    MusicXMLImporter musicXMLImporter = new MusicXMLImporter();
                    return musicXMLImporter.importSong(file);
                } else {
                    throw new ImportException("Unsupported file type: " + fileType + " with notation type: " + notationType);
                }
            case mei:
                MEISongImporter meiImporter = new MEISongImporter();
                return meiImporter.importSong(file);
            case kern:
                KernImporter kernImporter = new KernImporter();
                return kernImporter.importSong(file);
            default:
                throw new ImportException("Unsupported file type: " + fileType);
        }
    }

}
