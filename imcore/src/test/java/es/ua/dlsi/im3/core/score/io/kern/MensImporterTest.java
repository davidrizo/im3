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
        MensImporter importer = new MensImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/kern/mens_grammar_test.krn");
        HumdrumMatrix humdrumMatrix = importer.importMens(file);
        assertEquals("Size", 24, humdrumMatrix.getMatrix().size());
        int row = 0;
        assertEquals("**mens", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        row++;

        assertEquals("*part5", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Part number", 5, ((ScorePart)humdrumMatrix.get(row, 0).getParsedObject()).getNumber());
        row++;

        assertEquals("*staff93", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Staff number", 93, ((Staff)humdrumMatrix.get(row, 0).getParsedObject()).getNumberIdentifier());
        row++;

        assertEquals("*clefG2", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Clef", new ClefG2(), humdrumMatrix.get(row, 0).getParsedObject());
        assertEquals("*clefG2 text", new KernText("G clef"), humdrumMatrix.get(row, 1).getParsedObject());
        row++;

        assertEquals("*k[b-]", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Key", new Key(PitchClasses.F, Mode.UNKNOWN), humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("*M4/2", humdrumMatrix.get(row, 0).getHumdrumEncoding()); //TODO ¿Qué hacemos con el 4/4, ¿sugerencia para luego generar moderno?
        row++;

        assertEquals("*met(C)", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Mensuration sign", new TempusImperfectumCumProlationeImperfecta(), humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("*>A", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Section", new SectionMark("A"), humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("*ISoprano", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Instrument", new KernInstrument("Soprano"), humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("*MM73", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Metronome", new MetronomeMark(73), humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("*", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Null interpretation", KernNullInterpretation.class, humdrumMatrix.get(row, 0).getParsedObject().getClass());
        row++;

        assertEquals("=", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Bar line", new MarkBarline(), humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals(".", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Placeholder", KernPlaceHolder.class, humdrumMatrix.get(row, 0).getParsedObject().getClass());
        row++;

        assertEquals("!Field comment", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Field comment", new KernFieldComment("Field comment"), humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("!LO:R:v=L2", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Rest position", new KernRestPosition(PositionsInStaff.LINE_2), humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("Sr", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Brevis rest", new SimpleRest(Figures.BREVE, 0), humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("s~pA#x", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        SimpleNote simpleNote = new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(PitchClasses.A_SHARP, 3));
        simpleNote.getAtomFigure().setColored(true);
        simpleNote.getAtomFigure().setExplicitMensuralPerfection(Perfection.perfectum);
        simpleNote.setWrittenExplicitAccidental(Accidentals.SHARP);
        assertEquals("Semibrevis perect colored A", simpleNote, humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("<ScR", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        KernLigatureComponent ligatureComponent1 = new KernLigatureComponent(LigatureStartEnd.start, LigatureType.recta, new SimpleNote(Figures.BREVE, 0, new ScientificPitch(PitchClasses.C, 3)));
        assertEquals("Brevis ligature start", ligatureComponent1, humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("LeQ", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        KernLigatureComponent ligatureComponent2 = new KernLigatureComponent(LigatureStartEnd.inside, LigatureType.obliqua, new SimpleNote(Figures.LONGA, 0, new ScientificPitch(PitchClasses.E, 3)));
        assertEquals("Longa ligature inside", ligatureComponent2, humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("Sd>", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        KernLigatureComponent ligatureComponent3 = new KernLigatureComponent(LigatureStartEnd.end, LigatureType.computed, new SimpleNote(Figures.BREVE, 0, new ScientificPitch(PitchClasses.D, 3)));
        assertEquals("Brevis ligature end", ligatureComponent3, humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("Mi:E", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        SimpleNote simpleNote2 = new SimpleNote(Figures.MINIM, 0, new ScientificPitch(PitchClasses.E, 3));
        simpleNote2.getAtomFigure().setFollowedByMensuralDivisionDot(true);
        simpleNote2.getAtomFigure().setExplicitMensuralPerfection(Perfection.imperfectum);
        assertEquals("Semibrevis impertect A with division dot", simpleNote2, humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("!prueba", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("prueba field comment", new KernFieldComment("prueba"), humdrumMatrix.get(row, 0).getParsedObject());
        assertEquals("Second spine in prueba field comment", "!Prueba field comment", humdrumMatrix.get(row, 1).getHumdrumEncoding());
        row++;

        assertEquals("!", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("Empty field comment", new KernFieldComment(null), humdrumMatrix.get(row, 0).getParsedObject());
        row++;

        assertEquals("!sb", humdrumMatrix.get(row, 0).getHumdrumEncoding());
        assertEquals("sb", new KernFieldComment("sb"), humdrumMatrix.get(row, 0).getParsedObject());
        assertEquals("!", "!", humdrumMatrix.get(row, 1).getHumdrumEncoding());
        row++;

        //TODO Alteratio
        //TODO Slurs, ties
        //TODO SPINES y TEXT
        //TODO Editorial accidental
        //TODO Beams
    }
}