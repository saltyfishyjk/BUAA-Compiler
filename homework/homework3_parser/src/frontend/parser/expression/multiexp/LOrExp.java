package frontend.parser.expression.multiexp;

import frontend.lexer.Token;

import java.util.ArrayList;

public class LOrExp extends MultiExp<LAndExp> {
    public LOrExp(LAndExp first, ArrayList<Token> operators,
                  ArrayList<LAndExp> operands) {
        super(first, operators, operands, "<LOrExp>");
    }
}
