package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.parser.expression.Exp;

/**
 * <stmt> -> 'return' [<Exp>] ';'
 */
public class StmtReturn implements StmtEle {
    private Token returnTk; // 'return'
    private Exp exp; // MAY exist
    private Token semicn; // ';'

    public StmtReturn(Token returnTk,
                      Token semicn) {
        this.returnTk = returnTk;
        this.semicn = semicn;
    }

    public StmtReturn(Token returnTk,
                      Exp exp,
                      Token semicn) {
        this(returnTk, semicn);
        this.exp = exp;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.returnTk.syntaxOutput());
        if (exp != null) {
            sb.append(this.exp.syntaxOutput());
        }
        sb.append(this.semicn.syntaxOutput());
        return sb.toString();
    }

    public int checkReturn() {
        if (this.exp != null) {
            return 2;
        } else {
            return 1;
        }
    }

    public int getReturnLineNum() {
        return this.returnTk.getLineNum();
    }
}
