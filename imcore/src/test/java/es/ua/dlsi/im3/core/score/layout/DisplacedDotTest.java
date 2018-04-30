package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.score.layout.coresymbols.*;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DisplacedDotTest {
    // Just test it does not crash
    //@Test // TODO: 26/4/18 URGENT - Volverlo a poner
    public void horizontalLayoutCapitan() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/layout/displaceddot.mei");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.capitan,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout(true);

        SVGExporter svgExporter = new SVGExporter();
        File svgFile = TestFileUtils.createTempFile("displaceddot.svg");
        svgExporter.exportLayout(svgFile, layout);

        assertEquals(1, layout.getCanvases().size());
        assertEquals(2, layout.getLayoutStaves().size());
        LayoutStaff topLayoutStaff = layout.getLayoutStaves().iterator().next();
        TreeSet<LayoutCoreSymbolInStaff> layoutSymbolsInStaff = topLayoutStaff.getLayoutSymbolsInStaff();
        assertEquals(11, layoutSymbolsInStaff.size());

        ArrayList<LayoutCoreSymbolInStaff> v = new ArrayList<>(layoutSymbolsInStaff);
        int i=0;
        for (LayoutCoreSymbolInStaff layoutCoreSymbolInStaff: v) {
            System.out.println("#" + i + " " + layoutCoreSymbolInStaff);
            i++;
        }

        assertTrue(v.get(0) instanceof LayoutCoreClef);
        assertTrue(v.get(1) instanceof LayoutCoreKeySignature);
        assertTrue(v.get(2) instanceof LayoutCoreTimeSignature);
        assertTrue(v.get(3) instanceof LayoutCoreSingleFigureAtom);
        assertTrue(v.get(4) instanceof LayoutCoreMarkBarline);
        assertTrue(v.get(5) instanceof LayoutCoreDisplacedDot);
        assertTrue(v.get(6) instanceof LayoutCoreSingleFigureAtom);
        assertTrue(v.get(7) instanceof LayoutCoreMarkBarline);
        assertTrue(v.get(8) instanceof LayoutCoreSingleFigureAtom);
        assertTrue(v.get(9) instanceof LayoutCoreSingleFigureAtom);
        assertTrue(v.get(10) instanceof LayoutCoreMarkBarline);

        //TODO Comprobar posiciones relativas entre barlines
    }
}