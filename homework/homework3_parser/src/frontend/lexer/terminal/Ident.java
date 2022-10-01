package frontend.lexer.terminal;

import frontend.lexer.Token;
import frontend.lexer.TokenType;

/**
 * 标识符
 */
public class Ident {
    private Token token;

    public Ident(String name, int lineNum) {
        this.token = new Token(TokenType.IDENFR, lineNum, name);
    }

    public String getName() {
        return this.token.getContent();
    }

    public int getLineNum() {
        return this.token.getLineNum();
    }
}
