package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.Cond;
import frontend.parser.expression.CondParser;

public class StmtWhileParser {
    private TokenListIterator iterator;
    /* StmtWhile Attributes */
    private Token whileTk; // 'while'
    private Token leftParent; // '('
    private Cond cond;
    private Token rightParent; // ')'
    private Stmt stmt;

    public StmtWhileParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtWhile parseStmtWhile() {
        this.whileTk = this.iterator.readNextToken();
        if (!this.whileTk.getType().equals(TokenType.WHILETK)) {
            System.out.println("EXPECT WHILETK IN STMTWHILEPARSER");
        }
        this.leftParent = this.iterator.readNextToken();
        if (!this.leftParent.getType().equals(TokenType.LPARENT)) {
            System.out.println("EXPECT LEFTPARENT IN STMTWHILEPARER");
        }
        CondParser condParser = new CondParser(this.iterator);
        this.cond = condParser.parseCond();
        this.rightParent = this.iterator.readNextToken();
        if (!this.rightParent.getType().equals(TokenType.RPARENT)) {
            System.out.println("EXPECT RPARENT IN STMTWHILEPARSER");
        }
        StmtParser stmtParser = new StmtParser(this.iterator);
        this.stmt = stmtParser.parseStmt();
        StmtWhile stmtWhile = new StmtWhile(this.whileTk, this.leftParent,
                this.cond, this.rightParent, this.stmt);
        return stmtWhile;
    }
}
