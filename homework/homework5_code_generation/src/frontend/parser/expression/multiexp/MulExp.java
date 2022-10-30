package frontend.parser.expression.multiexp;

import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.expression.unaryexp.UnaryExp;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class MulExp extends MultiExp<UnaryExp> {
    public MulExp(UnaryExp first, ArrayList<Token> operators,
                  ArrayList<UnaryExp> operands) {
        super(first, operators, operands, "<MulExp>");
    }

    public int getDimension() {
        return this.getFirst().getDimension();
    }

    @Override
    public int calcNode(SymbolTable symbolTable) {
        int ret = this.getFirst().calcNode(symbolTable);
        int len = this.getOperands().size();
        for (int i = 0; i < len; i++) {
            if (this.getOperators().get(i).getType().equals(TokenType.MULT)) {
                ret = ret * this.getOperands().get(i).calcNode(symbolTable);
            } else if (this.getOperators().get(i).getType().equals(TokenType.DIV)) {
                ret = ret / this.getOperands().get(i).calcNode(symbolTable);
            } else if (this.getOperators().get(i).getType().equals(TokenType.MOD)) {
                ret = ret % this.getOperands().get(i).calcNode(symbolTable);
            } else {
                System.out.println("ERROR in MulExp.calcNode : SHOULD NOT REACH HERE");
            }
        }
        return ret;
    }
}
