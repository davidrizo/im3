package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.Atom;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.SimpleNote;
import es.ua.dlsi.im3.core.score.StemDirection;
import es.ua.dlsi.im3.core.score.io.XMLExporterImporterTest;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
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
        layout.layout(true);

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

    @Test
    public void widthTest() throws Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/accidentals.xml");
        ScoreSong song = importer.importSong(file);
        HorizontalLayout layout = new HorizontalLayout(song, LayoutFonts.bravura,
                new CoordinateComponent(960), new CoordinateComponent(700));
        layout.layout(true);

        TreeSet<LayoutCoreSymbolInStaff> layoutSymbols = layout.getSystem().getTopStaff().getLayoutSymbolsInStaff();

        assertEquals("Layout symbols in staff", 12, layoutSymbols.size());
        ArrayList<LayoutCoreSymbolInStaff> v = new ArrayList<>();
        Iterator<LayoutCoreSymbolInStaff> iter = layoutSymbols.iterator();
        for (int i=0; i<layoutSymbols.size(); i++) {
            LayoutCoreSymbolInStaff coreSymbolInStaff = iter.next();
            v.add(coreSymbolInStaff);
            assertTrue("Width of element > 0, #" + i + " " + coreSymbolInStaff, coreSymbolInStaff.getWidth() > 0.0);
        }

        assertTrue("Quarter without accidental less than with accidental", v.get(6).getWidth() < v.get(7).getWidth());
        assertEquals("Quarters with accidentals width", v.get(8).getWidth(), v.get(9).getWidth(), 0.0001);

    }

}