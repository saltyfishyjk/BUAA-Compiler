package frontend.parser.expression.unaryexp;

import frontend.lexer.Token;
import frontend.parser.SyntaxNode;

/**
 * 非终结符UnaryOp
 * 合法的类别只有+, -, !，且!仅能出现在条件表达式中
 */
public class UnaryOp implements SyntaxNode {
    private final String name = "<UnaryOp>";
    private Token token;

    public UnaryOp(Token token) {
        this.token = token;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(token.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }
}
