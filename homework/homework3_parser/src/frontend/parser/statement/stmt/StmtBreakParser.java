package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;

public class StmtBreakParser {
    private TokenListIterator iterator;
    /* StmtBreak Attributes */
    private Token breakTk; // 'break'
    private Token semicn; // ';'

    public StmtBreakParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtBreak parseStmtBreak() {
        this.breakTk = this.iterator.readNextToken();
        if (!this.breakTk.getType().equals(TokenType.BREAKTK)) {
            System.out.println("EXPECT BREAKTK IN STMTBREAKPARSER");
        }
        this.semicn = this.iterator.readNextToken();
        if (!this.semicn.getType().equals(TokenType.SEMICN)) {
            System.out.println("EXPECT SEMICN IN STMTBREAKPARSER");
        }
        StmtBreak stmtBreak = new StmtBreak(this.breakTk, this.semicn);
        return stmtBreak;
    }
}
