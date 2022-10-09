package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.parser.expression.Exp;
import frontend.parser.expression.ExpParser;
import middle.symbol.SymbolTable;

public class StmtExpParser {
    private TokenListIterator iterator;
    /* StmtExp Attributes */
    private Exp exp = null;
    private Token semicn = null; // ';'
    private SymbolTable curSymbolTable;

    public StmtExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtExpParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public StmtExp parseStmtExp() {
        // ExpParser expParser = new ExpParser(this.iterator);
        ExpParser expParser = new ExpParser(this.iterator, this.curSymbolTable);
        this.exp = expParser.parseExp();
        this.semicn = this.iterator.readNextToken();
        StmtExp stmtExp = new StmtExp(this.exp, this.semicn);
        return stmtExp;
    }
}
