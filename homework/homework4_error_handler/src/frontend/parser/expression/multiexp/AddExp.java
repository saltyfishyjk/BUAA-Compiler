package frontend.parser.expression.multiexp;

import frontend.lexer.Token;

import java.util.ArrayList;

public class AddExp extends MultiExp<MulExp> {
    public AddExp(MulExp first, ArrayList<Token> tokens, ArrayList<MulExp> operands) {
        super(first, tokens, operands, "<AddExp>");
    }

    public int getDimension() {
        return this.getFirst().getDimension();
    }
}
