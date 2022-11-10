package frontend.parser.expression.multiexp;

import frontend.lexer.Token;
import frontend.lexer.TokenType;
import middle.symbol.SymbolTable;

import java.util.ArrayList;

public class AddExp extends MultiExp<MulExp> {
    public AddExp(MulExp first, ArrayList<Token> tokens, ArrayList<MulExp> operands) {
        super(first, tokens, operands, "<AddExp>");
    }

    public int getDimension() {
        return this.getFirst().getDimension();
    }

    @Override
    public int calcNode(SymbolTable symbolTable) {
        int ret = this.getFirst().calcNode(symbolTable);
        int len = this.getOperands().size();
        for (int i = 0; i < len; i++) {
            if (this.getOperators().get(i).getType().equals(TokenType.PLUS)) {
                ret += this.getOperands().get(i).calcNode(symbolTable);
            } else if (this.getOperators().get(i).getType().equals(TokenType.MINU)) {
                ret -= this.getOperands().get(i).calcNode(symbolTable);
            } else {
                System.out.println("ERROR in AddExp calcNode : SHOULD NOT REACH HERE");
            }
        }
        return ret;
    }
}
