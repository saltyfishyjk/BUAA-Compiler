package frontend.parser.terminal;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.terminal.Ident;

public class IdentParser {
    private TokenListIterator iterator;
    /* Ident Attribute */
    private Token token; // ident

    public IdentParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public Ident parseIdent() {
        token = this.iterator.readNextToken();
        if (!token.getType().equals(TokenType.IDENFR)) {
            System.out.println("EXPECT IDENFR HERE");
        }
        Ident ident = new Ident(token);
        return ident;
    }
}
