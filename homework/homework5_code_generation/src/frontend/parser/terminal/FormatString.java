package frontend.parser.terminal;

import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.SyntaxNode;

/**
 * 格式字符串终结符 FormatString
 */
public class FormatString implements SyntaxNode {
    private Token token; // STRCON

    public FormatString(Token token) {
        this.token = token;
    }

    public FormatString(String str, int lineNum) {
        this.token = new Token(TokenType.STRCON, lineNum, str);
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.token.syntaxOutput());
        return sb.toString();
    }

    public String getContent() {
        return this.token.getContent();
    }

    public int getLineNum() {
        return this.token.getLineNum();
    }

    public Token getToken() {
        return token;
    }
}
