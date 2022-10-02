package frontend.parser.expression.primaryexp;

import frontend.lexer.Token;
import frontend.parser.expression.Exp;
import frontend.parser.expression.primaryexp.PrimaryExpEle;

/**
 * '(' <Exp> ')'
 */
public class PrimaryExpExp implements PrimaryExpEle {
    private Token leftBracket; // must be '('
    private Exp exp;
    private Token rightBracket; // must be ')'

    public PrimaryExpExp(Token leftBracket, Exp exp, Token rightBracket) {
        this.leftBracket = leftBracket;
        this.exp = exp;
        this.rightBracket = rightBracket;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(leftBracket.syntaxOutput());
        sb.append(exp.syntaxOutput());
        sb.append(rightBracket.syntaxOutput());
        return sb.toString();
    }
}
