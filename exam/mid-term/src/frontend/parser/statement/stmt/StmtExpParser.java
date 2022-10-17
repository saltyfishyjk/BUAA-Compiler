package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.parser.expression.Exp;
import frontend.parser.expression.ExpParser;

public class StmtExpParser {
    private TokenListIterator iterator;
    /* StmtExp Attributes */
    private Exp exp = null;
    private Token semicn = null; // ';'

    public StmtExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtExp parseStmtExp() {
        ExpParser expParser = new ExpParser(this.iterator);
        this.exp = expParser.parseExp();
        this.semicn = this.iterator.readNextToken();
        StmtExp stmtExp = new StmtExp(this.exp, this.semicn);
        return stmtExp;
    }
}
