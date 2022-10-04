package frontend.parser.expression;

import frontend.lexer.TokenListIterator;
import frontend.parser.expression.multiexp.MulExp;

public class MulExpParser {
    private TokenListIterator iterator;

    public MulExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public MulExp parseMulExp() {
        /* TODO */
        return null;
    }
}