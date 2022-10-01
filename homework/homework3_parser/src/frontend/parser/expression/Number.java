package frontend.parser.expression;

import frontend.lexer.terminal.IntConst;

/**
 * Number 非终结符
 * Number -> IntConst
 */
public class Number {
    private IntConst intConst;

    public Number(IntConst intConst) {
        this.intConst = intConst;
    }

    public IntConst getIntConst() {
        return intConst;
    }
}
