package es.ua.dlsi.im3.core.io.antlr;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;

public class ANTLRUtils {
    public void printLexer(Lexer lexer) {
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
