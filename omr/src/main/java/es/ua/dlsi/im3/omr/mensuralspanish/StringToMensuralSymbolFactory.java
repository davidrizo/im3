package es.ua.dlsi.im3.omr.mensuralspanish;

import es.ua.dlsi.im3.omr.IStringToSymbolFactory;

public class StringToMensuralSymbolFactory implements IStringToSymbolFactory<MensuralSymbols> {
    @Override
    public MensuralSymbols parseString(String input) {
        return MensuralSymbols.parseString(input);
    }
}
