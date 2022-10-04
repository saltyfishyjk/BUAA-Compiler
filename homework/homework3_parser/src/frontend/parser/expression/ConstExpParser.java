package frontend.parser.expression;

import frontend.lexer.TokenListIterator;
import frontend.parser.expression.multiexp.AddExp;
import frontend.parser.expression.multiexp.AddExpParser;

public class ConstExpParser {
    private TokenListIterator iterator;
    /* ConstExp Attributes */


    public ConstExp parseConstExp() {
        AddExpParser addExpParser = new AddExpParser(this.iterator);
        AddExp addExp = addExpParser.parseAddExp();
        ConstExp constExp = new ConstExp(addExp);
        return constExp;
    }
}
