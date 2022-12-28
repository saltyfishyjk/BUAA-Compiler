package frontend.parser.declaration;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;

public class BTypeParser {
    private TokenListIterator iterator;

    public BTypeParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public BType parseBtype() {
        Token first = this.iterator.readNextToken();
        if (!first.getType().equals(TokenType.INTTK)) {
            System.out.println("ERROR : EXPECT INTTK");
        }
        BType btype = new BType(first);
        return btype;
    }
}
