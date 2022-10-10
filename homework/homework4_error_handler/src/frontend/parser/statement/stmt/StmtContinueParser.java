package frontend.parser.statement.stmt;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
import middle.symbol.SymbolTable;

public class StmtContinueParser {
    private TokenListIterator iterator;
    /* StmtContinue Attributes */
    private Token continueTk; // 'continue'
    private Token semicn; // ';'
    private SymbolTable curSymbolTable;

    public StmtContinueParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public StmtContinueParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public StmtContinue parseStmtContinue() {
        this.continueTk = this.iterator.readNextToken();
        handleMError();
        if (!this.continueTk.getType().equals(TokenType.CONTINUETK)) {
            System.out.println("EXPECT CONTINUETK IN STMTCONTINUEPARSER");
        }
        this.semicn = this.iterator.readNextToken();
        handleIError(this.semicn);
        StmtContinue stmtContinue = new StmtContinue(this.continueTk, this.semicn);
        return stmtContinue;
    }

    private void handleMError() {
        if (this.curSymbolTable.getCycleDepth() <= 0) {
            Error error = new Error(this.continueTk.getLineNum(), ErrorType.MISUSE_END_LOOP);
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
