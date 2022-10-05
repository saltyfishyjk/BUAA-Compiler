package frontend.parser.terminal;

import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.SyntaxNode;

/**
 * 标识符 Identifier
 */
public class Ident implements SyntaxNode {
    private Token token;

    public Ident(Token token) {
        this.token = token;
    }

    public Ident(String name, int lineNum) {
        this.token = new Token(TokenType.IDENFR, lineNum, name);
    }

    public String getName() {
        return this.token.getContent();
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

    @Override
    public String toString() {
        return this.token.getContent();
    }
}
