package frontend.parser.expression.unaryexp;

import frontend.lexer.Token;
import frontend.parser.terminal.Ident;
import frontend.parser.expression.FuncRParams;

/**
 * <Ident> '(' [<FuncRParams>] ')'
 */
public class UnaryExpFunc implements UnaryExpEle {
    private Ident ident;
    private FuncRParams funcRParams = null;
    private Token leftBracket;
    private Token rightBracket;

    public UnaryExpFunc(Ident ident,
                        FuncRParams funcRParams,
                        Token leftBracket,
                        Token rightBracker) {
        this.ident = ident;
        this.funcRParams = funcRParams;
        this.leftBracket = leftBracket;
        this.rightBracket = rightBracker;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.syntaxOutput());
        sb.append(leftBracket.syntaxOutput());
        if (funcRParams != null) {
            sb.append(this.funcRParams.syntaxOutput());
        }
        sb.append(rightBracket);
        return sb.toString();
    }
}
