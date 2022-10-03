package frontend.parser.function.functype;

import frontend.lexer.Token;
import frontend.parser.function.functype.FuncTypeEle;

public class FuncTypeInt implements FuncTypeEle {
    private Token intTk; // 'int'

    public FuncTypeInt(Token intTk) {
        this.intTk = intTk;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.intTk.syntaxOutput());
        return sb.toString();
    }
}
