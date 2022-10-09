package frontend.parser.function;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.FuncFParamsParser;
import frontend.parser.function.functype.FuncType;
import frontend.parser.function.functype.FuncTypeParser;
import frontend.parser.statement.Block;
import frontend.parser.statement.BlockParser;
import frontend.parser.terminal.Ident;
import frontend.parser.terminal.IdentParser;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
import middle.symbol.SymbolFunc;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;

public class FuncDefParser {
    private TokenListIterator iterator;
    /* FuncDef Attributes */
    private FuncType funcType = null;
    private Ident ident = null;
    private Token leftParent = null; // '('
    private FuncFParams funcFParams = null; // MAY exist
    private Token rightParent = null; // ')'
    private Block block = null;
    private FuncDef funcDef = null;
    private SymbolTable curSymbolTable;

    public FuncDefParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public FuncDefParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public FuncDef parseFuncDef() {
        FuncTypeParser funcTypeParser = new FuncTypeParser(this.iterator);
        this.funcType = funcTypeParser.parseFuncType();
        IdentParser identParser = new IdentParser(this.iterator);
        this.ident = identParser.parseIdent();
        this.leftParent = this.iterator.readNextToken();
        this.rightParent = this.iterator.readNextToken();
        /* 添加函数符号 & 处理b类错误：名字重定义*/
        addFuncSymbol();
        /* '{' */
        if (this.rightParent.getType().equals(TokenType.LBRACE)) {
            /* 处理无参数时g类错误：缺失 ) */
            handleGError(this.rightParent);
            /* 补全右括号，使可以按照原有正确逻辑继续处理 */
            this.rightParent = new Token(TokenType.RPARENT, this.leftParent.getLineNum(), ")");
        }
        if (!this.rightParent.getType().equals(TokenType.RPARENT)) {
            this.iterator.unReadToken(1);
            // FuncFParamsParser funcFParamsParser = new FuncFParamsParser(this.iterator);
            FuncFParamsParser funcFParamsParser = new FuncFParamsParser(this.iterator, this.curSymbolTable);
            this.funcFParams = funcFParamsParser.parseFuncFParams();
            /* ')' */
            this.rightParent = this.iterator.readNextToken();
            /* 处理有参数时g类错误：缺失 ) */
            handleGError(this.rightParent);
            BlockParser blockParser = new BlockParser(this.iterator);
            this.block = blockParser.parseBlock();
            this.funcDef = new FuncDef(this.funcType, this.ident, this.leftParent,
                    this.funcFParams, this.rightParent, this.block);
        } else {
            BlockParser blockParser = new BlockParser(this.iterator);
            this.block = blockParser.parseBlock();
            this.funcDef = new FuncDef(this.funcType, this.ident, this.leftParent,
                    this.rightParent, this.block);
        }

        return funcDef;
    }

    private void handleGError(Token token) {
        if (!token.getType().equals(TokenType.RPARENT)) {
            this.iterator.unReadToken(2);
            Error error = new Error(this.iterator.readNextToken().getLineNum(),
                    ErrorType.MISSING_R_PARENT);
            ErrorTable.addError(error);
        }
    }

    private void addFuncSymbol() {
        /* add func symbol */
        SymbolType symbolType = SymbolType.FUNC;
        SymbolFunc symbolFunc = new SymbolFunc(this.ident.getLineNum(),
                this.ident.getName(), symbolType);
        if (this.funcType.getType().equals(TokenType.VOIDTK)) {
            symbolFunc.setDimension(-1);
        } else if (this.funcType.getType().equals(TokenType.INTTK)) {
            symbolFunc.setDimension(0);
        }
        /* 创建新的子符号表，形参与Block块内符号加入本表 */
        curSymbolTable = new SymbolTable(this.curSymbolTable);
    }

    private void addFuncParamsSymbol() {
        /* TODO */
    }
}
