package es.ua.dlsi.im3.core.score.mensural;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.conversions.MensuralToModern;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.ScoreSongImporter;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.PageLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import es.ua.dlsi.im3.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LayoutMensuralAndTranscription {
    public static final void main(String [] args) throws IOException, IM3Exception {
        // TODO: 16/10/17 Que se pueda elegir el tipo de renderización y salida

        if (args.length != 2) {
            System.err.println("Use LayoutMensuralAndTranscription: <input file> <output file>");
        }

        ScoreSongImporter importer = new ScoreSongImporter();
        ScoreSong mensural = importer.importSong(new File(args[0]), FileUtils.getFileNameExtension(args[0]), new BinaryDurationEvaluator(new Time(2)));

        MensuralToModern mensuralToModern = new MensuralToModern();
        //TODO Parámetro
        //ScoreSong modern = mensuralToModern.convertIntoNewSong(mensural, Intervals.FOURTH_PERFECT_DESC); // ésta genera más sostenidos
        ScoreSong modern = mensuralToModern.convertIntoNewSong(mensural, Intervals.FIFTH_PERFECT_DESC);
        mensuralToModern.merge(mensural, modern, true);

        // TODO: 16/10/17 Poder cambiar esto
        HashMap<Staff, LayoutFonts> fontsHashMap = new HashMap<>();
        for (Staff staff: mensural.getStaves()) {
            if (staff.getNotationType() == NotationType.eMensural) {
                fontsHashMap.put(staff, LayoutFonts.capitan);
            } else if (staff.getNotationType() == NotationType.eModern) {
                fontsHashMap.put(staff, LayoutFonts.bravura);
            } else {
                throw new IM3Exception("The staff " + staff + " has not a notation type");
            }
        }

        mensural.debugPutIDsAsLyrics(); // FIXME: 17/10/17 Quitar
        // TODO: 16/10/17 Tamaño
        //PageLayout layout = new PageLayout(mensural, fontsHashMap, new CoordinateComponent(5000), new CoordinateComponent(5000));
        HorizontalLayout layout = new HorizontalLayout(mensural, fontsHashMap, new CoordinateComponent(20000), new CoordinateComponent(800));
        layout.layout();
        //PDFExporter pdfExporter = new PDFExporter();
        //pdfExporter.exportLayout(new File(args[1]), layout);
        SVGExporter svgExporter = new SVGExporter();
        svgExporter.exportLayout(new File(args[1]), layout);
        System.out.println("Done!");

    }
}
