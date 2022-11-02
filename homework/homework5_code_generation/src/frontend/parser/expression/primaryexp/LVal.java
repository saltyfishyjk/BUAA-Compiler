package frontend.parser.expression.primaryexp;

import frontend.lexer.Token;
import frontend.parser.terminal.Ident;
import frontend.parser.expression.Exp;
import middle.symbol.Symbol;
import middle.symbol.SymbolCon;
import middle.symbol.SymbolTable;
import middle.symbol.SymbolType;
import middle.symbol.SymbolVar;

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

    @Override
    public int calcNode(SymbolTable symbolTable) {
        Symbol symbol = symbolTable.getSymbol(this.ident.getName());
        /* TODO : 计算普通变量，一维数组和二维数组的值 */
        if (exps == null || exps.size() == 0) {
            // 零维
            if (symbol instanceof SymbolCon) {
                // 零维常量
                SymbolCon con = (SymbolCon)symbol;
                return con.getInitVal();
            } else if (symbol instanceof SymbolVar) {
                // 零维变量
                SymbolVar var = (SymbolVar)symbol;
                return var.getInitVal();
            }
        } else if (exps.size() == 1) {
            // 一维数组
            if (symbol instanceof SymbolCon) {
                // 一维常量数组
                SymbolCon con = (SymbolCon)symbol;
                int index = exps.get(0).calcNode(symbolTable);
                return con.getInitval1().get(index);
            } else if (symbol instanceof SymbolVar) {
                // 一维变量数组
                SymbolVar var = (SymbolVar)symbol;
                int index = exps.get(0).calcNode(symbolTable);
                return var.getInitVal1().get(index);

            }
        } else if (exps.size() == 2) {
            // 二维数组
            if (symbol instanceof SymbolCon) {
                // 二维常量数组
                SymbolCon con = (SymbolCon)symbol;
                int i = exps.get(0).calcNode(symbolTable);
                int j = exps.get(1).calcNode(symbolTable);
                return con.getInitval2().get(i).get(j);
            } else if (symbol instanceof SymbolVar) {
                // 二维变量数组
                SymbolVar var = (SymbolVar)symbol;
                int i = exps.get(0).calcNode(symbolTable);
                int j = exps.get(1).calcNode(symbolTable);
                return var.getInitVal2().get(i).get(j);
            }
        } else {
            System.out.println("ERROR in LVal : should not reach here");
            return 0;
        }
        return 0;
    }

    public String getName() {
        return this.ident.getName();
    }
}