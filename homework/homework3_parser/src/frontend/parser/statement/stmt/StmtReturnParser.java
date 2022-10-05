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

    public StmtReturnParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtReturn parseStmtReturn() {
        this.returnTk = this.iterator.readNextToken();
        if (!this.returnTk.getType().equals(TokenType.RETURNTK)) {
            System.out.println("EXPECT RETURNTK IN STMTRETURNPARSER");
        }
        ExpParser expParser = new ExpParser(this.iterator);
        this.exp = expParser.parseExp();
        this.semicn = this.iterator.readNextToken();
        StmtReturn stmtReturn = new StmtReturn(this.returnTk, this.exp, this.semicn);
        return stmtReturn;
    }
}
