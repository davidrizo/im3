package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.io.kern.HumdrumMatrix;
import org.junit.Test;

import static org.junit.Assert.*;

public class MensSemanticImporterTest {

    @Test
    public void importString() throws IM3Exception {
        String mensCode =
                    "*met(C)\n" +
                    "Xa\n" +
                    "Lb\n" +
                    "Sc\n" +
                    "sd\n" +
                    "Me\n" +
                    "mf\n" +
                    "Ug\n" +
                    "uAA#\n" +
                    "XA-\n" +
                    "Xpr\n" +
                    "Lir\n" +
                    "Lpr\n" +
                    "Sr\n" +
                    "sr\n" +
                    "Mr\n" +
                    "mr\n" +
                    "Ur\n" +
                    "ur\n";

        MensSemanticImporter importer = new MensSemanticImporter();
        SemanticEncoding semanticEncoding = importer.importString(NotationType.eMensural, mensCode);
        KernSemanticExporter exporter = new KernSemanticExporter();
        String output = exporter.export(semanticEncoding);
        assertEquals(mensCode, output);


    }
}
