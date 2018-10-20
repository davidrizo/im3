package grammardevelopment;


import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import prueba.mensLexer;
import prueba.mensParser;
import prueba.mensParserBaseListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class just import isolated semantic symbols (clefs, notes...) to be handled later
 *
 * @author drizo
 */
public class Importer {
    private void doImport(CharStream input, String inputDescription) throws Exception {
        mensLexer lexer = new mensLexer(input);

        System.out.println("------- LEXER ------");
        printLexer(lexer);
        System.out.println("------- END LEXER ------");

        System.out.println("------- PARSER ------");

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        mensParser parser = new mensParser(tokens);

        ParseTree tree = parser.start();
        ParseTreeWalker walker = new ParseTreeWalker();
        mensParserBaseListener loader = new mensParserBaseListener();
        walker.walk(loader, tree);
        System.out.println("------- END PARSER ------");

        System.out.println("------- PARSE TREE ------");
        ArrayList<String> ruleNames = new ArrayList<>();
        for (String rn: parser.getRuleNames()) {
            ruleNames.add(rn);
        }
        System.out.println(TreeUtils.toPrettyTree(tree, ruleNames));
    }

    public void doImport(File file) throws Exception {
        try {
            CharStream input = CharStreams.fromFileName(file.getAbsolutePath());
            doImport(input, file.getAbsolutePath());
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

    public void doImport(String string) throws Exception {
        CharStream input = CharStreams.fromString(string);
        doImport(input, string);
    }

    public static final void main(String [] args) throws Exception {
        Importer importer = new Importer();
        File testFile = new File("/tmp/prueba.txt");
        importer.doImport(testFile);
    }

    private void printLexer(Lexer lexer) {
        TokenSource tokenSource = lexer;
        while (true) {
            Token token = tokenSource.nextToken();
            if (token.getType() == Lexer.EOF) {
                break;
            }

            System.out.println("Token: <" + token.getText() + ">");
        }
    }
}
