package frontend.parser.statement.stmt;

import frontend.lexer.Token;

public class StmtContinue implements StmtEle {
    private Token continueTk; // 'continue'
    private Token semicn; // ';'

    public StmtContinue(Token continueTk,
                        Token semicn) {
        this.continueTk = continueTk;
        this.semicn = semicn;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.continueTk.syntaxOutput());
        sb.append(this.semicn.syntaxOutput());
        return sb.toString();
    }
}
