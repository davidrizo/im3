package es.ua.dlsi.im3.omr.language.mensural.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.Measure;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.TimeSignature;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;

public class EndBarState extends OMRState {
    public EndBarState(int number) {
        super(number, "endbar");
    }

    @Override
    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction)  {
        // TODO: 5/10/17 Tipo de barra?

        Time time = Time.TIME_ZERO;
        if (transduction.getSong().getNumMeasures() > 0) {
            try {
                time = transduction.getSong().getLastMeasure().getEndTime();
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }
        }
        if (transduction.getSong().getMeasureWithOnset(time) == null) {
            Measure measure = new Measure(transduction.getSong());
            try {
                transduction.getSong().addMeasure(time, measure);
                measure.setEndTime(transduction.getLayer().getDuration());

                // TODO: 5/10/17 Comprobar que el endtime coincide con la duración esperada del compás
                TimeSignature lastTimeSignature = transduction.getStaff().getLastTimeSignature();
                if (lastTimeSignature.getDuration().equals(measure.getDuration())) {
                    System.err.println("TO-DO Bajar probabilidad porque la duración del compás: " +
                            measure.getDuration() + " es distinta a la del time signature: " +
                            lastTimeSignature + " --> " + lastTimeSignature.getDuration()
                    );
                }

            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }
        }
    }
}
