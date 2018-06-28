package es.ua.dlsi.im3.analysis.analysis.analyzers.tonal.academic.melodic;

import es.ua.dlsi.im3.analysis.analysis.analyzers.tonal.TonalAnalysis;
import es.ua.dlsi.im3.core.IProgressObserver;
import es.ua.dlsi.im3.core.score.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 * @author drizo
 * @date 26/11/2011
 */
public class MelodicAnalyzerSimple extends MelodicAnalyzer {

    protected MelodicAnalyzerFeaturesExtractor featuresExtractor;

    public MelodicAnalyzerSimple(String name, double authority) {
        super(name, authority);
        featuresExtractor = new MelodicAnalyzerFeaturesExtractor(//TODO Que realmente se use
                MelodicFeatures.PREV_INTERVAL_NAME, MelodicFeatures.PREV_INTERVAL_MODE, MelodicFeatures.PREV_INTERVAL_DIR,
                MelodicFeatures.NEXT_INTERVAL_NAME, MelodicFeatures.NEXT_INTERVAL_MODE, MelodicFeatures.NEXT_INTERVAL_DIR,
                MelodicFeatures.TIED_FROM_PREVIOUS,
                //20160314 MelodicFeatures.DURATION,
                MelodicFeatures.RATIO,
                MelodicFeatures.INSTABILITY, MelodicFeatures.NEXT_INSTABILITY, MelodicFeatures.METER_NUMERATOR
        );
    }

    public MelodicAnalyzerSimple(String name) {
        this(name, 1);
    }

    @Override
    public String getNameAbbr() {
        return "SMP";
    }


    //TODO COMPROBAR ESTO BIEN - ADAPTARLO TAMBIÉN A LAS ETIQUETAS DEL MEI

