package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.Cond;
import frontend.parser.expression.CondParser;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
import middle.symbol.SymbolTable;

public class StmtWhileParser {
    private TokenListIterator iterator;
    /* StmtWhile Attributes */
    private Token whileTk; // 'while'
    private Token leftParent; // '('
    private Cond cond;
    private Token rightParent; // ')'
    private Stmt stmt;
    private SymbolTable curSymbolTable;

    public StmtWhileParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtWhileParser(TokenListIterator iterator,
                           SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
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
        // CondParser condParser = new CondParser(this.iterator);
        CondParser condParser = new CondParser(this.iterator, this.curSymbolTable);
        this.cond = condParser.parseCond();
        this.rightParent = this.iterator.readNextToken();
        /* 处理j类错误：缺失 ) */
        handleJError(this.rightParent);
        /* 进入循环体，创建新的子符号表，并使其深度+1 */
        SymbolTable symbolTable = new SymbolTable(this.curSymbolTable);
        symbolTable.setCycleDepth(this.curSymbolTable.getCycleDepth() + 1);
        StmtParser stmtParser = new StmtParser(this.iterator, symbolTable);
        this.stmt = stmtParser.parseStmt();
        StmtWhile stmtWhile = new StmtWhile(this.whileTk, this.leftParent,
                this.cond, this.rightParent, this.stmt);
        return stmtWhile;
    }

    private void handleJError(Token token) {
        if (!token.getType().equals(TokenType.RPARENT)) {
            this.iterator.unReadToken(2);
            Error error = new Error(this.iterator.readNextToken().getLineNum(),
                    ErrorType.MISSING_R_PARENT);
            ErrorTable.addError(error);
        }
    }

}
