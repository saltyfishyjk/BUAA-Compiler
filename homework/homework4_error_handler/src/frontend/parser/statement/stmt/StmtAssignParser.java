package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.Exp;
import frontend.parser.expression.ExpParser;
import frontend.parser.expression.primaryexp.LVal;
import frontend.parser.expression.primaryexp.LValParser;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;

public class StmtAssignParser {
    private TokenListIterator iterator;
    /* StmtAssign Attributes */
    private LVal lval = null;
    private Token eq; // '='
    private Exp exp;
    private Token semicn; // ';'
    private SymbolTable curSymbolTable;

    public StmtAssignParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtAssignParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public StmtAssign parseStmtAssign() {
        // LValParser lvalParser = new LValParser(this.iterator);
        LValParser lvalParser = new LValParser(this.iterator, this.curSymbolTable);
        this.lval = lvalParser.parseLVal();
        /* 处理h类错误：修改常量值 */
        handleHError(this.lval);
        this.eq = this.iterator.readNextToken();
        if (!this.eq.getType().equals(TokenType.ASSIGN)) {
            System.out.println("EXPECT = HERE");
        }
        // ExpParser expParser = new ExpParser(this.iterator);
        ExpParser expParser = new ExpParser(this.iterator, this.curSymbolTable);
        this.exp = expParser.parseExp();
        this.semicn = this.iterator.readNextToken();
        /* 处理i类错误：缺失; */
        handleIError(this.semicn);
        StmtAssign stmtAssign = new StmtAssign(this.lval, this.eq, this.exp, this.semicn);
        return stmtAssign;
    }

    private void handleIError(Token token) {
        this.semicn = token;
        if (!this.semicn.getType().equals(TokenType.SEMICN)) {
            this.iterator.unReadToken(2); // 后退两格以方便确定分号上一个非终结符位置
            Error error = new Error(this.iterator.readNextToken().getLineNum(),
                    ErrorType.MISSING_SEMICN);
            ErrorTable.addError(error);
        }
    }

    private void handleHError(LVal lval) {
        if (lval.getSymbolType() == null) {
            return;
        }
        if (lval.getSymbolType().equals(SymbolType.CON) ||
            lval.getSymbolType().equals(SymbolType.CON1) ||
            lval.getSymbolType().equals(SymbolType.CON2)) {
            Error error = new Error(this.lval.getLineNum(), ErrorType.ALTER_CONST);
            ErrorTable.addError(error);
        }
    }
}
