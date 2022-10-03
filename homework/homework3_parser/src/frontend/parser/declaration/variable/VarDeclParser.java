package frontend.parser.declaration.variable;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;

import java.util.ListIterator;

public class VarDeclParser {
    private TokenListIterator iterator;

    public VarDeclParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public VarDecl parseVarDecl() {
        /* TODO */
        return null;
    }
}
