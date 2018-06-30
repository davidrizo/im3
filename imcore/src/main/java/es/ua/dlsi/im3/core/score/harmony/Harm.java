package es.ua.dlsi.im3.core.score.harmony;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;

import java.util.ArrayList;

/**
 * Created by drizo on 20/6/17.
 */
public class Harm implements ITimedElement {
    Key key;

    Time time;
    /**
     * When using V7/ii, chordSpecifications[0] = V7, chordSpecifications[1] = ii
     */
    ArrayList<ChordSpecification> chordSpecifications;

    Harm alternate;

    TonalFunction tonalFunction;

    public Harm(Key key, ChordSpecification chordSpecification) {
        chordSpecifications = new ArrayList<>();
        this.key = key;
        chordSpecifications.add(chordSpecification);
    }

    public void addChordSpecification(ChordSpecification chordSpecification) {
        chordSpecifications.add(chordSpecification);
    }

    @Override
    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public void move(Time offset) {
        this.time = time.add(offset);
    }
    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public ArrayList<ChordSpecification> getChordSpecifications() {
        return chordSpecifications;
    }

    public Harm getAlternate() {
        return alternate;
    }

    public void setAlternate(Harm alternate) {
        this.alternate = alternate;
    }


    public TonalFunction getTonalFunction() {
        return tonalFunction;
    }

    public void setTonalFunction(TonalFunction tonalFunction) {
        this.tonalFunction = tonalFunction;
    }

    @Override
    public String toString() {
        return "Harm{" +
                "key=" + key +
                ", time=" + time +
                ", chordSpecifications=" + chordSpecifications +
                ", alternate=" + alternate +
                '}';
    }

    public ScaleMembership belongsToChord(PitchClass pitchClass, MotionDirection direction, boolean isLastMeasure) {
        //TODO
        return null;
    }
}
