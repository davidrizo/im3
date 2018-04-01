package es.ua.dlsi.im3.analysis.analysis.analyzers.tonal;

import es.ua.dlsi.im3.analysis.analysis.analyzers.tonal.academic.melodic.MelodicAnalysis;
import es.ua.dlsi.im3.analysis.analysis.analyzers.tonal.academic.melodic.NoteMelodicAnalysis;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.TimedElementCollection;
import es.ua.dlsi.im3.core.score.AtomPitch;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.harmony.Harm;

import java.util.ArrayList;

/**
 * Instead of using the song embedded analysis we save them here in order to offer different analyses
 * to the same song
 * Created by drizo on 23/6/17.
 */
public class TonalAnalysis {
    ScoreSong song;
    MelodicAnalysis melodicAnalysis;
    TimedElementCollection<Harm> harms;

    public TonalAnalysis(ScoreSong song) {
        this.song = song;
        melodicAnalysis = new MelodicAnalysis(song);
        harms = new TimedElementCollection<>();
    }

    public void addHarm(Harm harm) throws IM3Exception {
        if (harms == null) {
            harms = new TimedElementCollection<>();
        }
        if (harm.getTime() == null) {
            throw new IM3Exception("The harm element " + harm + " has not time set");
        }
        harms.addValue(harm);
    }

    /**
     * May return null
     * @return
     */
    public ArrayList<Harm> getOrderedHarms() {
        if (harms == null) {
            return null;
        } else {
            return harms.getOrderedValues();
        }
    }

    public MelodicAnalysis getMelodicAnalysis() {
        return melodicAnalysis;
    }


    public void saveToSong() throws IM3Exception {
        song.clearHarms();
        for (Harm harm: harms.getValues()) {
            song.addHarm(harm);
        }
        melodicAnalysis.saveToSong();
    }


    public void loadFromSong() throws IM3Exception {
        harms = new TimedElementCollection<>();
        for (Harm harm: song.getOrderedHarms()) {
            harms.addValue(harm);
        }
        melodicAnalysis.loadFromSong();
    }

    public NoteMelodicAnalysis getMelodicAnalysis(AtomPitch note) {
        return melodicAnalysis.getAnalysis(note);
    }

    public Harm getHarmAtTimeOrNull(Time time) {
        return harms.getValueAtTimeOrNull(time);
    }
}
