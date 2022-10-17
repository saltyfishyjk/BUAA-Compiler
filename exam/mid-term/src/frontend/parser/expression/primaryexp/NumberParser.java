package frontend.parser.expression.primaryexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.terminal.HexadecimalConstParser;
import frontend.parser.terminal.IntConst;
import frontend.parser.terminal.IntConstParser;

public class NumberParser {
    private TokenListIterator iterator;

    public NumberParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public Number parseNumber() {
        // IntConstParser intConstParser = new IntConstParser(this.iterator);
        // IntConst intConst = intConstParser.parseIntConst();
        // Number number = new Number(intConst);
        Token token = this.iterator.readNextToken();
        Number number;
        if (token.getType().equals(TokenType.HEXCON)) {
            this.iterator.unReadToken(1);
            HexadecimalConstParser hexadecimalConstParser = new HexadecimalConstParser(this.iterator);
            // NumberEle numberEle = (NumberEle) hexadecimalConstParser.parseHexadecimalConst();
            number = new Number(hexadecimalConstParser.parseHexadecimalConst());
        } else {
            this.iterator.unReadToken(1);
            IntConstParser intConstParser = new IntConstParser(this.iterator);
            number = new Number(intConstParser.parseIntConst());
        }
        return number;
    }
}
