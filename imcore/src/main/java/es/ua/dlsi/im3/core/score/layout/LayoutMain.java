package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.io.ScoreSongImporter;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LayoutMain {
    public static final void main(String [] args) throws IOException, IM3Exception {
        // TODO: 16/10/17 Que se pueda elegir el tipo de renderización y salida

        if (args.length != 2) {
            System.err.println("Use LayoutMain: <input file> <output pdf file>");
        }

        ScoreSongImporter importer = new ScoreSongImporter();
        ScoreSong scoreSong = importer.importSong(new File(args[0]), FileUtils.getFileNameExtension(args[0]));

        // TODO: 16/10/17 Tamaño
        AutomaticPageLayout layout = new AutomaticPageLayout(scoreSong, scoreSong.getStaves(), true, new CoordinateComponent(5000), new CoordinateComponent(5000));
        layout.layout(true);
        PDFExporter pdfExporter = new PDFExporter();
        pdfExporter.exportLayout(new File(args[1]), layout);
        System.out.println("Done!");

    }
}
