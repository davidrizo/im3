package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.language.mensural.states.OMRState;

public class EndBarState extends OMRState {
    public EndBarState(int number) {
        super(number, "endbar");
    }

    @Override
    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction)  {
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
            //TODO Modifgicar mas adelante implementacion para adicion de compases
            // Generación Measure measure = new Measure(transduction.getSong());
            /*
                try {
                transduction.getSong().addMeasure(time, measure);
                measure.setEndTime(transduction.getLayer().getDuration());
                //System.out.println(transduction.getLayer().getDuration());
                //TODO: 7/12/17 Problema con los multirest. obtengo IM3Exeption: Cannot set an end time
                //TODO: 5/10/17 Comprobar que el endtime coincide con la duración esperada del compás
                SemanticTimeSignature lastTimeSignature = transduction.getStaff().getLastTimeSignature();
                if (lastTimeSignature.getDuration().equals(measure.getDuration())) {
                    System.err.println("TO-DO Bajar probabilidad porque la duración del compás: " +
                            measure.getDuration() + " es distinta a la del time signature: " +
                            lastTimeSignature + " --> " + lastTimeSignature.getDuration()
                    );
                }

            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }*/
        }
    }
}

