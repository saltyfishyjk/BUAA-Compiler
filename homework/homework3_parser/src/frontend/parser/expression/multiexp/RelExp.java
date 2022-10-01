package frontend.parser.expression.multiexp;

import frontend.lexer.Token;

import java.util.ArrayList;

public class RelExp extends MultiExp<AddExp> {
    public RelExp(AddExp first, ArrayList<Token> operators,
                  ArrayList<AddExp> operands, String name) {
        super(first, operators, operands, name);
    }
}
