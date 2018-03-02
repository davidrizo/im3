package es.ua.dlsi.im3.omr.classifiers.traced;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IBimodalDatasetReader {
	List<BimodalSymbol> read(InputStream is) throws IOException;
	List<BimodalSymbol> read(File file) throws IOException;
}
