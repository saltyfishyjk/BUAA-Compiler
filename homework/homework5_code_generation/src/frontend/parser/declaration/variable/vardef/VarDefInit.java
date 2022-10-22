package frontend.parser.declaration.variable.vardef;

import frontend.lexer.Token;
import frontend.parser.terminal.Ident;
import frontend.parser.declaration.variable.initval.InitVal;
import frontend.parser.expression.ConstExp;

import java.util.ArrayList;

public class VarDefInit implements VarDefEle {
    private Ident ident;
    private ArrayList<Token> leftBraces; // '['
    private ArrayList<ConstExp> constExps;
    private ArrayList<Token> rightBraces; // ']'
    private Token eq;
    private InitVal initVal;

    public VarDefInit(Ident ident,
                      ArrayList<Token> leftBraces,
                      ArrayList<ConstExp> constExps,
                      ArrayList<Token> rightBraces,
                      Token eq,
                      InitVal initVal) {
        this.ident = ident;
        this.leftBraces = leftBraces;
        this.constExps = constExps;
        this.rightBraces = rightBraces;
        this.eq = eq;
        this.initVal = initVal;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.syntaxOutput());
        if (leftBraces != null && constExps != null && rightBraces != null &&
            leftBraces.size() == constExps.size() && constExps.size() == rightBraces.size()) {
            int len = leftBraces.size();
            for (int i = 0; i < len; i++) {
                sb.append(this.leftBraces.get(i).syntaxOutput());
                sb.append(this.constExps.get(i).syntaxOutput());
                sb.append(this.rightBraces.get(i).syntaxOutput());
            }
        }
        sb.append(this.eq.syntaxOutput());
        sb.append(this.initVal.syntaxOutput());
        return sb.toString();
    }
}