    @Override
    public MelodicAnalysis melodicAnalysis(ScoreSong song, IProgressObserver o, List<Segment> alreadyComputedSonorities)
            throws MelodicAnalysisException {

        TonalAnalysis tonalAnalysis = new TonalAnalysis(song);
        MelodicAnalysis melodicAnalysis = tonalAnalysis.getMelodicAnalysis();

        HashMap<AtomPitch, NoteMelodicAnalysisFeatures> features = featuresExtractor.computeFeatures(song, alreadyComputedSonorities, tonalAnalysis);

        TreeSet<Interval> SECONDS = new TreeSet<Interval>();
        SECONDS.add(Intervals.SECOND_MINOR_DESC.createInterval());
        SECONDS.add(Intervals.SECOND_MAJOR_DESC.createInterval());
        SECONDS.add(Intervals.SECOND_MINOR_ASC.createInterval());

        TreeSet<Interval> SECONDS_DESC = new TreeSet<Interval>();
        SECONDS_DESC.add(Intervals.SECOND_MINOR_DESC.createInterval());
        SECONDS_DESC.add(Intervals.SECOND_MAJOR_DESC.createInterval());

        TreeSet<Interval> SECONDS_ASC = new TreeSet<Interval>();
        SECONDS_ASC.add(Intervals.SECOND_MINOR_ASC.createInterval());
        SECONDS_ASC.add(Intervals.SECOND_MAJOR_ASC.createInterval());
        Interval UNISON = Intervals.UNISON_PERFECT.createInterval();

        //Interval [] appSet = {new Interval(-2, 'm'), new Interval(-2, 'M'), new Interval(2, 'm')};
        List<AtomPitch> notes = song.getAtomPitches();
        long total = notes.size();
        long progress = 0;
        IntervalEmpty intervalEmpty = new IntervalEmpty();
        for (AtomPitch note: notes) {
            NoteMelodicAnalysisFeatures nf = features.get(note);
            if (nf != null) {
                if (nf.isStrong() && nf.getPrevInterval().equals(UNISON) && SECONDS.contains(nf.getNextInterval())) {
                    if (nf.isTied()) {
                        setMelodicAnalysis(melodicAnalysis, note, MelodicAnalysisNoteKinds.APPOGIATURA, false, nf, "R1: strong ^ pi=1 ^ ni=2 ^ �tied"); //TODO Poner en lugar de R1 isStrong && ... como tenemos en paper
                    } else {
                        setMelodicAnalysis(melodicAnalysis, note, MelodicAnalysisNoteKinds.SUSPENSION, false, nf, "R2: strong ^ pi=1 ^ ni=2 ^ tied");
                    }
                } else if (!nf.getPrevInterval().equals(intervalEmpty) && !nf.getNextInterval().equals(intervalEmpty)) {
                    if (!nf.isStrong() && nf.getRatio() <= 1.0) { // passing tone && neighbor tone
                        if (SECONDS_DESC.contains(nf.getPrevInterval()) && SECONDS_ASC.contains(nf.getNextInterval()) || SECONDS_ASC.contains(nf.getPrevInterval()) && SECONDS_DESC.contains(nf.getNextInterval())) {
                            //URGENT if (getMelodicAnalysis(prev).getKind() == MelodicAnalysisNoteKinds.HARMONIC) { Lo Quito para compilar
                            // this rule is new, it was not in the ICMC'07 Paper
                            //setMelodicAnalysis(snote, MelodicAnalysisNoteKinds.NEIGHBOUR_TONE, false, features, "R3: �strong ^ ratio <= 1 ^ (pi = -2 ^ ni = +2 v pi = +2 ^ ni = -2) ^ prevNote = h"); //TODO
                            setMelodicAnalysis(melodicAnalysis, note, MelodicAnalysisNoteKinds.NEIGHBOUR_TONE, false, nf, "R3: �strong ^ ratio <= 1 ^ (pi = -2 ^ ni = +2 v pi = +2 ^ ni = -2)"); //TODO
                            //}
                        } else if (SECONDS_DESC.contains(nf.getPrevInterval()) && SECONDS_DESC.contains(nf.getNextInterval()) || SECONDS_ASC.contains(nf.getPrevInterval()) && SECONDS_ASC.contains(nf.getNextInterval())) {
                            setMelodicAnalysis(melodicAnalysis, note, MelodicAnalysisNoteKinds.PASSING_TONE, true, nf, "R4: �strong ^ ratio <= 1 ^ (pi = -2 ^ ni = -2 v pi = +2 ^ ni = +2)");
                        }
                    }
                }

                if (nf.getRatio() <= 1.0 && nf.getNextInterval() != null && nf.getNextInterval().equals(UNISON)) {
                    setMelodicAnalysis(melodicAnalysis, note, MelodicAnalysisNoteKinds.ANTICIPATION, true, nf, "R5: ratio <=1 && ni=1");
                    //TODO PLACIDO Luego, en la siguiente vuelta deberemos comprobar la armon�a (no pertenece a la armon�a en la que est� ubicada y s� a la posterior)
                }


                // if no rule applied, set the default
                if (melodicAnalysis.getAnalysis(note) == null) {
                    setMelodicAnalysis(melodicAnalysis, note, MelodicAnalysisNoteKinds.HARMONIC, true, nf, "Default"); // by default, everything is harmonic
                }
                progress++;
                if (o != null) {
                    o.setCurrentProgress(Math.min(progress, total), total);//TODO Revisar por qu� en el coral 437 el max no es el total
                }
            }
        }
        if (o != null) {
            o.onEnd();
        }
        return melodicAnalysis;
    }
    //TODO - seguramente podremos quitar dependsOnHarmony, porque s�lo vale para que pasen los tests!!!! 

    protected void setMelodicAnalysis(MelodicAnalysis melodicAnalysis, AtomPitch note,
                                      MelodicAnalysisNoteKinds kind, boolean dependsOnHarmony, NoteMelodicAnalysisFeatures features, String rule) {

        melodicAnalysis.addAnalysis(note, new NoteMelodicAnalysis(note, this, features, kind, rule));
    }

}
