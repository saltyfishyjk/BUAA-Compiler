package frontend.parser.terminal;

import frontend.lexer.Token;
import frontend.parser.expression.primaryexp.NumberEle;

/**
 * Hex Const
 */
public class HexadecimalConst implements NumberEle {
    private Token token;

    public HexadecimalConst(Token token) {
        this.token = token;
    }

    public String getNum() {
        return this.token.getContent();
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.token.syntaxOutput());
        return sb.toString();
    }
}
