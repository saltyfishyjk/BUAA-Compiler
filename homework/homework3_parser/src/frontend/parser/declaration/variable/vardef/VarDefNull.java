package frontend.parser.declaration.variable.vardef;

import frontend.lexer.Token;
import frontend.parser.terminal.Ident;
import frontend.parser.expression.ConstExp;

import java.util.ArrayList;

public class VarDefNull implements VarDefEle {
    private Ident ident;
    private ArrayList<Token> leftBraces;
    private ArrayList<ConstExp> constExps;
    private ArrayList<Token> rightBraces;

    public VarDefNull(Ident ident,
                      ArrayList<Token> leftBraces,
                      ArrayList<ConstExp> constExps,
                      ArrayList<Token> rightBraces) {
        this.ident = ident;
        this.leftBraces = leftBraces;
        this.constExps = constExps;
        this.rightBraces = rightBraces;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.syntaxOutput());
        if (this.leftBraces != null && this.constExps != null && this.rightBraces != null &&
                this.leftBraces.size() == this.constExps.size() &&
                    this.constExps.size() == this.rightBraces.size()) {
            int len = this.leftBraces.size();
            for (int i = 0; i < len; i++) {
                sb.append(this.leftBraces.get(i).syntaxOutput());
                sb.append(this.constExps.get(i).syntaxOutput());
                sb.append(this.rightBraces.get(i).syntaxOutput());
            }
        }
        return sb.toString();
    }
}
