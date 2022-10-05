package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.Cond;
import frontend.parser.expression.CondParser;

public class StmtCondParser {
    private TokenListIterator iterator;
    /* StmtCond Attributes */
    private Token ifTK = null; // 'if'
    private Token leftParent = null; // '('
    private Cond cond = null;
    private Token rightParent = null; // ')'
    private Stmt ifStmt = null;
    private Token elseTk = null; // MAY exist 'else'
    private Stmt elseStmt = null; // MAY exist else statement
    private StmtCond stmtCond = null;

    public StmtCondParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtCond parseStmtCond() {
        this.ifTK = this.iterator.readNextToken();
        if (!this.ifTK.getType().equals(TokenType.IFTK)) {
            System.out.println("EXPECT IFTK IN STMTCONDPARSER");
        }
        this.leftParent = this.iterator.readNextToken();
        if (!this.leftParent.getType().equals(TokenType.LPARENT)) {
            System.out.println("EXPECT LEFTPARENT IN STMTCONDPARSER");
        }
        CondParser condParser = new CondParser(this.iterator);
        this.cond = condParser.parseCond();
        this.rightParent = this.iterator.readNextToken();
        if (!this.rightParent.getType().equals(TokenType.RPARENT)) {
            System.out.println("EXPECT RPARENT IN STMTCONDPARSER");
        }
        StmtParser stmtParser = new StmtParser(this.iterator);
        this.ifStmt = stmtParser.parseStmt();
        this.elseTk = this.iterator.readNextToken();
        if (this.elseTk.getType().equals(TokenType.ELSETK)) {
            this.elseStmt = stmtParser.parseStmt();
            this.stmtCond = new StmtCond(this.ifTK, this.leftParent,
                    this.cond, this.rightParent, this.ifStmt, this.elseTk, this.elseStmt);
        } else {
            this.iterator.unReadToken(1);
            this.stmtCond = new StmtCond(this.ifTK, this.leftParent,
                    this.cond, this.rightParent, this.ifStmt);
        }
        return this.stmtCond;
    }
}
