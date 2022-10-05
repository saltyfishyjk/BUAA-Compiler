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
    private Token leftParent;
    private Token rightParent;

    public UnaryExpFunc(Ident ident,
                        FuncRParams funcRParams,
                        Token leftParent,
                        Token rightBracker) {
        this.ident = ident;
        this.funcRParams = funcRParams;
        this.leftParent = leftParent;
        this.rightParent = rightBracker;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.syntaxOutput());
        sb.append(leftParent.syntaxOutput());
        if (funcRParams != null) {
            sb.append(this.funcRParams.syntaxOutput());
        }
        sb.append(rightParent);
        return sb.toString();
    }
}
