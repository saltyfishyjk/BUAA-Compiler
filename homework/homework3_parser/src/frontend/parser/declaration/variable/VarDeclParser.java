package frontend.parser.declaration.variable;

import frontend.lexer.Token;

import java.util.ListIterator;

public class VarDeclParser {
    private ListIterator<Token> iterator;

    public VarDeclParser(ListIterator iterator) {
        this.iterator = iterator;
    }

    public VarDecl parseVarDecl() {
        /* TODO */
        return null;
    }
}
