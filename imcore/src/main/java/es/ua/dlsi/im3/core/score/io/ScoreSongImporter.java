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
    public ScoreSong importSong(FileType fileType, File folder, String filename) throws ImportException {
        File file = new File(folder, filename);
        return importSong(fileType, file);
    }

    public ScoreSong importSong(FileType fileType, File file) throws ImportException {
        if (!file.exists()) {
            throw new ImportException("Input file '" + file.getAbsolutePath() + "' does not exist");
        }
        switch (fileType) {
            case musicxml:
                MusicXMLImporter musicXMLImporter = new MusicXMLImporter();
                return musicXMLImporter.importSong(file);
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

    public ScoreSong importSong(File file, String extension) throws ImportException {
        if (!file.exists()) {
            throw new ImportException("Input file '" + file.getAbsolutePath() + "' does not exist");
        }
        switch (extension) {
            case "xml":
                MusicXMLImporter musicXMLImporter = new MusicXMLImporter();
                return musicXMLImporter.importSong(file);
            case "mei":
                MEISongImporter meiImporter = new MEISongImporter();
                return meiImporter.importSong(file);
            case "krn":
                KernImporter kernImporter = new KernImporter();
                return kernImporter.importSong(file);
            default:
                throw new ImportException("Unsupported file type: " + extension);
        }
    }
}
