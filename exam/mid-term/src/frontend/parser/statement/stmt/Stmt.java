package frontend.parser.statement.stmt;

import frontend.parser.statement.blockitem.BlockItemEle;

/**
 * 语句 Stmt
 * 可能包含的分支情况由StmtEle接口统一
 */
public class Stmt implements BlockItemEle {
    private final String name = "<Stmt>";
    private StmtEle stmtEle;

    public Stmt(StmtEle stmtEle) {
        this.stmtEle = stmtEle;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.stmtEle.syntaxOutput());
        sb.append(this.name + "\n");
        return sb.toString();
    }
}
