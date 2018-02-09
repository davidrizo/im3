package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.Atom;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.SimpleNote;
import es.ua.dlsi.im3.core.score.StemDirection;
import es.ua.dlsi.im3.core.score.io.XMLExporterImporterTest;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.layout.coresymbols.*;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.svg.SVGExporter;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

public class LayoutElementsTest {
    @Test
    public void stemDir() throws Exception {
        MEISongImporter importer = new MEISongImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/stemdir.mei");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.bravura,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout();

        TreeSet<LayoutCoreSymbolInStaff> layoutSymbols = layout.getSystem().getTopStaff().getLayoutSymbolsInStaff();
        ArrayList<LayoutCoreSingleFigureAtom> v = new ArrayList<>();
        Iterator<LayoutCoreSymbolInStaff> iter = layoutSymbols.iterator();
        for (int i=0; i<layoutSymbols.size(); i++) {
            LayoutCoreSymbolInStaff coreSymbolInStaff = iter.next();
            if (coreSymbolInStaff instanceof LayoutCoreSingleFigureAtom) {
                v.add((LayoutCoreSingleFigureAtom)coreSymbolInStaff);
            }
        }
        assertEquals("Number of notes", 2, v.size());
        assertFalse("First stem down", v.get(0).isStemUp());
        assertTrue("First stem up", v.get(1).isStemUp());

    }


}