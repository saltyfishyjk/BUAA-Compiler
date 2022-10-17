package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.parser.expression.primaryexp.LVal;

public class StmtGetint implements StmtEle {
    private LVal lval;
    private Token eq; // '='
    private Token getint; // 'getint'
    private Token leftParent; // '('
    private Token rightParent; // ')'
    private Token semicn; // ';'

    public StmtGetint(LVal lval,
                      Token eq,
                      Token getint,
                      Token leftParent,
                      Token rightParent,
                      Token semicn) {
        this.lval = lval;
        this.eq = eq;
        this.getint = getint;
        this.leftParent = leftParent;
        this.rightParent = rightParent;
        this.semicn = semicn;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.lval.syntaxOutput());
        sb.append(this.eq.syntaxOutput());
        sb.append(this.getint.syntaxOutput());
        sb.append(this.leftParent.syntaxOutput());
        sb.append(this.rightParent.syntaxOutput());
        sb.append(this.semicn.syntaxOutput());
        return sb.toString();
    }
}
