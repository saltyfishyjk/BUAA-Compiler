package frontend.parser.expression.unaryexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.parser.expression.FuncRParams;
import frontend.parser.expression.FuncRParamsParser;
import frontend.parser.terminal.Ident;

public class UnaryExpFuncParser {
    private TokenListIterator iterator;
    /* UnaryExpFunc Attributes */
    private Ident ident = null;
    private FuncRParams funcRParams = null;
    private Token leftParent; // '('
    private Token rightParent; // ')'

    public UnaryExpFuncParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public UnaryExpFunc parseUnaryFuncExp() {
        this.leftParent = this.iterator.readNextToken();
        FuncRParamsParser funcRParamsParser = new FuncRParamsParser(this.iterator);
        this.funcRParams = funcRParamsParser.parseFuncRParams();
        this.rightParent = this.iterator.readNextToken();
        UnaryExpFunc unaryExpFunc = new UnaryExpFunc(this.ident, this.funcRParams,
                this.leftParent, this.rightParent);
        return unaryExpFunc;
    }
}
