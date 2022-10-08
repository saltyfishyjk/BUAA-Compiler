package frontend.parser.expression;

import frontend.lexer.TokenListIterator;
import frontend.parser.expression.multiexp.AddExp;
import frontend.parser.expression.multiexp.AddExpParser;

public class ExpParser {
    private TokenListIterator iterator;

    public ExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public Exp parseExp() {
        AddExpParser addExpParser = new AddExpParser(this.iterator);
        AddExp addExp = addExpParser.parseAddExp();
        Exp exp = new Exp(addExp);
        return exp;
    }
}
