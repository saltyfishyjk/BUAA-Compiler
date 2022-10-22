package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.parser.expression.Cond;

/**
 * <stmt> -> 'while' '(' <Cond> ')' <Stmt>
 */
public class StmtWhile implements StmtEle {
    private Token whileTk; // 'while'
    private Token leftParent; // '('
    private Cond cond;
    private Token rightParent; // ')'
    private Stmt stmt;

    public StmtWhile(Token whileTk,
                     Token leftParent,
                     Cond cond,
                     Token rightParent,
                     Stmt stmt) {
        this.whileTk = whileTk;
        this.leftParent = leftParent;
        this.cond = cond;
        this.rightParent = rightParent;
        this.stmt = stmt;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.whileTk.syntaxOutput());
        sb.append(this.leftParent.syntaxOutput());
        sb.append(this.cond.syntaxOutput());
        sb.append(this.rightParent.syntaxOutput());
        sb.append(this.stmt.syntaxOutput());
        return sb.toString();
    }
}
