package frontend.parser.declaration.constant.constinitval;

import frontend.lexer.Token;

import java.util.ArrayList;

public class ConstInitValMulti implements ConstInitValEle {
    private Token leftBrace; // '{'
    private ConstInitVal constInitVal; // may exist or not
    private ArrayList<Token> commas;
    private ArrayList<ConstInitVal> constInitVals;
    private Token rightBrace; // '}'

    public ConstInitValMulti(Token leftBrace,
                             ConstInitVal constInitval,
                             ArrayList<Token> commas,
                             ArrayList<ConstInitVal> constInitVals,
                             Token rightBrace) {
        this.leftBrace = leftBrace;
        this.constInitVal = constInitval;
        this.commas = commas;
        this.constInitVals = constInitVals;
        this.rightBrace = rightBrace;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.leftBrace.syntaxOutput());
        if (this.constInitVal != null) {
            sb.append(this.constInitVal);
            if (this.commas != null && this.constInitVals != null
                    && this.commas.size() == this.constInitVals.size()) {
                int len = this.commas.size();
                for (int i = 0; i < len; i++) {
                    sb.append(this.commas.get(i).syntaxOutput());
                    sb.append(this.constInitVals.get(i).syntaxOutput());
                }
            }
        }
        sb.append(this.rightBrace.syntaxOutput());
        return sb.toString();
    }
}
