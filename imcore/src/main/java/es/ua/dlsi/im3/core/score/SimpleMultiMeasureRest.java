package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;
import org.apache.commons.lang3.math.Fraction;

import java.util.ArrayList;
import java.util.List;

public class SimpleMultiMeasureRest extends SimpleRest {
	/**
	 * Number of measures
	 */
	int numMeasures;
	public SimpleMultiMeasureRest(Time measureDuration, int numMeasures) {
		this(Figures.WHOLE, measureDuration, numMeasures);
	}
	/**
	 *
	 * @param measureDuration
	 */
	public SimpleMultiMeasureRest(Figures figure, Time measureDuration, int numMeasures) {
		super(figure, 0, measureDuration.multiplyBy(Fraction.getFraction(numMeasures, 1)));
		this.numMeasures = numMeasures;
	}

	//TODO Test unitario
	public SimpleMultiMeasureRest(SimpleMultiMeasureRest simpleMultiMeasureRest) {
		super(simpleMultiMeasureRest.getAtomFigure().getFigure(), 0, simpleMultiMeasureRest.getDuration());
		this.numMeasures = simpleMultiMeasureRest.getNumMeasures();
	}

	public SimpleMultiMeasureRest clone() {
		return new SimpleMultiMeasureRest(this);
	}

	@Override
	public String toString() {
		return super.toString() + " " + numMeasures + " measures rest";
	}

	public void setFigure(Figures figure) {
		this.atomFigure.setFigure(figure);
	}

	public int getNumMeasures() {
		return numMeasures;
	}

    /**
     * Create SimpleMeasureRests for each measure it takes
     * @return
     */
    public List<Atom> getAtoms() {
        List<Atom> subatoms = new ArrayList<>();
        Time dur = getDuration().divide(numMeasures);
        Time time = this.getTime();
        for (int i=0; i<numMeasures; i++) {
            SimpleMeasureRest simpleMeasureRest = new SimpleMeasureRest(Figures.WHOLE, dur);
            simpleMeasureRest.setTime(time);
            subatoms.add(simpleMeasureRest);
            time = time.add(dur);
        }
        return subatoms;
    }
}
