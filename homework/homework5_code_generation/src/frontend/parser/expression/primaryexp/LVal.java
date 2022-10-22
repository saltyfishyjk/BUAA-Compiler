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
    private int dimension;

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
        if (this.symbolType != null) {
            if (this.symbolType.equals(SymbolType.CON) || this.symbolType.equals(SymbolType.VAR)) {
                this.dimension = 0;
            } else if (this.symbolType.equals(SymbolType.CON1) ||
                    this.symbolType.equals(SymbolType.VAR1)) {
                this.dimension = 1;
            } else {
                this.dimension = 2;
            }
        }
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
        return this.dimension - this.leftBrackets.size();
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public int getLineNum() {
        return this.ident.getLineNum();
    }
}