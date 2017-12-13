package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalSymbol;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;

import java.util.ArrayList;
import java.util.Arrays;

public class KeySignatureState extends OMRState {
    ArrayList<PositionInStaff> positions;
    ArrayList<Accidentals> accidentals;
    ArrayList<String> sharpOrder;
    ArrayList<String> flatOrder;


    public KeySignatureState(int number) {
        super(number, "keySig");
    }

    @Override
    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) {
        if (!token.getSymbol().equals(GraphicalSymbol.accidental)) {
            // the automaton has an error
            throw new IM3RuntimeException("Expected an accidental and found a " + token.getSymbol());
        }

        if (accidentals == null) {
            accidentals = new ArrayList<>();
            positions = new ArrayList<>();
            sharpOrder = new ArrayList<>();
            flatOrder = new ArrayList<>();
        }

        if (token.getValue() == null) {
            throw new IM3RuntimeException("Value of accidental is null");
        }
        //System.out.println(token.getValue());
        switch (token.getValue()) {
            case "b":
            case "flat":
                accidentals.add(Accidentals.FLAT);
                break;
            case "#":
            case "sharp":
                accidentals.add(Accidentals.SHARP);
                break;
            default:
                // TODO: 4/10/17 Modern key signatures or key signature change may contain naturals
                transduction.setZeroProbability();
                //throw new IM3Exception("Cannot generate use this accidental in a key signature: " + token.getValue());
        }
        positions.add(token.getPositionInStaff());
    }

    @Override
    public void onExit(State nextState, boolean isStateChange, OMRTransduction transduction) {
        if (accidentals == null) {
            // If no accidental has been found no key signature is indicated in the score
            // Not to be confused with the presence of a CM or Am key
            throw new IM3RuntimeException("Cannot generate a key signature without keys");
        }

        if (isStateChange) {
            Key key = null;
            String clefName = transduction.getStaff().getLastClef().getNote().name();
            int clefLine = transduction.getStaff().getLastClef().getLine();
            switch (clefName) {
                case "G":
                    //System.out.println("clave de sol");
                    if (clefLine == 2){
                        //Lo dejamos por defecto
                        sharpOrder = new ArrayList<String>(Arrays.asList("L5","S3","S5","L4","S2","S4","L3"));
                        flatOrder = new ArrayList<String>(Arrays.asList("L3","S4","S2","L4","L2","S3","S1"));
                    } else if (clefLine == 1) {

                    } else
                        throw new IM3RuntimeException("G clef is not on 1st o 2nd line");
                    break;
                case "F":
                    if (clefLine == 4){
                        //System.out.println("clave de fa en 4a");
                        sharpOrder = new ArrayList<String>(Arrays.asList("L4","S2","S4","L3","S1","S3","L2"));
                        flatOrder = new ArrayList<String>(Arrays.asList("L2","S3","S1","L3","S4","S2","L4")); //Revisa
                    } else if (clefLine == 3) {

                    } else
                        throw new IM3RuntimeException("F clef is not on 3rd o 4th line");

                    break;
                case "C":
                    switch (clefLine) {
                        case 1: //TODO 30/11/17 Pueden haber alternativas
                            sharpOrder = new ArrayList<String>(Arrays.asList("S2","S4","L3","L5","S3","S5","L4"));
                            flatOrder = new ArrayList<String>(Arrays.asList("L4","S5","S3","L5","L3","S4","S2"));
                            break;
                        case 3:
                            flatOrder = new ArrayList<String>(Arrays.asList("S2","L4","L2","S3","S1","L3","L1"));
                            sharpOrder = new ArrayList<String>(Arrays.asList("S4","L3","L5","S3","L2","L4","S2"));
                            break;
                        case 4:
                            sharpOrder = new ArrayList<String>(Arrays.asList("L2","L4","S2","S4","L3","L5","S3"));
                            flatOrder = new ArrayList<String>(Arrays.asList("S3","L5","L3","S4","S2","L4","L2"));
                            break;
                        default:
                            throw new IM3RuntimeException("C clef is not on 1st, 3rd or 4th line");
                    }
                    break;

            }

            // TODO: 4/10/17 Comprobar que las alteraciones (posiciones en líneas) son las correctas, si no dar p=0
            // TODO: 21/11/17 Comprobar becuadros
            try {
                if (accidentals.get(0) == Accidentals.FLAT) {
                    key = new Key(-accidentals.size(), Mode.UNKNOWN);
                    for(int i=0; i<positions.size();i++){
                        //System.out.println(positions.get(i).toString());
                        if(!positions.get(i).toString().equals(flatOrder.get(i).toString()))
                            throw new IM3RuntimeException("Flat order is not correct"); //Lanzo excepcion o probabilidad 0?
                    }
                } else {
                    key = new Key(accidentals.size(), Mode.UNKNOWN);
                    for(int i=0; i<positions.size();i++){
                        if(!positions.get(i).toString().equals(sharpOrder.get(i).toString()))
                            throw new IM3RuntimeException("Sharp order is not correct");
                    }

                }
                //System.out.println(transduction.getStaff().getLastClef().getNote().name()); //clave de... G B
                //System.out.println(transduction.getStaff().getLastClef().getLine()); //linea, entero
                transduction.getStaff().addKeySignature(new KeySignature(transduction.getStaff().getNotationType(), key));
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }

            positions = null;
            accidentals = null;
        }
    }
}
