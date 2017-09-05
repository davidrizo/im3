package es.ua.dlsi.im3.experiments.analysis.tonalanalysis_mei_corpus;

import es.ua.dlsi.im3.analyzers.tonal.ImporterExporter;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * It reads the tonal analysis files used for CMA chapter (Interactive tonal analysis) encoded in MusicXML lyrics
 * and exports it to MEI
 */
public class MigrateMusicXMLLyricsFiles2MEI {
    public static final void main(String [] args) {
        if (args.length != 2) {
            System.err.println("Use: MigrateMusicXMLLyricsFiles2MEI <source folder> <target folder>");
            return;
        }

        MigrateMusicXMLLyricsFiles2MEI m = new MigrateMusicXMLLyricsFiles2MEI();
        File fromFolder = new File(args[0]);
        File toFolder = new File(args[1]);

        if (!toFolder.exists()) {
            toFolder.mkdirs();
        }

        try {
            m.run(fromFolder, toFolder);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void run(File fromFolder, File toFolder) throws IM3Exception, IOException, ExportException, ImportException {
        if (!fromFolder.exists()) {
            throw new IM3Exception("The source folder " + fromFolder.getAbsolutePath() + " does not exist");
        }

        if (!fromFolder.isDirectory()) {
            throw new IM3Exception("The source folder " + fromFolder.getAbsolutePath() + " is not a folder");
        }

        if (toFolder.exists() && !toFolder.isDirectory()) {
            throw new IM3Exception("The toFolder folder " + toFolder.getAbsolutePath() + " is not a folder");
        }

        ArrayList<File> fileList = new ArrayList<>();
        FileUtils.readFiles(fromFolder, fileList, "xml", false);

        for (File file: fileList) {
            String name = FileUtils.getFileWithoutPathOrExtension(file);

            //System.out.println("Folder " + folder.getName() + ", file " + file.getName());
            ImporterExporter importerExporter = new ImporterExporter();
            ScoreSong song = importerExporter.readMusicXML(file, false);
            MEISongExporter exporter = new MEISongExporter();
            exporter.setUseHarmTypes(true);

            File meiFile = new File(toFolder, name + ".mei");
            exporter.exportSong(meiFile, song);
        }

        /*File[] folders = FileUtils.listFolders(fromFolder);
        for (File folder : folders) {
            migrateFolder(folder, toFolder);
        }*/
    }

    /*private void migrateFolder(File folder, File toFolder) throws IOException, ImportException, ExportException {
        System.out.println("Folder " + folder);
        ArrayList<File> fileList = new ArrayList<>();

        FileUtils.readFiles(folder, fileList, "xml", false);
        File toSubFolder = new File(toFolder, folder.getName());

        if (!fileList.isEmpty()) { // avoid creating empty folders
            toSubFolder.mkdirs();
        }
        for (File file: fileList) {
            String name = FileUtils.getFileWithoutPathOrExtension(file);

            //System.out.println("Folder " + folder.getName() + ", file " + file.getName());
            ImporterExporter importerExporter = new ImporterExporter();
            ScoreSong song = importerExporter.readMusicXML(file, false);
            MEISongExporter exporter = new MEISongExporter();
            exporter.setUseHarmTypes(true);

            File meiFile = new File(toSubFolder, name + ".mei");
            exporter.exportSong(meiFile, song);
        }

        File[] folders = FileUtils.listFolders(folder);
        for (File subfolder : folders) {
            migrateFolder(subfolder, toSubFolder);
        }
    }*/
}
