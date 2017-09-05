package es.ua.dlsi.im3.core.score.io.harmony;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.io.antlr.ErrorListener;
import es.ua.dlsi.im3.core.io.antlr.GrammarParseRuntimeException;
import es.ua.dlsi.im3.core.score.*;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author drizo
 */
public class HarmonyImporter  {

    public HarmonyImporter() {
    }

    public static class Loader extends harmonyBaseListener {

        Key [] key;
        TonalFunction [] tonalFunction;
        int index;
        Loader() {
        }

        @Override
        public void enterTonalFunction(harmonyParser.TonalFunctionContext ctx) {
            Logger.getLogger(HarmonyImporter.class.getName()).log(Level.FINEST, "Enter Tonal Function {0}", ctx.getText());
            if (ctx.alternateTonalFunction() != null) {
                tonalFunction = new TonalFunction[2];
            } else {
                tonalFunction = new TonalFunction[1];
            }
            index = 0;
        }

        @Override
        public void enterAlternateTonalFunction(harmonyParser.AlternateTonalFunctionContext ctx) {
            Logger.getLogger(HarmonyImporter.class.getName()).log(Level.FINEST, "Enter Alternate Tonal Function {0}", ctx.getText());
            index = 1;
        }

        @Override
        public void exitTonalFunctionSpecification(harmonyParser.TonalFunctionSpecificationContext ctx) {
            Logger.getLogger(HarmonyImporter.class.getName()).log(Level.FINEST, "Exit Tonal Function Specification {0}", ctx.getText());

            TerminalNode typeNode = (TerminalNode) ctx.getChild(0);

            switch (typeNode.getSymbol().getText()) {
                case "T":
                    tonalFunction[index] = TonalFunction.TONIC;
                    break;
                case "D":
                    tonalFunction[index] = TonalFunction.DOMINANT;
                    break;
                case "S":
                case "SD":
                    tonalFunction[index] = TonalFunction.SUBDOMINANT;
                    break;
                default:
                    throw new GrammarParseRuntimeException("Unknown tonal function: " + typeNode.getSymbol().getText());
            }
        }


        @Override
        public void enterKey(harmonyParser.KeyContext ctx) {
            Logger.getLogger(HarmonyImporter.class.getName()).log(Level.FINEST, "Enter Key {0}", ctx.getText());
            if (ctx.alternateKey() != null) {
                key = new Key[2];
            } else {
                key = new Key[1];
            }
            index = 0;
        }

        @Override
        public void enterAlternateKey(harmonyParser.AlternateKeyContext ctx) {
            Logger.getLogger(HarmonyImporter.class.getName()).log(Level.FINEST, "Enter Alternate Key {0}", ctx.getText());
            index = 1;
        }

        @Override
        public void exitKeySpecification(harmonyParser.KeySpecificationContext ctx) {
            Logger.getLogger(HarmonyImporter.class.getName()).log(Level.FINEST, "Exit Key Specification {0}", ctx.getText());

            NoteNames noteName;
            Accidentals acc = null;
            Mode mode;

            try {
                noteName = NoteNames.noteFromName(ctx.getChild(0).getText());
            } catch (IM3Exception e) {
                throw new GrammarParseRuntimeException(e);
            }

            if (ctx.ALTERATION() != null) {
                try {
                    acc = Accidentals.accidentalFromName(ctx.ALTERATION().getText());
                } catch (IM3Exception e) {
                    throw new GrammarParseRuntimeException(e);
                }
            }

            if (ctx.MODE() == null) {
                mode = Mode.MAJOR;
            } else {
                try {
                    mode = Mode.stringToMode(ctx.MODE().getText());
                } catch (IM3Exception e) {
                    throw new GrammarParseRuntimeException(e);
                }
            }

            PitchClass pc = new PitchClass(noteName, acc);
            try {
                key[index] = new Key(pc, mode);
            } catch (IM3Exception e) {
                throw new GrammarParseRuntimeException(e);
            }
        }

    }


    /**
     * @param string
     * @return key[0] is the main key, key[1], if present, would be the alternate key
     * @throws ImportException
     */
    public Key[] readKey(String string) throws ImportException {
        try {
            CharStream input = CharStreams.fromString(string);
            harmonyLexer lex = new harmonyLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lex);
            harmonyParser parser = new harmonyParser(tokens);
            ErrorListener errorListener = new ErrorListener();
            parser.addErrorListener(errorListener);
            ParseTree tree = parser.key();
            ParseTreeWalker walker = new ParseTreeWalker();
            Loader loader = new Loader();
            walker.walk(loader, tree);
            if (errorListener.getNumberErrorsFound() != 0) {

                throw new ImportException(errorListener.getNumberErrorsFound() + " errors found in "
                        + string);
            }
            return loader.key;
        } catch (Exception e) {
            System.err.println("Input: " + string);
            e.printStackTrace();
            throw new ImportException(e.toString());
        }
    }
    /**
     * @param string
     * @return tonalfunction[0] is the main tonal function, tonalFinction[1], if present, would be the alternate tonal function
     * @throws ImportException
     */
    public TonalFunction[] readTonalFunction(String string) throws ImportException {
        try {
            CharStream input = CharStreams.fromString(string);
            harmonyLexer lex = new harmonyLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lex);
            harmonyParser parser = new harmonyParser(tokens);
            ErrorListener errorListener = new ErrorListener();
            parser.addErrorListener(errorListener);
            ParseTree tree = parser.tonalFunction();
            ParseTreeWalker walker = new ParseTreeWalker();
            Loader loader = new Loader();
            walker.walk(loader, tree);
            if (errorListener.getNumberErrorsFound() != 0) {

                throw new ImportException(errorListener.getNumberErrorsFound() + " errors found in "
                        + string);
            }
            return loader.tonalFunction;
        } catch (Exception e) {
            System.err.println("Input: " + string);
            e.printStackTrace();
            throw new ImportException(e.toString());
        }
    }

}
