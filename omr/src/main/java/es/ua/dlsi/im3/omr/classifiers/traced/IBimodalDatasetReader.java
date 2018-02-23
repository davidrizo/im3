package es.ua.dlsi.im3.omr.classifiers.traced;

import es.ua.dlsi.im3.omr.IStringToSymbolFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IBimodalDatasetReader<SymbolType> {
	List<BimodalSymbol<SymbolType>> read(InputStream is, IStringToSymbolFactory<SymbolType> stringToSymbol) throws IOException;
	List<BimodalSymbol<SymbolType>> read(File file, IStringToSymbolFactory<SymbolType> stringToSymbol) throws IOException;
}
