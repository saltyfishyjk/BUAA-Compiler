package frontend.parser.declaration;

import frontend.lexer.Token;

import java.util.ListIterator;

public class DeclParser {
    private ListIterator<Token> iterator;

    public DeclParser(ListIterator<Token> iterator) {
        this.iterator = iterator;
    }

    public Decl parseDecl() {
        /* TODO */
        return null;
    }
}
