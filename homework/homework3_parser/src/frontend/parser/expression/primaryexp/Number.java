package frontend.parser.expression.primaryexp;

import frontend.lexer.terminal.IntConst;
import frontend.parser.expression.primaryexp.PrimaryExpEle;

/**
 * Number 非终结符
 * Number -> IntConst
 */
public class Number implements PrimaryExpEle {
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
        StringBuilder sb = new StringBuilder();
        sb.append(intConst.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }
}
