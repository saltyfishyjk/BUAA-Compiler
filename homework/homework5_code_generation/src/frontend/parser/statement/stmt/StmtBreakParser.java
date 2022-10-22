package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
import middle.symbol.SymbolTable;

public class StmtBreakParser {
    private TokenListIterator iterator;
    /* StmtBreak Attributes */
    private Token breakTk; // 'break'
    private Token semicn; // ';'
    private SymbolTable curSymbolTable;

    public StmtBreakParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtBreakParser(TokenListIterator iterator,
                           SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public StmtBreak parseStmtBreak() {
        this.breakTk = this.iterator.readNextToken();
        /* 处理M类错误：在非循环块中使用break语句 */
        handleMError();
        if (!this.breakTk.getType().equals(TokenType.BREAKTK)) {
            System.out.println("EXPECT BREAKTK IN STMTBREAKPARSER");
        }
        this.semicn = this.iterator.readNextToken();
        handleIError(this.semicn);
        StmtBreak stmtBreak = new StmtBreak(this.breakTk, this.semicn);
        return stmtBreak;
    }

    private void handleMError() {
        if (this.curSymbolTable.getCycleDepth() <= 0) {
            Error error = new Error(this.breakTk.getLineNum(), ErrorType.MISUSE_END_LOOP);
            ErrorTable.addError(error);
        }
    }

    private void handleIError(Token token) {
        if (!token.getType().equals(TokenType.SEMICN)) {
            this.iterator.unReadToken(2); // 后退两格以方便确定分号上一个非终结符位置
            Error error = new Error(this.iterator.readNextToken().getLineNum(),
                    ErrorType.MISSING_SEMICN);
            ErrorTable.addError(error);
        }
    }

}
