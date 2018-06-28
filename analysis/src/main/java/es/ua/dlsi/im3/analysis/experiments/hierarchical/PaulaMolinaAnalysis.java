package es.ua.dlsi.im3.analysis.experiments.hierarchical;

import es.ua.dlsi.im3.analysis.hierarchical.Analysis;
import es.ua.dlsi.im3.analysis.hierarchical.HierarchicalAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.io.MEIHierarchicalAnalysesModernImporter;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.Harmony;
import es.ua.dlsi.im3.core.score.ScoreSong;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @autor drizo
 */
public class PaulaMolinaAnalysis {
    public static final void main(String [] args) throws ImportException {
        new PaulaMolinaAnalysis().run();
    }

    private void run() throws ImportException {
        File file = new File("/Users/drizo/Documents/EASD.A/docencia/alicante-2017-2018/inv/paula_molina_gonzalez_analisis/ultima_licion_zayas.mei");
        MEIHierarchicalAnalysesModernImporter importer = new MEIHierarchicalAnalysesModernImporter();
        importer.importSongAndAnalyses(file);

        ScoreSong song = importer.getScoreSong();
        ArrayList<Analysis> analyses = importer.getAnalyses();
        HierarchicalAnalysis<?> ha = analyses.get(0).getHierarchicalAnalyses().get(0);
        FormAnalysis formAnalysis = (FormAnalysis) ha;
        System.out.println("Form analysis");
        System.out.println(formAnalysis.getTree()); //TODO Sólo saca root ????

        System.out.println("Harmonies");
        ArrayList<Harmony> harmonies = song.getHarmoniesSortedByTime();
        System.out.println(harmonies); //TODO ¿por qué las saca vacías?
        //TODO ¿Queremos poner las armonías en el árbol?

    }
}
