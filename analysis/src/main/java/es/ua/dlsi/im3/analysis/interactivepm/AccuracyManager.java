package es.ua.dlsi.im3.analysis.interactivepm;

//TODO Pruebas unitarias

import java.util.ArrayList;

/**
 * On a classical scheme, the error rate is the number of errors / number of items
 * On the muret approach, the error rate is the number of interactions / number of items
 * @author drizo
 *
 */
public class AccuracyManager {
    private final long initialTotalCount;
    private final long initialErrorCount;
    private long currentErrorCount;
    ArrayList<Interaction> interactions;

    public AccuracyManager(long totalCount, long errorCount) {
        this.initialTotalCount = totalCount;
        this.initialErrorCount = errorCount;
        this.currentErrorCount = errorCount;
        //System.err.println("Creando " + this.toString());		//TODO Quitar mensaje

        this.interactions = new ArrayList<>();
    }

    public void addInteraction(Interaction interaction) {
        this.interactions.add(interaction);
    }

    public double getInitialErrorRate() {
        return (double) initialErrorCount / (double) initialTotalCount;
    }

    public double getInitialSuccessRate() {
        return (double) (initialTotalCount - initialErrorCount) / (double) initialTotalCount;
    }

    public double getCurrentErrorRate() {
        return (double) currentErrorCount / (double) initialTotalCount;
    }

    public double getCurrentSuccessRate() {
        if (initialErrorCount < currentErrorCount) {
            throw new RuntimeException("Error in accuracy manager: initialErrorCount = " +initialErrorCount + ", currentErrorCount=" + currentErrorCount);
        }
        return (double) (initialTotalCount - currentErrorCount) / (double) initialTotalCount;
    }

    public double getInteractiveApproachErrorRate() {
        return (double) (interactions.size() + currentErrorCount) / (double) initialTotalCount;
    }

    public double getInteractiveApproachSuccessRate() {
        int ia = interactions.size();
        return (double) (initialTotalCount - ia - currentErrorCount) / (double) initialTotalCount;
    }

    public double getInteractiveApproachImprovement() {
        return this.getInitialErrorRate() - getInteractiveApproachErrorRate();
    }
    //TODO Poder guardar las interacciones

    public void setCurrentErrorCount(long currentErrorCount) {
        this.currentErrorCount = currentErrorCount;
    }

    public void clearInteractions() {
        this.interactions.clear();
    }
}
