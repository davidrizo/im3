package es.ua.dlsi.im3.mavr.model.graph;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.mavr.model.harmony.IntegerHSB;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Example of use of GeneticColorGraphSolver using the Laura Molina's analysis of Última Lición for her Master's Thesis
 * @autor drizo
 */
public class LauraMolinaUltimaLicionAnalysisColors {
    public static final void main(String [] args) throws IM3Exception, FileNotFoundException {
        ConceptGraph<String> metricalUnitGraph = new ConceptGraph<>();
        metricalUnitGraph.addConcept("1");
        metricalUnitGraph.addConcept("2");
        metricalUnitGraph.addConcept("3");
        metricalUnitGraph.addConcept("4");

        metricalUnitGraph.addConcept("4=1");
        metricalUnitGraph.relateConcepts("4=1", "1", 0.9);
        metricalUnitGraph.relateConcepts("4=1", "4", 0.9);

        metricalUnitGraph.addConcept("4=2");
        metricalUnitGraph.relateConcepts("4=2", "2", 0.9);
        metricalUnitGraph.relateConcepts("4=2", "4", 0.9);

        metricalUnitGraph.addConcept("4=3");
        metricalUnitGraph.relateConcepts("4=3", "3", 0.9);
        metricalUnitGraph.relateConcepts("4=3", "4", 0.9);

        metricalUnitGraph.addConcept("(2)");
        metricalUnitGraph.relateConcepts("(2)", "2", 0.9);

        metricalUnitGraph.addConcept("(4)");
        metricalUnitGraph.relateConcepts("(4)", "4", 0.9);

        metricalUnitGraph.addConcept("(4=1)");
        metricalUnitGraph.relateConcepts("(4=1)", "4=1", 0.9);

        metricalUnitGraph.addConcept("(4=3)");
        metricalUnitGraph.relateConcepts("(4=3)", "4=3", 0.9);

        metricalUnitGraph.addConcept("(3)");
        metricalUnitGraph.relateConcepts("(3)", "3", 0.9);

        metricalUnitGraph.addConcept("(3')");
        metricalUnitGraph.relateConcepts("(3')", "(3)", 0.9);

        metricalUnitGraph.addConcept("(3'')");
        metricalUnitGraph.relateConcepts("(3'')", "(3')", 0.9);

        metricalUnitGraph.addConcept("(3''')");
        metricalUnitGraph.relateConcepts("(3''')", "(3'')", 0.9);

        ColorGraphSolver colorGraphSolver = new ColorGraphSolver(metricalUnitGraph);
        colorGraphSolver.findColors();

        metricalUnitGraph.getGraph().writeDot(new File("/tmp/ultimalicion_automatic.dot"), false);

        setColor(metricalUnitGraph, "1", 0, 100, 100);
        setColor(metricalUnitGraph, "2", 90, 100, 100);
        setColor(metricalUnitGraph, "(2)", 90, 90, 90);
        setColor(metricalUnitGraph, "4", 180, 100, 100);
        setColor(metricalUnitGraph, "(4)", 180, 90, 90);
        setColor(metricalUnitGraph, "3", 270, 100, 100);
        setColor(metricalUnitGraph, "(3)", 270, 90, 90);
        setColor(metricalUnitGraph, "(3')", 270, 80, 80);
        setColor(metricalUnitGraph, "(3'')", 270, 70, 70);
        setColor(metricalUnitGraph, "(3''')", 270, 60, 60);

        setColor(metricalUnitGraph, "4=1", 0, 90, 90); //TODO (como si fuera (1)
        setColor(metricalUnitGraph, "(4=1)", 0, 80, 80); //TODO
        setColor(metricalUnitGraph, "4=2", (180+90)/2, 90, 90);
        setColor(metricalUnitGraph, "4=3", (180+270)/2, 90, 90);
        setColor(metricalUnitGraph, "(4=3)", (180+270)/2, 80, 80);
        metricalUnitGraph.getGraph().writeDot(new File("/tmp/ultimalicion_manual.dot"), false);



    }

    private static void setColor(ConceptGraph<String> metricalUnitGraph, String concept, int hue, int saturation, int brightness) throws IM3Exception {
        IntegerHSB integerHSB = new IntegerHSB(hue, saturation, brightness);
        metricalUnitGraph.getNode(concept).setColor(integerHSB.toLAB().hex());
    }
}
