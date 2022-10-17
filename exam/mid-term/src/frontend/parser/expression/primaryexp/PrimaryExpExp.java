package frontend.parser.expression.primaryexp;

import frontend.lexer.Token;
import frontend.parser.expression.Exp;

/**
 * '(' <Exp> ')'
 */
public class PrimaryExpExp implements PrimaryExpEle {
    private Token leftParent; // must be '('
    private Exp exp;
    private Token rightParent; // must be ')'

    public PrimaryExpExp(Token leftBracket, Exp exp, Token rightParent) {
        this.leftParent = leftBracket;
        this.exp = exp;
        this.rightParent = rightParent;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(leftParent.syntaxOutput());
        sb.append(exp.syntaxOutput());
        sb.append(rightParent.syntaxOutput());
        return sb.toString();
    }
}
