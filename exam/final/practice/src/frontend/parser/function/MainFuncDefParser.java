package frontend.parser.function;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.statement.Block;
import frontend.parser.statement.BlockParser;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
import middle.symbol.SymbolFunc;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;

public class MainFuncDefParser {
    private TokenListIterator iterator;
    /* MainFuncDef Attributes */
    private Token intTk; // 'int'
    private Token mainTk; // 'main'
    private Token leftParent; // '('
    private Token rightParent; // ')'
    private Block block;
    private BlockParser blockParser;
    private SymbolFunc symbolFunc;
    private SymbolTable curSymbolTable;

    public MainFuncDefParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public MainFuncDefParser(TokenListIterator iterator,
                             SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public MainFuncDef parseMainFuncDef() {
        this.intTk = this.iterator.readNextToken();
        this.mainTk = this.iterator.readNextToken();
        /* 添加新符号 & 处理b类错误：名字重定义 */
        addFuncSymbol();
        this.leftParent = this.iterator.readNextToken();
        this.rightParent = this.iterator.readNextToken();
        /* 处理j类错误：缺失 ) */
        handleJError(this.rightParent);
        this.blockParser = new BlockParser(this.iterator, this.curSymbolTable, 2);
        // this.blockParser = new BlockParser(this.iterator);
        this.block = this.blockParser.parseBlock();
        /* 处理g类错误：缺失有返回值的return语句 */
        handleGError();
        MainFuncDef mainFuncDef = new MainFuncDef(this.intTk, this.mainTk,
                this.leftParent, this.rightParent, this.block);
        return mainFuncDef;
    }

    private void addFuncSymbol() {
        /* 生成新符号 */
        SymbolType symbolType = SymbolType.FUNC;
        int dimension = 0;
        this.symbolFunc = new SymbolFunc(this.mainTk.getLineNum(),
                "main", symbolType);
        this.symbolFunc.setDimension(0); // int
        /* 检查b类错误 */
        if (this.curSymbolTable.checkBTypeError(symbolFunc)) {
            Error error = new Error(symbolFunc.getLineNum(), ErrorType.DUPLICATED_IDENT);
            ErrorTable.addError(error);
        }
        this.curSymbolTable = new SymbolTable(this.curSymbolTable);
    }

    private void handleJError(Token token) {
        if (!token.getType().equals(TokenType.RPARENT)) {
            this.iterator.unReadToken(2);
            Error error = new Error(this.iterator.readNextToken().getLineNum(),
                    ErrorType.MISSING_R_PARENT);
            ErrorTable.addError(error);
        }
    }

    private void handleGError() {
        if (this.blockParser.checkReturn() != 2) {
            Error error = new Error(this.blockParser.getRightBraceLineNum(),
                    ErrorType.MISSING_RETURN);
            ErrorTable.addError(error);
        }
    }
}
