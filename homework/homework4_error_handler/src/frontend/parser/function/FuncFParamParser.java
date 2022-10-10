package frontend.parser.function;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.declaration.BType;
import frontend.parser.declaration.BTypeParser;
import frontend.parser.expression.ConstExp;
import frontend.parser.expression.ConstExpParser;
import frontend.parser.terminal.Ident;
import frontend.parser.terminal.IdentParser;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
import middle.symbol.Symbol;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;
import middle.symbol.SymbolVar;

import java.util.ArrayList;

public class FuncFParamParser {
    private TokenListIterator iterator;
    /* FuncFParam Attributes */
    private BType btype = null;
    private Ident ident = null;
    private Token leftBracketFirst = null;
    private Token rightBracketFirst = null;
    private ArrayList<Token> leftBrackets = new ArrayList<>();
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private ArrayList<Token> rightBrackets = new ArrayList<>();
    private FuncFParam funcFParam = null;
    private SymbolTable curSymbolTable;
    private Symbol symbol;

    public FuncFParamParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public FuncFParamParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public FuncFParam parseFuncFParam() {
        this.leftBrackets = new ArrayList<>();
        this.constExps = new ArrayList<>();
        this.rightBrackets = new ArrayList<>();
        BTypeParser btypeParser = new BTypeParser(this.iterator);
        this.symbol = null;
        this.btype = btypeParser.parseBtype();
        IdentParser identParser = new IdentParser(this.iterator);
        this.ident = identParser.parseIdent();
        this.leftBracketFirst = this.iterator.readNextToken();
        /* '[' */
        if (this.leftBracketFirst.getType().equals(TokenType.LBRACK)) {
            /* ']' */
            this.rightBracketFirst = this.iterator.readNextToken();
            /* 处理k类错误：缺失 ] */
            handleKError(this.rightBracketFirst);
            Token token = this.iterator.readNextToken();
            while (token.getType().equals(TokenType.LBRACK)) {
                this.leftBrackets.add(token);
                // ConstExpParser constExpParser = new ConstExpParser(this.iterator);
                ConstExpParser constExpParser = new ConstExpParser(this.iterator,
                        this.curSymbolTable);
                this.constExps.add(constExpParser.parseConstExp());
                this.rightBrackets.add(this.iterator.readNextToken());
                /* 处理k类错误：缺失 ] */
                this.handleKError(this.rightBrackets.get(this.rightBrackets.size() - 1));
                token = this.iterator.readNextToken();
            }
            this.iterator.unReadToken(1);
            this.funcFParam = new FuncFParam(this.btype, this.ident, this.leftBracketFirst,
                    this.rightBracketFirst, this.leftBrackets, this.constExps, this.rightBrackets);
        } else {
            this.iterator.unReadToken(1);
            this.funcFParam = new FuncFParam(this.btype, this.ident);
        }
        addSymbol();
        return this.funcFParam;
    }

    private void handleKError(Token token) {
        if (!token.getType().equals(TokenType.RBRACK)) {
            this.iterator.unReadToken(2);
            Token lastToken = this.iterator.readNextToken();
            Error error = new Error(lastToken.getLineNum(), ErrorType.MISSING_R_BACKET);
            ErrorTable.addError(error);
        }
    }

    public Symbol getSymbol() {
        return this.symbol;
    }

    public void addSymbol() {
        /* 生成新符号 */
        SymbolType symbolType;
        int dimension;
        if (this.leftBracketFirst.getType().equals(TokenType.LBRACK) &&
            this.leftBrackets.size() == 1) {
            dimension = 2;
            symbolType = SymbolType.VAR2;
        } else if (this.leftBracketFirst.getType().equals(TokenType.LBRACK)) {
            dimension = 1;
            symbolType = SymbolType.VAR1;
        } else {
            dimension = 0;
            symbolType = SymbolType.VAR;
        }
        Symbol symbol = new SymbolVar(this.ident.getLineNum(), this.ident.getName(),
                symbolType, dimension);
        /* 处理b类错误：名字重定义行为 */
        if (this.curSymbolTable.checkBTypeError(symbol)) {
            Error error = new Error(symbol.getLineNum(), ErrorType.DUPLICATED_IDENT);
            ErrorTable.addError(error);
        } else {
            this.symbol = symbol;
            this.curSymbolTable.addSymol(symbol);
        }
    }
}
