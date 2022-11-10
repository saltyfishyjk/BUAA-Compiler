package frontend.parser.expression.unaryexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.Exp;
import frontend.parser.expression.FuncRParams;
import frontend.parser.expression.FuncRParamsParser;
import frontend.parser.terminal.Ident;
import frontend.parser.terminal.IdentParser;
import middle.error.Error;
import middle.error.ErrorTable;
import middle.error.ErrorType;
import middle.symbol.Symbol;
import middle.symbol.SymbolFunc;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class UnaryExpFuncParser {
    private TokenListIterator iterator;
    /* UnaryExpFunc Attributes */
    private Ident ident = null;
    private FuncRParams funcRParams = null;
    private Token leftParent; // '('
    private Token rightParent; // ')'
    private UnaryExpFunc unaryExpFunc = null;
    private SymbolTable curSymbolTable;
    private int dimension;

    public UnaryExpFuncParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public UnaryExpFuncParser(TokenListIterator iterator, SymbolTable curSymbolTable) {
        this.iterator = iterator;
        this.curSymbolTable = curSymbolTable;
    }

    public UnaryExpFunc parseUnaryFuncExp() {
        IdentParser identParser = new IdentParser(this.iterator);
        this.ident = identParser.parseIdent();
        /* 处理c类错误：未定义名字 & 获取维数 */
        handleCError(this.ident);
        this.leftParent = this.iterator.readNextToken();
        this.rightParent = this.iterator.readNextToken();
        /* 处理j类错误：缺失 ) */
        if (this.rightParent.getType().equals(TokenType.SEMICN)) {
            handleJError(this.rightParent);
        }
        if (!this.rightParent.getType().equals(TokenType.RPARENT)) {
            this.iterator.unReadToken(1);
            // FuncRParamsParser funcRParamsParser = new FuncRParamsParser(this.iterator);
            FuncRParamsParser funcRParamsParser = new FuncRParamsParser(this.iterator,
                    this.curSymbolTable);
            this.funcRParams = funcRParamsParser.parseFuncRParams();
            this.rightParent = this.iterator.readNextToken();
            if (this.rightParent.getType().equals(TokenType.SEMICN)) {
                handleJError(this.rightParent);
            }
            unaryExpFunc = new UnaryExpFunc(this.ident, this.funcRParams,
                    this.leftParent, this.rightParent, this.dimension);
        } else {
            unaryExpFunc = new UnaryExpFunc(this.ident, this.leftParent,
                    this.rightParent, this.dimension);
        }
        /* 处理d类错误：函数参数个数不匹配 */
        handleDError(funcRParams);
        /* 处理e类错误：函数参数类型不匹配 */
        handleEError(funcRParams);
        return unaryExpFunc;
    }

    /* 处理c类错误：未定义名字 & 获取维数 */
    private void handleCError(Ident ident) {
        if (this.curSymbolTable.checkCTypeError(ident.getName())) {
            Error error = new Error(ident.getLineNum(), ErrorType.UNDEFINED_IDENT);
            ErrorTable.addError(error);
            this.dimension = -2; // 非法数据
        } else {
            Symbol symbol = this.curSymbolTable.getSymbol(ident.getName());
            this.dimension = symbol.getDimension();
        }
    }

    private void handleJError(Token token) {
        if (!token.getType().equals(TokenType.RPARENT)) {
            this.rightParent = new Token(TokenType.RPARENT, this.leftParent.getLineNum(), ")");
            this.iterator.unReadToken(2);
            Error error = new Error(this.iterator.readNextToken().getLineNum(),
                    ErrorType.MISSING_R_PARENT);
            ErrorTable.addError(error);
        }
    }

    /* 处理d类错误：函数参数个数不匹配 */
    private void handleDError(FuncRParams funcRParams) {
        Symbol symbol = this.curSymbolTable.getSymbol(this.ident.getName());
        if (!(symbol instanceof SymbolFunc)) {
            return;
        }
        SymbolFunc symbolFunc = (SymbolFunc) symbol;
        /* 处理没有参数的正确情况 */
        if (funcRParams == null && symbolFunc.getSymbols().size() == 0) {
            return;
        }
        if ((funcRParams == null && symbolFunc.getSymbols().size() != 0)
                || (symbolFunc.getSymbols().size() != funcRParams.getNum())) {
            Error error = new Error(this.ident.getLineNum(), ErrorType.MISMATCH_PARAM_NUM);
            ErrorTable.addError(error);
        }
    }

    /* 处理e类错误：函数参数类型不匹配 */
    private void handleEError(FuncRParams funcRParams) {
        /* 没有该名字的函数，说明已经被c类错误处理过 */
        Symbol symbol = this.curSymbolTable.getSymbol(this.ident.getName());
        if (!(symbol instanceof SymbolFunc)) {
            return;
        }
        SymbolFunc symbolFunc = (SymbolFunc) symbol;
        ArrayList<Symbol> symbols = symbolFunc.getSymbols();
        /* 无参数说明为正确情况，不应当处理 */
        if (this.funcRParams == null && symbols.size() == 0) {
            return;
        }
        /* 参数列表长度不匹配，说明已经被作为d类错误处理过 */
        if ((this.funcRParams == null && symbols.size() != 0) ||
                this.funcRParams.getExps().size() != symbols.size()) {
            return;
        }

        ArrayList<Exp> exps = this.funcRParams.getExps();
        int len = symbols.size();
        for (int i = 0; i < len; i++) {
            Exp exp = exps.get(i);
            symbol = symbols.get(i);
            if (exp.getDimension() != symbol.getDimension()) {
                /* TODO 处理exp维数问题 */
                Error error = new Error(this.ident.getLineNum(), ErrorType.MISMATCH_PARAM_TYPE);
                ErrorTable.addError(error);
                break;
            }
        }
    }
}
