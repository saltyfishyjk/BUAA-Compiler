package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.parser.expression.Cond;
import frontend.parser.expression.CondParser;

public class StmtRepeatParser {
    private TokenListIterator iterator;

    public StmtRepeatParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtRepeat parseStmtRepeat() {
        Token repeat = this.iterator.readNextToken();
        StmtParser stmtParser = new StmtParser(this.iterator);
        Stmt stmt = stmtParser.parseStmt();
        Token until = this.iterator.readNextToken();
        Token leftBracket = this.iterator.readNextToken();
        CondParser condParser = new CondParser(this.iterator);
        Cond cond = condParser.parseCond();
        Token rightBracket = this.iterator.readNextToken();
        Token semicn = this.iterator.readNextToken();
        StmtRepeat stmtRepeat = new StmtRepeat(repeat, stmt, until, leftBracket, cond, rightBracket, semicn);
        return stmtRepeat;
    }
}
