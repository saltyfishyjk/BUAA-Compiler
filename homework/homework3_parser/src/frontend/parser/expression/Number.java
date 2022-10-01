package frontend.parser.expression;

import frontend.lexer.terminal.IntConst;
import frontend.parser.SyntaxNode;

/**
 * Number 非终结符
 * Number -> IntConst
 */
public class Number implements SyntaxNode {
    private final String name = "<Number>";
    private IntConst intConst;

    public Number(IntConst intConst) {
        this.intConst = intConst;
    }

    public IntConst getIntConst() {
        return intConst;
    }

    @Override
    public String syntaxOutput() {
        /* TODO */
        return null;
    }
}
