package frontend.parser.expression.unaryexp;

import frontend.lexer.TokenType;
import middle.symbol.SymbolTable;

public class UnaryExpOp implements UnaryExpEle {
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;

    public UnaryExpOp(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }

    @Override
    public String syntaxOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.unaryOp.syntaxOutput());
        sb.append(this.unaryExp.syntaxOutput());
        return sb.toString();
    }

    @Override
    public int getDimension() {
        return this.unaryExp.getDimension();
    }

    @Override
    public int calcNode(SymbolTable symbolTable) {
        if (this.unaryOp.getToken().getType().equals(TokenType.PLUS)) {
            return this.unaryExp.calcNode(symbolTable);
        } else if (this.unaryOp.getToken().getType().equals(TokenType.MINU)) {
            // 返回的应当是负数
            return (-1) * this.unaryExp.calcNode(symbolTable);
        } else {
            System.out.println("ERROR in UnaryExpOp.calcNode : should not calc such op");
            return 0;
        }
    }

    public UnaryOp getUnaryOp() {
        return unaryOp;
    }

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }
}
