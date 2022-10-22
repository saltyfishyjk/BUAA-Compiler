package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.parser.expression.Exp;
import frontend.parser.expression.primaryexp.LVal;

/**
 * Stmt -> <LVal> '=' <Exp> ';'
 */
public class StmtAssign implements StmtEle {
    private LVal lval;
    private Token eq; // '='
    private Exp exp;
    private Token semicn; // ';'

    public StmtAssign(LVal lval,
                      Token eq,
                      Exp exp,
                      Token semicn) {
        this.lval = lval;
        this.eq = eq;
        this.exp = exp;
        this.semicn = semicn;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.lval.syntaxOutput());
        sb.append(this.eq.syntaxOutput());
        sb.append(this.exp.syntaxOutput());
        sb.append(this.semicn.syntaxOutput());
        return sb.toString();
    }
}
