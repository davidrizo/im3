package es.ua.dlsi.im3.omr.primus.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.Encoder;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticExporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticExporter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class PrIMUSChordsAndTupletsGenerator {
    public static final void main(String [] args) throws IOException {
        String _baseFolder = "/Users/drizo/cmg/investigacion/training_sets/sources/OMR/primus/original100kFicheros";
        String inputFile = "meiWithChordsAndTuplets.txt"; // it contains all MEI files with chords and tuplets
        String outputFile = "advance_agnostic_encoded_folders.txt"; // it contains all MEI files with chords and tuplets

        File baseFolder = new File(_baseFolder);
        FileOutputStream fos = new FileOutputStream(new File(baseFolder, outputFile));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));


        PrIMUSChordsAndTupletsGenerator prIMUSChordsAndTupletsGenerator = new PrIMUSChordsAndTupletsGenerator();
        Stream<String> stream = Files.lines(Paths.get(_baseFolder + "/" + inputFile));
        stream.forEach(s -> {
            try {
                String meiFolderGenerated = prIMUSChordsAndTupletsGenerator.generate(baseFolder, s);
                bw.write(meiFolderGenerated);
                bw.newLine();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        bw.close();

    }

    /**
     *
     * @param baseFolder
     * @param meiFile
     * @return Path of the folder where the mei has been generated
     * @throws IM3Exception
     * @throws IOException
     */
    private String generate(File baseFolder, String meiFile) throws IM3Exception, IOException {
        File file = new File(baseFolder, meiFile);
        System.out.println("Importing " + file.getName());
        MEISongImporter importer = new MEISongImporter(null);
        ScoreSong song = importer.importSong(file);

        String basename = FileUtils.getFileWithoutPathOrExtension(file);
        File agnostic = new File(file.getParent(), basename + ".advance.agnostic");

        System.out.println("Generating " + agnostic.getAbsolutePath());

        Encoder encoder = new Encoder(AgnosticVersion.v3_advance, true);
        encoder.encode(song);
        AgnosticExporter agnosticExporter = new AgnosticExporter(AgnosticVersion.v3_advance);
        agnosticExporter.export(encoder.getAgnosticEncoding(), agnostic);

        return file.getParent();
        ///KernExporter kernExporter = new KernExporter();
        ///kernExporter.exportSong(kern, song);

    }
}
