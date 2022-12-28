package frontend.parser.terminal;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;

public class IntConstParser {
    private TokenListIterator iterator;
    /* IntConst */
    private Token token = null;

    public IntConstParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public IntConst parseIntConst() {
        this.token = this.iterator.readNextToken();
        if (!this.token.getType().equals(TokenType.INTCON)) {
            System.out.println("EXPECT INTCON HERE");
        }
        IntConst intConst = new IntConst(this.token);
        return intConst;
    }
}
