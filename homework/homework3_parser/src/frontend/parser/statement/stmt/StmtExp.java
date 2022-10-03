package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.parser.expression.Exp;

/**
 * 注意，这里Exp一定存在，将不存在的情况单独拆分为StmtNull
 * Stmt -> <Exp> ';'
 */
public class StmtExp implements StmtEle {
    private Exp exp;
    private Token semicn; // ';'

    public StmtExp(Exp exp, Token semicn) {
        this.exp = exp;
        this.semicn = semicn;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.exp.syntaxOutput());
        sb.append(this.semicn.syntaxOutput());
        return sb.toString();
    }
}
