package es.ua.dlsi.im3.core.score.io;

import es.ua.dlsi.im3.core.score.DurationEvaluator;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.kern.KernImporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.io.FileType;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.mensural.BinaryDurationEvaluator;

import java.io.File;

/**
 * Created by drizo on 9/6/17.
 */
public class ScoreSongImporter {
    public ScoreSong importSong(FileType fileType, File folder, String filename) throws ImportException {
        return importSong(fileType, folder, filename, new DurationEvaluator());
    }
    public ScoreSong importSong(FileType fileType, File folder, String filename, DurationEvaluator durationEvaluator) throws ImportException {
        File file = new File(folder, filename);
        return importSong(fileType, file, durationEvaluator);
    }

    public ScoreSong importSong(FileType fileType, File file, DurationEvaluator durationEvaluator) throws ImportException {
        if (!file.exists()) {
            throw new ImportException("Input file '" + file.getAbsolutePath() + "' does not exist");
        }
        switch (fileType) {
            case musicxml:
                MusicXMLImporter musicXMLImporter = new MusicXMLImporter();
                return musicXMLImporter.importSong(file);
            case mei:
                MEISongImporter meiImporter = new MEISongImporter(durationEvaluator);
                return meiImporter.importSong(file);
            case kern:
                KernImporter kernImporter = new KernImporter(durationEvaluator);
                return kernImporter.importSong(file);
            default:
                throw new ImportException("Unsupported file type: " + fileType);
        }
    }

    public ScoreSong importSong(File file, String extension, DurationEvaluator durationEvaluator) throws ImportException {
        if (!file.exists()) {
            throw new ImportException("Input file '" + file.getAbsolutePath() + "' does not exist");
        }
        switch (extension) {
            case "xml":
                MusicXMLImporter musicXMLImporter = new MusicXMLImporter();
                return musicXMLImporter.importSong(file);
            case "mei":
                MEISongImporter meiImporter = new MEISongImporter(durationEvaluator);
                return meiImporter.importSong(file);
            case "krn":
                KernImporter kernImporter = new KernImporter(durationEvaluator);
                return kernImporter.importSong(file);
            default:
                throw new ImportException("Unsupported file type: " + extension);
        }
    }

    public ScoreSong importSong(File file, String fileNameExtension) throws ImportException {
        return importSong(file, fileNameExtension, new DurationEvaluator());
    }
}
