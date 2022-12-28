package frontend.parser.expression.primaryexp;

import frontend.lexer.TokenListIterator;
import frontend.parser.terminal.IntConst;
import frontend.parser.terminal.IntConstParser;

public class NumberParser {
    private TokenListIterator iterator;

    public NumberParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public Number parseNumber() {
        IntConstParser intConstParser = new IntConstParser(this.iterator);
        IntConst intConst = intConstParser.parseIntConst();
        Number number = new Number(intConst);
        return number;
    }
}
