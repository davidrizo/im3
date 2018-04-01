package es.ua.dlsi.im3.analysis.experiments.analysis.analysis.tonalanalysis_mei_corpus;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.kern.KernImporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * It reads the tonal analysis files in **kern format using **harm and **root spines
 * and exports it to MEI
 */
public class MigrateKernHarmFiles2MEI {
    public static final void main(String [] args) {
        if (args.length != 2) {
            System.err.println("Use: MigrateKernHarmFiles2MEI <source folder> <target folder>");
            return;
        }

        MigrateKernHarmFiles2MEI m = new MigrateKernHarmFiles2MEI();
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

    private void run(File fromFolder, File toFolder) throws IM3Exception, IOException {
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
        FileUtils.readFiles(fromFolder, fileList, "krn", false);

        for (File file: fileList) {
            System.out.println("Processing file " + file.getName());
            String name = FileUtils.getFileWithoutPathOrExtension(file);

            KernImporter importer = new KernImporter();
            ScoreSong song = importer.importSong(file);
            MEISongExporter exporter = new MEISongExporter();
            exporter.setUseHarmTypes(true);

            File meiFile = new File(toFolder, name + ".mei");
            exporter.exportSong(meiFile, song);
        }
    }
}
