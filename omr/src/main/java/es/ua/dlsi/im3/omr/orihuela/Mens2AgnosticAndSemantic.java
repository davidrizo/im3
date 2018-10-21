package es.ua.dlsi.im3.omr.orihuela;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScorePart;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.kern.HumdrumMatrix;
import es.ua.dlsi.im3.core.score.io.kern.HumdrumMatrix2ScoreSong;
import es.ua.dlsi.im3.core.score.io.kern.MensImporter;
import es.ua.dlsi.im3.core.score.io.lilypond.LilypondExporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.Encoder;
import es.ua.dlsi.im3.omr.encoding.Exporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticExporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticExporter;

import java.io.File;
import java.io.IOException;

/**
 * It obtains the agnostic and semantic encoding given the **mens encoding
 * The comment !pb is used to indicate "page break" in separated parts mode, and !sb for system break
 */
public class Mens2AgnosticAndSemantic {
    public static final void main(String [] args) throws IM3Exception, IOException {
        if (args.length != 2) {
            throw new IM3Exception("Usage: <**mens file> <output folder, created if not exists>");
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            throw new IM3Exception("Input file '" + file.getAbsolutePath() + "' does not exist");
        }
        Mens2AgnosticAndSemantic mens2AgnosticAndSemantic = new Mens2AgnosticAndSemantic();

        File outputFolder = new File(args[1]);
        if (outputFolder.exists() && !outputFolder.isDirectory()) {
            throw new IM3Exception("The output folder exists and it is not a folder");
        }
        outputFolder.mkdirs();
        mens2AgnosticAndSemantic.run(file, outputFolder);
    }

    private void run(File file, File outputFolder) throws IM3Exception, IOException {
        MensImporter importer = new MensImporter();
        importer.setDebug(false);

        HumdrumMatrix humdrumMatrix = importer.importMens(file);
        HumdrumMatrix2ScoreSong humdrumMatrix2ScoreSong = new HumdrumMatrix2ScoreSong();
        ScoreSong scoreSong = humdrumMatrix2ScoreSong.convert(humdrumMatrix);

        String baseName = FileUtils.getFileWithoutPathOrExtension(file);
        // print complete score
        File lilypondFullScore = new File(outputFolder, baseName + "_fullscore.ly");
        LilypondExporter lilypondExporter = new LilypondExporter(true);
        lilypondExporter.exportSong(lilypondFullScore, scoreSong);

        //TODO Imprimir partes separadas
        for (ScorePart part: scoreSong.getParts()) {
            String name;
            if (part.getName() != null) {
                name = "part_" + part.getName();
            } else {
                name = "part_" + part.getNumber();
            }
            File lilypondPart = new File(outputFolder, baseName + "_" + name + ".ly");
            LilypondExporter lilypondExporterPart = new LilypondExporter(true);
            lilypondExporterPart.exportPart(lilypondPart, part);
        }

        Encoder encoder = new Encoder(AgnosticVersion.v2, true);
        encoder.encode(scoreSong);
        Exporter agnosticExporter = new AgnosticExporter(AgnosticVersion.v2);
        agnosticExporter.export(encoder.getAgnosticEncoding(), new File(outputFolder, "out.agnostic"));

        Exporter semanticExporter = new SemanticExporter();
        semanticExporter.export(encoder.getSemanticEncoding(), new File(outputFolder, "out.semantic"));

    }
}
