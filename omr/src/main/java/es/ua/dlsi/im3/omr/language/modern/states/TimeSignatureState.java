package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Digit;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.MeterSign;
import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;
import es.ua.dlsi.im3.omr.language.OMRTransduction;

public class TimeSignatureState extends OMRState {
    // TODO: 4/10/17 C3, C, C/...

    public TimeSignatureState(int number) {
        super(number, "timeSig");
    }

    @Override
    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction) {
        if (!(token.getSymbol() instanceof Digit) && !(token.getSymbol() instanceof MeterSign)) {
            // the automaton has an error
            throw new IM3RuntimeException("Expected a digit or metersign and found a " + token.getSymbol());
        }
        if (token.getSymbol() instanceof Digit) {
            Digit symbol = (Digit) token.getSymbol();
            if (token.getPositionInStaff() == PositionsInStaff.LINE_2) {
                int denominator = symbol.getDigit();

                if (!(previousState instanceof DigitTimeSignatureState)) {
                    throw new IM3RuntimeException("Expected a previous state DigitTimeSignatureState and found " + previousState);
                }

                DigitTimeSignatureState prev = (DigitTimeSignatureState) previousState;
                int numerator = prev.getNumerator();
                TimeSignature timeSignature = new FractionalTimeSignature(numerator, denominator);
                try {
                    transduction.getStaff().addElementWithoutLayer(timeSignature);
                } catch (IM3Exception e) {
                    throw new IM3RuntimeException(e);
                }
            } else {
                throw new IM3RuntimeException("Invalid line for digit, it should be L4 and it is " + token.getPositionInStaff());
            }
        } else if (token.getSymbol() instanceof MeterSign) {
            MeterSign symbol = (MeterSign) token.getSymbol();
            TimeSignature timeSignature;
            if (symbol.getMeterSigns() == MeterSigns.C) {
                timeSignature = new TimeSignatureCommonTime();
            } else if (symbol.getMeterSigns() == MeterSigns.Ccut) {
                timeSignature = new TimeSignatureCutTime();
            } else {
                throw new IM3RuntimeException("Unsupported meter: " + symbol.getMeterSigns());
            }
        } else {
            throw new IM3RuntimeException("Expected a digit or metersign and found a " + token.getSymbol());
        }


        /*if (token.getPositionInStaff().getLine() == 2) {
            denominator = Integer.parseInt(token.getValue()); // TODO: 4/10/17 Comprobar que es un valor válido
            numerator = DigitTimeSignatureState.getNumerator();
            SemanticTimeSignature timeSignature = new SemanticFractionalTimeSignature(numerator, denominator);
            try {
                transduction.getStaff().addTimeSignature(timeSignature);
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }
        } else if ((token.getPositionInStaff().getLine() == 3) && token.getSymbol().equals(GraphicalSymbol.metersign)){ //Comprobacion de compasillo
            numerator = 4;
            denominator = 4;
            SemanticTimeSignature timeSignature = new SemanticFractionalTimeSignature(numerator, denominator);
            try {
                transduction.getStaff().addTimeSignature(timeSignature);
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }
        } else {
            throw new IM3RuntimeException("Error reading Time Signature");
        }*/
    }

    @Override
    public void onExit(State nextState, boolean isStateChange, OMRTransduction transduction) {
        if (isStateChange) {
            /*if (numerator == null || denominator == null) {
                throw new IM3RuntimeException("Invalid grammar for Time Signature, numerator or denominator are null"); //TODO C, C/..
            }

            SemanticTimeSignature timeSignature = new SemanticFractionalTimeSignature(numerator, denominator);
            try {
                transduction.getStaff().addTimeSignature(timeSignature);
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }

            denominator = null;
            numerator = null; */
            //SemanticTimeSignature timeSignature = new SemanticFractionalTimeSignature(4, 4); //ELIMINAR, NO ES CORRECTO
            //try {
             //   transduction.getStaff().addTimeSignature(timeSignature);
            //} catch (IM3Exception e) {
            //    throw new IM3RuntimeException(e);
            //}

        }
    }
}
