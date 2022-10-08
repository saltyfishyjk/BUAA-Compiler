package frontend.parser.expression;

import frontend.lexer.TokenListIterator;
import frontend.parser.expression.multiexp.AddExp;
import frontend.parser.expression.multiexp.AddExpParser;

public class ConstExpParser {
    private TokenListIterator iterator;
    /* ConstExp Attributes */
    private AddExp addExp = null;

    public ConstExpParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public ConstExp parseConstExp() {
        AddExpParser addExpParser = new AddExpParser(this.iterator);
        this.addExp = addExpParser.parseAddExp();
        ConstExp constExp = new ConstExp(this.addExp);
        return constExp;
    }
}
