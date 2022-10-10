package frontend.parser.function;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.FuncFParamsParser;
import frontend.parser.function.functype.FuncType;
import frontend.parser.function.functype.FuncTypeParser;
import frontend.parser.statement.Block;
import frontend.parser.statement.BlockParser;
import frontend.parser.statement.blockitem.BlockItemEle;
import frontend.parser.statement.stmt.Stmt;
import frontend.parser.statement.stmt.StmtReturn;
import frontend.parser.terminal.Ident;
import frontend.parser.terminal.IdentParser;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
import middle.symbol.Symbol;
import middle.symbol.SymbolFunc;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;

import java.util.ArrayList;

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
    private SymbolFunc symbolFunc;
    private BlockParser blockParser;

    public FuncDefParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public FuncDefParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public FuncDef parseFuncDef() {
        FuncTypeParser funcTypeParser = new FuncTypeParser(this.iterator);
        this.symbolFunc = null;
        this.funcType = funcTypeParser.parseFuncType();
        IdentParser identParser = new IdentParser(this.iterator);
        this.ident = identParser.parseIdent();
        this.leftParent = this.iterator.readNextToken();
        this.rightParent = this.iterator.readNextToken();
        /* 添加函数符号 & 处理b类错误：名字重定义*/
        addFuncSymbol();
        /* '{' */
        if (this.rightParent.getType().equals(TokenType.LBRACE)) {
            /* 处理无参数时j类错误：缺失 ) */
            handleJError(this.rightParent);
            /* 补全右括号，使可以按照原有正确逻辑继续处理 */
            this.rightParent = new Token(TokenType.RPARENT, this.leftParent.getLineNum(), ")");
        }
        if (!this.rightParent.getType().equals(TokenType.RPARENT)) {
            this.iterator.unReadToken(1);
            // FuncFParamsParser funcFParamsParser = new FuncFParamsParser(this.iterator);
            FuncFParamsParser funcFParamsParser = new FuncFParamsParser(this.iterator,
                    this.curSymbolTable);
            this.funcFParams = funcFParamsParser.parseFuncFParams();
            addFuncParamsSymbol(funcFParamsParser.getSymbols());
            /* ')' */
            this.rightParent = this.iterator.readNextToken();
            /* 处理有参数时j类错误：缺失 ) */
            handleJError(this.rightParent);
            setBlockParser();
            // BlockParser blockParser = new BlockParser(this.iterator);
            this.block = this.blockParser.parseBlock();
            /* 处理f和g类错误：*/
            handleFandGError();
            this.funcDef = new FuncDef(this.funcType, this.ident, this.leftParent,
                    this.funcFParams, this.rightParent, this.block);

        } else {
            setBlockParser();
            // BlockParser blockParser = new BlockParser(this.iterator);
            this.block = this.blockParser.parseBlock();
            /* 处理f和g类错误 */
            handleFandGError();
            this.funcDef = new FuncDef(this.funcType, this.ident, this.leftParent,
                    this.rightParent, this.block);
        }

        return funcDef;
    }

    private void handleJError(Token token) {
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
        this.symbolFunc = new SymbolFunc(this.ident.getLineNum(),
                this.ident.getName(), symbolType);
        if (this.funcType.getType().equals(TokenType.VOIDTK)) {
            this.symbolFunc.setDimension(-1);
        } else if (this.funcType.getType().equals(TokenType.INTTK)) {
            this.symbolFunc.setDimension(0);
        }
        /* 检查b类错误：名字重定义 */
        if (this.curSymbolTable.checkBTypeError(symbolFunc)) {
            Error error = new Error(symbolFunc.getLineNum(), ErrorType.DUPLICATED_IDENT);
            ErrorTable.addError(error);
        } else {
            this.curSymbolTable.addSymol(this.symbolFunc);
        }
        /* 创建新的子符号表，形参与Block块内符号加入该新表 */
        this.curSymbolTable = new SymbolTable(this.curSymbolTable);
    }

    private void addFuncParamsSymbol(ArrayList<Symbol> symbols) {
        for (Symbol symbol : symbols) {
            this.symbolFunc.addSymbol(symbol);
        }
    }

    private void setBlockParser() {
        if (this.funcType.getType().equals(TokenType.VOIDTK)) {
            blockParser = new BlockParser(this.iterator, this.curSymbolTable, 1);
        } else {
            blockParser = new BlockParser(this.iterator, this.curSymbolTable, 2);
        }
    }

    private void handleFandGError() {
        if (this.funcType.getType().equals(TokenType.VOIDTK)) {
            /* f error */
            if (this.blockParser.checkReturn() == 2) {
                BlockItemEle blockItemEle = this.blockParser.getLastBlockItem().getBlockItemEle();
                Stmt stmt = ((Stmt)blockItemEle); // MUST BE RETURN STMT
                StmtReturn stmtReturn = (StmtReturn)stmt.getStmtEle();
                int lineNum = stmtReturn.getReturnLineNum();
                Error error = new Error(lineNum, ErrorType.RETURN_VALUE_VOID);
                ErrorTable.addError(error);
            }
        } else {
            /* g error */
            if (this.blockParser.checkReturn() != 2) {
                Error error = new Error(this.blockParser.getRightBraceLineNum(),
                        ErrorType.MISSING_RETURN);
                ErrorTable.addError(error);
            }
        }
    }
}
