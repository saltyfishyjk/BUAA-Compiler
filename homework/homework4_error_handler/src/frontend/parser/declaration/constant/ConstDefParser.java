package frontend.parser.declaration.constant;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.terminal.Ident;
import frontend.parser.declaration.constant.constinitval.ConstInitVal;
import frontend.parser.declaration.constant.constinitval.ConstInitValParser;
import frontend.parser.expression.ConstExp;
import frontend.parser.expression.ConstExpParser;
import frontend.parser.terminal.IdentParser;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
import middle.symbol.Symbol;
import middle.symbol.SymbolCon;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;

import java.util.ArrayList;

public class ConstDefParser {
    private TokenListIterator iterator;
    /* ConstDef Attributes */
    private Ident ident;
    private ArrayList<Token> leftBrackets = new ArrayList<>();
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private ArrayList<Token> rightBrackets = new ArrayList<>();
    private Token eq; // =
    private ConstInitVal constInitVal;
    private SymbolTable curSymbolTable;

    public ConstDefParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public ConstDefParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public ConstDef parseConstDef() {
        this.leftBrackets = new ArrayList<>();
        this.constExps = new ArrayList<>();
        this.rightBrackets = new ArrayList<>();
        IdentParser identParser = new IdentParser(this.iterator);
        ident = identParser.parseIdent();
        Token token = iterator.readNextToken();
        while (token.getType().equals(TokenType.LBRACK)) {
            /* '[' */
            this.leftBrackets.add(token);
            /* ConstExp */
            // ConstExpParser constExpParser = new ConstExpParser(this.iterator);
            ConstExpParser constExpParser = new ConstExpParser(this.iterator, this.curSymbolTable);
            ConstExp constExp = constExpParser.parseConstExp();
            this.constExps.add(constExp);
            token = this.iterator.readNextToken();
            /* ']' */
            /* 处理k类错误：缺失 ] */
            handleKError(token);
            this.rightBrackets.add(token);
            token = this.iterator.readNextToken();
        }
        this.eq = token;
        // ConstInitValParser constInitValParser = new ConstInitValParser(this.iterator);
        ConstInitValParser constInitValParser = new ConstInitValParser(this.iterator,
                this.curSymbolTable);
        this.constInitVal = constInitValParser.parseConstInitVal();
        ConstDef constDef = new ConstDef(this.ident, this.leftBrackets, this.constExps,
                this.rightBrackets, this.eq, this.constInitVal);
        /* 添加新符号 & 处理b类错误：名字重定义 */
        addSymbol();
        return constDef;
    }

    private void handleKError(Token token) {
        if (!token.getType().equals(TokenType.RBRACK)) {
            this.iterator.unReadToken(2);
            Token lastToken = this.iterator.readNextToken();
            Error error = new Error(lastToken.getLineNum(), ErrorType.MISSING_R_BACKET);
            ErrorTable.addError(error);
        }
    }

    private void addSymbol() {
        SymbolType symbolType;
        int dimension = 0;
        if (this.leftBrackets.size() == 0) {
            symbolType = SymbolType.CON;
            dimension = 0;
        } else if (this.leftBrackets.size() == 1) {
            symbolType = SymbolType.CON1;
            dimension = 1;
        } else if (this.leftBrackets.size() == 2) {
            symbolType = SymbolType.CON2;
            dimension = 2;
        } else {
            symbolType = null;
            dimension = -1;
            System.out.println("ERROR in ConstDefParser!");
        }
        Symbol symbol = new SymbolCon(this.ident.getLineNum(), this.ident.getName(),
                symbolType, dimension);
        /* TODO : ADD CONST INIT */
        /* 处理b类错误：名字重定义行为 */
        if (this.curSymbolTable.checkBTypeError(symbol)) {
            Error error = new Error(symbol.getLineNum(), ErrorType.DUPLICATED_IDENT);
            ErrorTable.addError(error);
        } else {
            this.curSymbolTable.addSymol(symbol);
        }
    }
}
