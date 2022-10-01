package frontend.parser.expression;

import frontend.lexer.Token;

/**
 * 非终结符UnaryOp
 * 合法的类别只有+, -, !，且!仅能出现在条件表达式中
 */
public class UnaryOp {
    private Token token;

    public UnaryOp(Token token) {
        this.token = token;
    }
}
