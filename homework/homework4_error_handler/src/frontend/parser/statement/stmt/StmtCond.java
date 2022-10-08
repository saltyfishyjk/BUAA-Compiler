package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.parser.expression.Cond;

public class StmtCond implements StmtEle {
    private Token ifTk; // 'if'
    private Token leftParent; // '('
    private Cond cond;
    private Token rightParent; // ')'
    private Stmt ifStmt;
    private Token elseTk; // 'else' MAY exist
    private Stmt elseStmt; // MAY exist

    public StmtCond(Token ifTk,
                    Token leftParent,
                    Cond cond,
                    Token rightParent,
                    Stmt ifStmt) {
        this.ifTk = ifTk;
        this.ifStmt = ifStmt;
        this.leftParent = leftParent;
        this.cond = cond;
        this.rightParent = rightParent;
    }

    public StmtCond(Token ifTk,
                    Token leftParent,
                    Cond cond,
                    Token rightParent,
                    Stmt ifStmt,
                    Token elseTk,
                    Stmt elseStmt) {
        this(ifTk, leftParent, cond, rightParent, ifStmt);
        this.elseTk = elseTk;
        this.elseStmt = elseStmt;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.ifTk.syntaxOutput());
        sb.append(this.leftParent.syntaxOutput());
        sb.append(this.cond.syntaxOutput());
        sb.append(this.rightParent.syntaxOutput());
        sb.append(this.ifStmt.syntaxOutput());
        if (elseTk != null) {
            sb.append(this.elseTk.syntaxOutput());
            sb.append(this.elseStmt.syntaxOutput());
        }
        return sb.toString();
    }
}
