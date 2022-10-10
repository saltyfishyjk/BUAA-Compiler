package frontend.parser.expression.primaryexp;

import frontend.lexer.Token;
import frontend.parser.terminal.Ident;
import frontend.parser.expression.Exp;
import middle.symbol.SymbolType;

import java.util.ArrayList;

/**
 * 左值表达式
 */
public class LVal implements PrimaryExpEle {
    private final String name = "<LVal>";
    private Ident ident;
    private ArrayList<Token> leftBrackets;
    private ArrayList<Exp> exps;
    private ArrayList<Token> rightBrackets;
    private SymbolType symbolType;

    public LVal(Ident ident,
                ArrayList<Token> leftBrackets,
                ArrayList<Exp> exps,
                ArrayList<Token> rightBrackets) {
        this.ident = ident;
        this.leftBrackets = leftBrackets;
        this.exps = exps;
        this.rightBrackets = rightBrackets;
    }

    public LVal(Ident ident,
                ArrayList<Token> leftBrackets,
                ArrayList<Exp> exps,
                ArrayList<Token> rightBrackets,
                SymbolType symbolType) {
        this(ident, leftBrackets, exps, rightBrackets);
        this.symbolType = symbolType;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.syntaxOutput());
        if (leftBrackets != null && exps != null && rightBrackets != null &&
            leftBrackets.size() == exps.size() && exps.size() == rightBrackets.size()) {
            int len = leftBrackets.size();
            for (int i = 0; i < len; i++) {
                sb.append(leftBrackets.get(i).syntaxOutput());
                sb.append(exps.get(i).syntaxOutput());
                sb.append(rightBrackets.get(i).syntaxOutput());
            }
        }
        sb.append(this.name + "\n");
        return sb.toString();
    }

    @Override
    public int getDimension() {
        return leftBrackets.size();
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public int getLineNum() {
        return this.ident.getLineNum();
    }
}