package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.Exp;
import frontend.parser.expression.ExpParser;

public class StmtReturnParser {
    private TokenListIterator iterator;
    /* StmtPrint Attributes */
    private Token returnTk; // 'return'
    private Exp exp;
    private Token semicn; // ';'
    private StmtReturn stmtReturn = null;

    public StmtReturnParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtReturn parseStmtReturn() {
        this.returnTk = this.iterator.readNextToken();
        if (!this.returnTk.getType().equals(TokenType.RETURNTK)) {
            System.out.println("EXPECT RETURNTK IN STMTRETURNPARSER");
        }
        ExpParser expParser = new ExpParser(this.iterator);
        this.semicn = this.iterator.readNextToken();
        if (!this.semicn.getType().equals(TokenType.SEMICN)) {
            this.iterator.unReadToken(1);
            this.exp = expParser.parseExp();
            this.semicn = this.iterator.readNextToken();
            stmtReturn = new StmtReturn(this.returnTk, this.exp, this.semicn);
        } else {
            stmtReturn = new StmtReturn(this.returnTk, this.semicn);
        }
        return stmtReturn;
    }
}
