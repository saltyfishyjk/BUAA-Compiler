package frontend.parser.function.functype;

import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.function.functype.FuncTypeEle;

public class FuncTypeVoid implements FuncTypeEle {
    private Token voidTk; // 'void'

    public FuncTypeVoid(Token voidTk) {
        this.voidTk = voidTk;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.voidTk.syntaxOutput());
        return sb.toString();
    }

    @Override
    public TokenType getType() {
        return voidTk.getType();
    }
}
