package frontend.parser.expression.unaryexp;

import frontend.lexer.Token;
import frontend.lexer.TokenListIterator;
import frontend.lexer.TokenType;
import frontend.parser.expression.FuncRParams;
import frontend.parser.expression.FuncRParamsParser;
import frontend.parser.terminal.Ident;
import frontend.parser.terminal.IdentParser;

public class UnaryExpFuncParser {
    private TokenListIterator iterator;
    /* UnaryExpFunc Attributes */
    private Ident ident = null;
    private FuncRParams funcRParams = null;
    private Token leftParent; // '('
    private Token rightParent; // ')'
    private UnaryExpFunc unaryExpFunc = null;

    public UnaryExpFuncParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public UnaryExpFunc parseUnaryFuncExp() {
        IdentParser identParser = new IdentParser(this.iterator);
        this.ident = identParser.parseIdent();
        this.leftParent = this.iterator.readNextToken();
        this.rightParent = this.iterator.readNextToken();
        if (!this.rightParent.getType().equals(TokenType.RPARENT)) {
            this.iterator.unReadToken(1);
            FuncRParamsParser funcRParamsParser = new FuncRParamsParser(this.iterator);
            this.funcRParams = funcRParamsParser.parseFuncRParams();
            this.rightParent = this.iterator.readNextToken();
            unaryExpFunc = new UnaryExpFunc(this.ident, this.funcRParams,
                    this.leftParent, this.rightParent);
        } else {
            unaryExpFunc = new UnaryExpFunc(this.ident, this.leftParent, this.rightParent);
        }
        return unaryExpFunc;
    }
}
