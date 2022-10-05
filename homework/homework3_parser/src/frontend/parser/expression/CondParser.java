package frontend.parser.expression;

import frontend.lexer.TokenListIterator;
import frontend.parser.expression.Cond;
import frontend.parser.expression.multiexp.LOrExp;
import frontend.parser.expression.multiexp.LOrExpParser;

public class CondParser {
    private TokenListIterator iterator;

    public CondParser(TokenListIterator iterator) {
        this.iterator = iterator;
    }

    public Cond parseCond() {
        LOrExpParser lorExpParser = new LOrExpParser(this.iterator);
        LOrExp lorExp = lorExpParser.parseLOrExp();
        Cond cond = new Cond(lorExp);
        return cond;
    }
}
