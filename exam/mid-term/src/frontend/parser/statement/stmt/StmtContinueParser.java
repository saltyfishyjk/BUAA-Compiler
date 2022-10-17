package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;

public class StmtContinueParser {
    private TokenListIterator iterator;
    /* StmtContinue Attributes */
    private Token continueTk; // 'continue'
    private Token semicn; // ';'

    public StmtContinueParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtContinue parseStmtContinue() {
        this.continueTk = this.iterator.readNextToken();
        if (!this.continueTk.getType().equals(TokenType.CONTINUETK)) {
            System.out.println("EXPECT CONTINUETK IN STMTCONTINUEPARSER");
        }
        this.semicn = this.iterator.readNextToken();
        if (!this.semicn.getType().equals(TokenType.SEMICN)) {
            System.out.println("EXPECT SEMICN IN STMTCONTINUEPARSER");
        }
        StmtContinue stmtContinue = new StmtContinue(this.continueTk, this.semicn);
        return stmtContinue;
    }
}
