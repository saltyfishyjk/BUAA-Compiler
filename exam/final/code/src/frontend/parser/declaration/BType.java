package frontend.parser.declaration;

import frontend.lexer.Token;
import frontend.parser.SyntaxNode;

public class BType implements SyntaxNode {
    private final String name = "<BType>";
    private Token token; // must be INTTK

    public BType(Token token) {
        this.token = token;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(token.syntaxOutput());
        /* not output BType according to assignment requirement */
        return sb.toString();
    }
}
