package frontend.parser.expression.primaryexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.Exp;
import frontend.parser.expression.ExpParser;
import frontend.parser.terminal.Ident;
import frontend.parser.terminal.IdentParser;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
import middle.symbol.Symbol;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;

import java.util.ArrayList;

public class LValParser {
    private TokenListIterator iterator;
    /* LVal Attributes */
    private Ident ident = null;
    private ArrayList<Token> leftBrackets = new ArrayList<>(); // '['
    private ArrayList<Exp> exps = new ArrayList<>();
    private ArrayList<Token> rightBrackets = new ArrayList<>();
    private SymbolTable curSymbolTable;
    private SymbolType symbolType;

    public LValParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public LValParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public LVal parseLVal() {
        this.leftBrackets = new ArrayList<>();
        this.exps = new ArrayList<>();
        this.rightBrackets = new ArrayList<>();
        IdentParser identParser = new IdentParser(this.iterator);
        this.ident = identParser.parseIdent();
        /* 处理c类错误：未定义名字 */
        handleCError(this.ident);
        Token token = this.iterator.readNextToken();
        while (token.getType().equals(TokenType.LBRACK)) { // '['
            this.leftBrackets.add(token);
            // ExpParser expParser = new ExpParser(this.iterator);
            ExpParser expParser = new ExpParser(this.iterator, this.curSymbolTable);
            this.exps.add(expParser.parseExp());
            token = this.iterator.readNextToken(); // ']'
            /* 处理k类错误：缺失 ] */
            handleKError(token);
            this.rightBrackets.add(token);
            token = this.iterator.readNextToken();
        }
        this.iterator.unReadToken(1);
        // LVal lval = new LVal(this.ident, this.leftBrackets, this.exps, this.rightBrackets);
        LVal lval = new LVal(this.ident, this.leftBrackets, this.exps, this.rightBrackets,
                getSymolType());
        return lval;
    }

    /* 处理c类错误：未定义名字 */
    private void handleCError(Ident ident) {
        if (this.curSymbolTable.checkCTypeError(ident.getName())) {
            Error error = new Error(ident.getLineNum(), ErrorType.UNDEFINED_IDENT);
            ErrorTable.addError(error);
            this.symbolType = null;
        } else {
            Symbol symbol = this.curSymbolTable.getSymbol(ident.getName());
            this.symbolType = symbol.getSymbolType();
        }
    }

    /* 处理k类错误：缺少右小括号 */
    private void handleKError(Token token) {
        if (!token.getType().equals(TokenType.RBRACK)) {
            this.iterator.unReadToken(2);
            Token lastToken = this.iterator.readNextToken();
            Error error = new Error(lastToken.getLineNum(), ErrorType.MISSING_R_BACKET);
            ErrorTable.addError(error);
        }
    }

    public SymbolType getSymolType() {
        return this.symbolType;
    }
}
