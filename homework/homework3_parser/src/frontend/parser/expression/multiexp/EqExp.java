package frontend.parser.expression.multiexp;

import frontend.lexer.Token;

import java.util.ArrayList;

public class EqExp extends MultiExp<RelExp> {
    public EqExp(RelExp first, ArrayList<Token> operators,
                 ArrayList<RelExp> operands) {
        super(first, operators, operands, "<EqExp>");
    }
}
