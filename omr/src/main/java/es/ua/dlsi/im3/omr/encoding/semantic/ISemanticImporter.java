package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;

import java.io.File;

public interface ISemanticImporter  {
    SemanticEncoding importString(NotationType notationType, String string) throws IM3Exception;
    SemanticEncoding importFile(NotationType notationType, File file) throws IM3Exception;
}
