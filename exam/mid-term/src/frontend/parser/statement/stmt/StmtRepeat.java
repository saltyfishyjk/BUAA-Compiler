package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.parser.expression.Cond;

public class StmtRepeat implements StmtEle {
    private Token repeat; // "repeat"
    private Stmt stmt; // stmt
    private Token until; // until
    private Token leftBracket; // '('
    private Cond cond; // cond
    private Token rightBracker; // ')'
    private Token semicn; // ';'

    public StmtRepeat(Token repeat,
                      Stmt stmt,
                      Token until,
                      Token leftBracket,
                      Cond cond,
                      Token rightBracker,
                      Token semicn) {
        this.repeat = repeat;
        this.stmt = stmt;
        this.until = until;
        this.leftBracket = leftBracket;
        this.cond = cond;
        this.rightBracker = rightBracker;
        this.semicn = semicn;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.repeat.syntaxOutput());
        sb.append(this.stmt.syntaxOutput());
        sb.append(this.until.syntaxOutput());
        sb.append(this.leftBracket.syntaxOutput());
        sb.append(this.cond.syntaxOutput());
        sb.append(this.rightBracker.syntaxOutput());
        sb.append(this.semicn.syntaxOutput());
        return sb.toString();
    }
}
