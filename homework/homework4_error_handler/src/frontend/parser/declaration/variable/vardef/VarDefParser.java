package frontend.parser.declaration.variable.vardef;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.declaration.variable.initval.InitVal;
import frontend.parser.declaration.variable.initval.InitValParser;
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

public class VarDefParser {
    private TokenListIterator iterator;
    /* VarDef Attributes */
    private Ident ident = null;
    private ArrayList<Token> leftBrackets = new ArrayList<>();
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private ArrayList<Token> rightBrackets = new ArrayList<>();
    /* Init Val */
    private Token eq = null;
    private InitVal initVal = null;
    /* VarDefEle */
    private VarDefEle varDefEle = null;
    private SymbolTable curSymbolTable;

    public VarDefParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public VarDefParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public VarDef parseVarDef() {
        this.leftBrackets = new ArrayList<>();
        this.constExps = new ArrayList<>();
        this.rightBrackets = new ArrayList<>();
        IdentParser identParser = new IdentParser(this.iterator);
        this.ident = identParser.parseIdent();
        Token token = this.iterator.readNextToken();
        /* '[' */
        while (token.getType().equals(TokenType.LBRACK)) {
            this.leftBrackets.add(token);
            // ConstExpParser expParser = new ConstExpParser(this.iterator);
            ConstExpParser expParser = new ConstExpParser(this.iterator, this.curSymbolTable);
            this.constExps.add(expParser.parseConstExp());
            token = this.iterator.readNextToken();
            /* ']' */
            /* 处理k类错误：缺失 ] */
            handleKError(token);
            this.rightBrackets.add(token);
            token = this.iterator.readNextToken();
        }
        if (token.getType().equals(TokenType.ASSIGN)) { // '='
            this.eq = token;
            // InitValParser initValParser = new InitValParser(this.iterator);
            InitValParser initValParser = new InitValParser(this.iterator, this.curSymbolTable);
            this.initVal = initValParser.parseInitVal();
            this.varDefEle = new VarDefInit(this.ident, this.leftBrackets,
                    this.constExps, this.rightBrackets, this.eq, this.initVal);
        } else {
            // token now is ';', need to backspace
            this.iterator.unReadToken(1);
            this.varDefEle = new VarDefNull(this.ident, this.leftBrackets,
                    this.constExps, this.rightBrackets);
        }
        /* 添加新符号 & 处理b类错误：名字重定义 */
        addSymbol();
        VarDef varDef = new VarDef(this.varDefEle);
        return varDef;
    }

    private void handleKError(Token token) {
        if (!token.getType().equals(TokenType.RBRACK)) {
            this.iterator.unReadToken(2);
            Error error = new Error(this.iterator.readNextToken().getLineNum(),
                    ErrorType.MISSING_R_BACKET);
            ErrorTable.addError(error);
        }
    }

    private void addSymbol() {
        /* 生成新符号 */
        SymbolType symbolType;
        int dimension = 0;
        if (this.leftBrackets.size() == 0) {
            symbolType = SymbolType.VAR;
            dimension = 0;
        } else if (this.leftBrackets.size() == 1) {
            symbolType = SymbolType.VAR1;
            dimension = 1;
        } else if (this.leftBrackets.size() == 2) {
            symbolType = SymbolType.VAR2;
            dimension = 2;
        } else {
            symbolType = null;
            dimension = -1;
            System.out.println("ERROR in VarDefParser!");
        }
        Symbol symbol = new SymbolVar(this.ident.getLineNum(), this.ident.getName(),
                symbolType, dimension);
        /* TODO : ADD VAR INIT */
        /* 处理b类错误：名字重定义行为 */
        if (this.curSymbolTable.checkBTypeError(symbol)) {
            Error error = new Error(symbol.getLineNum(), ErrorType.DUPLICATED_IDENT);
            ErrorTable.addError(error);
        } else {
            this.curSymbolTable.addSymol(symbol);
        }
    }
}
