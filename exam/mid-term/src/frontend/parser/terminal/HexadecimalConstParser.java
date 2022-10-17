package frontend.parser.terminal;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;

public class HexadecimalConstParser {
    private TokenListIterator iterator;
    private Token token = null;

    public HexadecimalConstParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public HexadecimalConst parseHexadecimalConst() {
        this.token = this.iterator.readNextToken();
        if (!this.token.getType().equals(TokenType.HEXCON)) {
            System.out.println("EXPECT HEXCON HERE");
        }
        HexadecimalConst hexadecimalConst = new HexadecimalConst(this.token);
        return hexadecimalConst;
    }

}
