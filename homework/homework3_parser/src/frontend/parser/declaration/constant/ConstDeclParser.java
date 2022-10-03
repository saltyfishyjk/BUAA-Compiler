package frontend.parser.declaration.constant;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;

import java.util.ListIterator;

public class ConstDeclParser {
    private TokenListIterator iterator;

    public ConstDeclParser(TokenListIterator iterator) {
        this.iterator =  iterator;
    }

    public ConstDecl parseConstDecl() {
        /* TODO */
        return null;
    }
}
