package es.ua.dlsi.im3.omr;

public interface IStringToSymbolFactory<SymbolType> {
    SymbolType parseString(String input);
}
