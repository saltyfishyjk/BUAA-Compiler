package frontend.parser.expression.multiexp;

import frontend.lexer.Token;
import frontend.parser.expression.UnaryExp;

import java.util.ArrayList;

public class MulExp extends MultiExp<UnaryExp> {
    public MulExp(UnaryExp first, ArrayList<Token> operators,
                  ArrayList<UnaryExp> operands, String name) {
        super(first, operators, operands, name);
    }
}
