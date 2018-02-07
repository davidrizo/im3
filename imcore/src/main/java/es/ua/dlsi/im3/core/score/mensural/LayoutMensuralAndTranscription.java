package es.ua.dlsi.im3.core.score.mensural;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.conversions.MensuralToModern;
import es.ua.dlsi.im3.core.conversions.ScoreToPlayed;
import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.played.SongTrack;
import es.ua.dlsi.im3.core.played.io.MidiSongExporter;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefC2;
import es.ua.dlsi.im3.core.score.clefs.ClefC3;
import es.ua.dlsi.im3.core.score.clefs.ClefF4;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.io.ScoreSongImporter;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.core.score.io.lilypond.LilypondExporter;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.PageLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import es.ua.dlsi.im3.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LayoutMensuralAndTranscription {
    public static final void main(String [] args) throws IOException, IM3Exception {
        // TODO: 16/10/17 Que se pueda elegir el tipo de renderización y salida

        if (args.length != 5) {
            System.err.println("Use LayoutMensuralAndTranscription: <input file> <output svg file (it generates also parts)> <output midi file> <output ly file> <output krn>");
        }

        System.out.println("Input: " + args[0]);
        ScoreSongImporter importer = new ScoreSongImporter();
        ScoreSong mensural = importer.importSong(new File(args[0]), FileUtils.getFileNameExtension(args[0]), new BinaryDurationEvaluator(new Time(2)));

        // exportamos también a lilypond
        LilypondExporter lilypondExporter = new LilypondExporter();
        File lyFile = new File(args[3]);
        lilypondExporter.exportSong(lyFile, mensural);

        // exportamos también a lilypond
        KernExporter kernExporter = new KernExporter();
        File kernFile = new File(args[4]);
        kernExporter.exportSong(kernFile, mensural);


        // TODO: 2/11/17 Esto deberá seguir unas normas - no éstas puestas casi a piñón
        if (mensural.getStaves().size() != 8) {
            throw new IM3Exception("TO-DO ESTO ESTÁ HECHO PARA PATRIARCA!!!! - CAMBIAR MAPAS DE CLAVES DE FORMA INTERACTIVA O CON REGLAS");
        }

        Clef [] modernClefs = new Clef [] {
            new ClefG2(), new ClefG2(), new ClefG2(), new ClefF4(),
            new ClefG2(), new ClefG2(), new ClefF4(), new ClefF4()
        };

        MensuralToModern mensuralToModern = new MensuralToModern(modernClefs);
        //TODO Parámetro
        //ScoreSong modern = mensuralToModern.convertIntoNewSong(mensural, Intervals.FOURTH_PERFECT_DESC); // ésta genera más sostenidos
        ScoreSong modern = mensuralToModern.convertIntoNewSong(mensural, Intervals.FIFTH_PERFECT_DESC);

        //ScoreSong modern = mensuralToModern.convertIntoNewSong(mensural, Intervals.UNISON_PERFECT);
        String midiFile = args[2];
        ScoreToPlayed scoreToPlayed = new ScoreToPlayed();
        PlayedSong played = scoreToPlayed.createPlayedSongFromScore(modern);

        MidiSongExporter exporter = new MidiSongExporter();
        exporter.addResetEWSCWordBuilderMessage(); // TODO: 29/10/17 Generalizar esto
        exporter.exportSong(new File(midiFile), played);

        mensuralToModern.merge(mensural, modern);

        // TODO: 16/10/17 Poder cambiar esto
        HashMap<Staff, LayoutFonts> fontsHashMap = new HashMap<>();
        for (Staff staff: mensural.getStaves()) {
            if (staff.getNotationType() == NotationType.eMensural) {
                fontsHashMap.put(staff, LayoutFonts.patriarca);
            } else if (staff.getNotationType() == NotationType.eModern) {
                fontsHashMap.put(staff, LayoutFonts.bravura);
            } else {
                throw new IM3Exception("The staff " + staff + " has not a notation type");
            }
        }

        mensural.debugPutIDsAsLyrics(); // FIXME: 17/10/17 Quitar
        // TODO: 16/10/17 Tamaño
        //PageLayout layout = new PageLayout(mensural, fontsHashMap, new CoordinateComponent(5000), new CoordinateComponent(5000));
        HorizontalLayout hlayout = new HorizontalLayout(mensural, fontsHashMap, new CoordinateComponent(40000), new CoordinateComponent(2800));
        hlayout.layout();
        //PDFExporter pdfExporter = new PDFExporter();
        //pdfExporter.exportLayout(new File(args[1]), layout);
        SVGExporter svgExporter = new SVGExporter();
        File svgHorizontalFile = new File(args[1]);
        svgExporter.exportLayout(svgHorizontalFile, hlayout);
        System.out.println("Done!");


        // Generate pages for all parts
        String svgFileName = FileUtils.getFileWithoutPathOrExtension(svgHorizontalFile);
        for (ScorePart part: mensural.getParts()) {
            String partSVGNamePrefix = svgFileName + "_" + FileUtils.leaveValidCaracters(part.getName());
            PageLayout pageLayout = new PageLayout(mensural, part.getStaves(), true, fontsHashMap,
                    new CoordinateComponent(30000), new CoordinateComponent(4800));
            pageLayout.layout();
            int pageNumber = 1;
            for (Canvas canvas: pageLayout.getCanvases()) {
                SVGExporter svgExporterPage = new SVGExporter();
                // TODO: 20/11/17 He quitado los page breaks
                //File pageFile = new File(svgHorizontalFile.getParent(), partSVGNamePrefix + "_page_" + pageNumber + ".svg");
                File pageFile = new File(svgHorizontalFile.getParent(), partSVGNamePrefix + ".svg");
                svgExporterPage.exportLayout(pageFile, canvas, pageLayout);
                pageNumber++;
            }


        }
    }
}
