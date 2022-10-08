package frontend.parser.terminal;

import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.SyntaxNode;

/**
 * 数值常量 Integer-Const
 */
public class IntConst implements SyntaxNode {
    private Token token;

    public IntConst(Token token) {
        this.token = token;
    }

    public IntConst(String numStr, int lineNum) {
        this.token = new Token(TokenType.INTCON, lineNum, numStr);
    }

    public int getNum() {
        return Integer.valueOf(this.token.getContent());
    }

    public int getLineNum() {
        return this.token.getLineNum();
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(token.syntaxOutput());
        return sb.toString();
    }
}