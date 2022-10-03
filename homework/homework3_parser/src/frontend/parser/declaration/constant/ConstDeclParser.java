package frontend.parser.declaration.constant;

import frontend.lexer.Token;

import java.util.ListIterator;

public class ConstDeclParser {
    private ListIterator<Token> iterator;

    public ConstDeclParser(ListIterator<Token> iterator) {
        this.iterator =  iterator;
    }

    public ConstDecl parseConstDecl() {
        /* TODO */
        return null;
    }
}
