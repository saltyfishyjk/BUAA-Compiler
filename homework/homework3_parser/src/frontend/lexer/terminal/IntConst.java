package frontend.lexer.terminal;

import frontend.lexer.Token;
import frontend.lexer.TokenType;

/**
 * 数值常量 Integer-Const
 */
public class IntConst {
    private Token token;

    public IntConst(String numStr, int lineNum) {
        this.token = new Token(TokenType.INTCON, lineNum, numStr);
    }

    public int getNum() {
        return Integer.valueOf(this.token.getContent());
    }

    public int getLineNum() {
        return this.token.getLineNum();
    }
}