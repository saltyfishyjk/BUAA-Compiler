package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.Exp;
import frontend.parser.expression.ExpParser;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
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
        /* 处理i类错误：缺失; */
        handleIError(this.semicn);
        StmtExp stmtExp = new StmtExp(this.exp, this.semicn);
        return stmtExp;
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
}
