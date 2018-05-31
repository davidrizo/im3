package es.ua.dlsi.im3.core.score.io.kern;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.mensural.meters.Perfection;
import es.ua.dlsi.im3.core.score.mensural.meters.TempusImperfectumCumProlationeImperfecta;
import es.ua.dlsi.im3.core.utils.FileUtils;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;

import static org.junit.Assert.*;

public class MensImporterTest {

    /**
     * The file contains the expected lexical rule name(s) and the current lexical token output separated by tabs
     * It asserts different expected rules are not recognized as the same type
     * @throws IOException
     */
    /*@Test
    public void printLexicalRuleNumbers() throws IOException {
        File file = TestFileUtils.getFile("/testdata/core/score/io/kern/lexicalMens.txt");

        HashMap<String, Integer> lexerTypes = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() > 0) {
                    String[] tokens = line.split("\t");
                    CharStream input = CharStreams.fromString(tokens[1]);
                    MensImporter.MensLexer mensLexer = new MensImporter.MensLexer(input, false);
                    int lexerType = mensLexer.nextToken().getType();
                    System.out.println("Expected rule: " + tokens[0] + ", input: " + tokens[1] + ", lexer type=" + lexerType);

                    Integer previousLexerType = lexerTypes.get(tokens[0]);
                    if (previousLexerType != null && previousLexerType != lexerType) {
                        fail("Lexer rule " + tokens[0] + ", found previouly with number " + previousLexerType + ", and now it is " + lexerType + " with input " + tokens[1]);
                    }
                    lexerTypes.put(tokens[0], lexerType);
                }
            }
        }

        CharStream input = CharStreams.fromStream(this.getClass().getResourceAsStream("/testdata/core/score/io/kern/lexicalMens.txt"));
        MensImporter.MensLexer mensLexer = new MensImporter.MensLexer(input, true);
    }*/

    @Test
    public void importTest() throws IM3Exception {
        //TODO HE DESACTIVADO LAS LYRICS PARA PODER PARSEAR
        System.err.println("TODO HE DESACTIVADO LAS LYRICS PARA PODER PARSEAR");
        MensImporter importer = new MensImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/kern/mensGrammarTest.krn");
        HumdrumMatrix humdrumMatrix = importer.importMens(file);
        assertEquals("Size", 17, humdrumMatrix.getMatrix().size());
        assertEquals("**mens", humdrumMatrix.get(0, 0).getHumdrumEncoding());
        assertEquals("*clefG2", humdrumMatrix.get(1, 0).getHumdrumEncoding());
        assertEquals("Clef", new ClefG2(), humdrumMatrix.get(1, 0).getParsedObject());
        assertEquals("*k[b-]", humdrumMatrix.get(2, 0).getHumdrumEncoding());
        assertEquals("Key", new Key(PitchClasses.F, Mode.UNKNOWN), humdrumMatrix.get(2, 0).getParsedObject());
        assertEquals("*met(C)", humdrumMatrix.get(3, 0).getHumdrumEncoding());
        assertEquals("Mensuration sign", new TempusImperfectumCumProlationeImperfecta(), humdrumMatrix.get(3, 0).getParsedObject());
        assertEquals("*>A", humdrumMatrix.get(4, 0).getHumdrumEncoding());
        assertEquals("Section", new SectionMark("A"), humdrumMatrix.get(4, 0).getParsedObject());
        assertEquals("*ISoprano", humdrumMatrix.get(5, 0).getHumdrumEncoding());
        assertEquals("Instrument", new KernInstrument("Soprano"), humdrumMatrix.get(5, 0).getParsedObject());
        assertEquals("*MM73", humdrumMatrix.get(6, 0).getHumdrumEncoding());
        assertEquals("Metronome", new MetronomeMark(73), humdrumMatrix.get(6, 0).getParsedObject());
        assertEquals("*", humdrumMatrix.get(7, 0).getHumdrumEncoding());
        assertEquals("Null interpretation", KernNullInterpretation.class, humdrumMatrix.get(7, 0).getParsedObject().getClass());
        assertEquals("=", humdrumMatrix.get(8, 0).getHumdrumEncoding());
        assertEquals("Bar line", new MarkBarline(), humdrumMatrix.get(8, 0).getParsedObject());

        assertEquals(".", humdrumMatrix.get(9, 0).getHumdrumEncoding());
        assertEquals("Placeholder", KernPlaceHolder.class, humdrumMatrix.get(9, 0).getParsedObject().getClass());

        assertEquals("!Field comment", humdrumMatrix.get(10, 0).getHumdrumEncoding());
        assertEquals("Field comment", new KernFieldComment("Field comment"), humdrumMatrix.get(10, 0).getParsedObject());

        assertEquals("!LO:R:v=L2", humdrumMatrix.get(11, 0).getHumdrumEncoding());
        assertEquals("Rest position", new KernRestPosition(PositionsInStaff.LINE_2), humdrumMatrix.get(11, 0).getParsedObject());

        assertEquals("Sr", humdrumMatrix.get(12, 0).getHumdrumEncoding());
        assertEquals("Brevis rest", new SimpleRest(Figures.BREVE, 0), humdrumMatrix.get(12, 0).getParsedObject());

        assertEquals("s~pA#x", humdrumMatrix.get(13, 0).getHumdrumEncoding());
        SimpleNote simpleNote = new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(PitchClasses.A_SHARP, 3));
        simpleNote.getAtomFigure().setColored(true);
        simpleNote.getAtomFigure().setExplicitMensuralPerfection(Perfection.perfectum);
        simpleNote.setWrittenExplicitAccidental(Accidentals.SHARP);

        assertEquals("Semibrevis perect colored A", simpleNote, humdrumMatrix.get(13, 0).getParsedObject());

        assertEquals("<ScR", humdrumMatrix.get(14, 0).getHumdrumEncoding());
        KernLigatureComponent ligatureComponent1 = new KernLigatureComponent(LigatureStartEnd.start, LigatureType.recta, new SimpleNote(Figures.BREVE, 0, new ScientificPitch(PitchClasses.C, 3)));
        assertEquals("Brevis ligature start", ligatureComponent1, humdrumMatrix.get(14, 0).getParsedObject());

        assertEquals("LeQ", humdrumMatrix.get(15, 0).getHumdrumEncoding());
        KernLigatureComponent ligatureComponent2 = new KernLigatureComponent(LigatureStartEnd.inside, LigatureType.obliqua, new SimpleNote(Figures.LONGA, 0, new ScientificPitch(PitchClasses.E, 3)));
        assertEquals("Longa ligature inside", ligatureComponent2, humdrumMatrix.get(15, 0).getParsedObject());

        assertEquals("Sd>", humdrumMatrix.get(16, 0).getHumdrumEncoding());
        KernLigatureComponent ligatureComponent3 = new KernLigatureComponent(LigatureStartEnd.end, LigatureType.computed, new SimpleNote(Figures.BREVE, 0, new ScientificPitch(PitchClasses.D, 3)));
        assertEquals("Brevis ligature end", ligatureComponent3, humdrumMatrix.get(16, 0).getParsedObject());

        //TODO Comprobar Coloration, perfection. Explicit accidental
        //TODO Slurs, ties
        //TODO SPINES y TEXT
        //TODO Editorial accidental
        //TODO Beams
    }
}