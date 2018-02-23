package es.ua.dlsi.im3.core.adt.wordgraph.construction;

import es.ua.dlsi.im3.core.adt.wordgraph.ClassesIndex;

import java.math.BigDecimal;

/**
 * The input to build the wordgraph is a matrix where each row represents a classification class and each column is
 * a frame to classify. The cell contains the logarithm of the probability
 * @param <ClassificationClassType>
 */
public class Input<ClassificationClassType> {
    private final ClassesIndex<ClassificationClassType> classesIndex;
    /**
     * logProbabilties[frame][index of class]
     */
    private final BigDecimal [][] logProbabilities;

    /**
     * @param classesIndex
     * @param logProbabilities logProbabilties[frame][index of class]
     */
    public Input(ClassesIndex<ClassificationClassType> classesIndex, BigDecimal [][] logProbabilities) {
        this.logProbabilities = logProbabilities;
        this.classesIndex = classesIndex;
    }

    public ClassesIndex<ClassificationClassType> getClassesIndex() {
        return classesIndex;
    }

    public BigDecimal[][] getLogProbabilities() {
        return logProbabilities;
    }
}
